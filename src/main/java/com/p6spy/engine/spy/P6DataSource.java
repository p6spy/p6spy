/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.p6spy.engine.spy;

import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Wrapper;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.CommonDataSource;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import com.p6spy.engine.common.P6LogQuery;

@SuppressWarnings("serial")
public class P6DataSource implements DataSource, ConnectionPoolDataSource, XADataSource, Referenceable, Serializable {

  protected CommonDataSource rds;
  protected String rdsName;

  static {
    // make sure that the core has been initialized
    P6Core.initialize();
  }

  /**
   * Default no-arg constructor for Serialization
   */
  public P6DataSource() {
  }

  public P6DataSource(DataSource source) {
    rds = source;
  }

  public String getRealDataSource() {
    return rdsName;
  }

  public void setRealDataSource(String inVar) {
    rdsName = inVar;
  }

  protected void bindDataSource() throws SQLException {
    final P6SpyLoadableOptions options = P6SpyOptions.getActiveInstance();
    
    // can be set when object is bound to JDNI, or
    // can be loaded from spy.properties
    if (rdsName == null) {
      rdsName = options.getRealDataSource();
    }
    if (rdsName == null) {
      throw new SQLException("P6DataSource: no value for Real Data Source Name, cannot perform jndi lookup");
    }

    // lookup the real data source
    Hashtable env = null;
    String factory;

    if ((factory = options.getJNDIContextFactory()) != null) {
      env = new Hashtable();
      env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
      String url = options.getJNDIContextProviderURL();
      if (url != null) {
        env.put(Context.PROVIDER_URL, url);
      }
      String custom = options.getJNDIContextCustom();
      if (custom != null) {
        StringTokenizer st = new StringTokenizer(custom, ",", false);
        while (st.hasMoreElements()) {
          String pair = st.nextToken();
          StringTokenizer pst = new StringTokenizer(pair, ";", false);
          if (pst.hasMoreElements()) {
            String name = pst.nextToken();
            if (pst.hasMoreElements()) {
              String value = pst.nextToken();
              env.put(name, value);
            }
          }
        }
      }
    }
    InitialContext ctx;
      try {
        if (env != null) {
          ctx = new InitialContext(env);
        } else {
          ctx = new InitialContext();
        }
        rds = (CommonDataSource) ctx.lookup(rdsName);
      } catch (NamingException e) {
        throw new SQLException("P6DataSource: naming exception during jndi lookup of Real Data Source Name of '" + rdsName + "'. "
            + e.getMessage(), e);
      }

    // Set any properties that the spy.properties file contains
    // that are supported by set methods in this class

    String dsProps = options.getRealDataSourceProperties();

    if (dsProps != null) {
      Hashtable props = null;

      StringTokenizer st = new StringTokenizer(dsProps, ",", false);
      while (st.hasMoreElements()) {
        String pair = st.nextToken();
        StringTokenizer pst = new StringTokenizer(pair, ";", false);
        if (pst.hasMoreElements()) {
          String name = pst.nextToken();
          if (pst.hasMoreElements()) {
            String value = pst.nextToken();
            if (props == null) {
              props = new Hashtable();
            }
            props.put(name, value);
          }
        }
      }
      Hashtable matchedProps = new Hashtable();
      if (props != null) {
        Class klass = rds.getClass();

        // find the setter methods in the class, and
        // see if the datasource properties collected
        // from the spy.properties file contains any matching
        // name
        Method[] methods = klass.getMethods();
        for (int i = 0; methods != null && i < methods.length; i++) {
          Method method = methods[i];
          String methodName = method.getName();
          // see if the method is a setXXX
          if (methodName.startsWith("set")) {
            String propertyname = methodName.substring(3).toLowerCase();
            // found a setXXX method, so see if there is an XXX
            // property in the list read in from spy.properties.
            Enumeration keys = props.keys();
            while (keys.hasMoreElements()) {
              String key = (String) keys.nextElement();
              // all checks are all lower case
              if (key.toLowerCase().equals(propertyname)) {
                try {
                  // this is a parameter for the current method,
                  // so find out which supported type the method
                  // expects
                  String value = (String) props.get(key);
                  Class[] types = method.getParameterTypes();
                  if (types[0].getName().equals(value.getClass().getName())) {
                    // the method expects a string
                    String[] args = new String[1];
                    args[0] = value;
                    P6LogQuery.debug("calling " + methodName + " on DataSource " + rdsName + " with " + value);
                    method.invoke(rds, args);
                    matchedProps.put(key, value);
                  } else if (types[0].isPrimitive() && types[0].getName().equals("int")) {
                    // the method expects an int, so we pass an Integer
                    Integer[] args = new Integer[1];
                    args[0] = Integer.valueOf(value);
                    P6LogQuery.debug("calling " + methodName + " on DataSource " + rdsName + " with " + value);
                    method.invoke(rds, args);
                    matchedProps.put(key, value);
                  } else {
                    P6LogQuery.debug("method " + methodName + " on DataSource " + rdsName + " matches property "
                        + propertyname + " but expects unsupported type " + types[0].getName());
                    matchedProps.put(key, value);
                  }
                } catch (java.lang.IllegalAccessException e) {
                  throw new SQLException("spy.properties file includes" + " datasource property " + key + " for datasource "
                      + rdsName + " but access is denied to method " + methodName, e);
                } catch (java.lang.reflect.InvocationTargetException e) {
                  throw new SQLException("spy.properties file includes" + " datasource property " + key + " for datasource "
                      + rdsName + " but call method " + methodName + " fails", e);
                }
              }
            }
          }
        }

        Enumeration keys = props.keys();
        while (keys.hasMoreElements()) {

          String key = (String) keys.nextElement();

          if (!matchedProps.containsKey(key)) {
            P6LogQuery.debug("spy.properties file includes" + " datasource property " + key + " for datasource " + rdsName
                + " but class " + klass.getName() + " has no method" + " by that name");
          }
        }
      }
    }

    if (rds == null) {
      throw new SQLException("P6DataSource: jndi lookup for Real Data Source Name of '" + rdsName + "' failed, cannot bind named data source.");
    }
  }

  /**
   * Required method to support this class as a <CODE>Referenceable</CODE>.
   */
  @Override
  public Reference getReference() throws NamingException {
    String FactoryName = "com.p6spy.engine.spy.P6DataSourceFactory";

    Reference Ref = new Reference(getClass().getName(), FactoryName, null);

    Ref.add(new StringRefAddr("dataSourceName", getRealDataSource()));
    return Ref;
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    if (rds == null) {
      bindDataSource();
    }
    return rds.getLoginTimeout();
  }

  @Override
  public void setLoginTimeout(int inVar) throws SQLException {
    if (rds == null) {
      bindDataSource();
    }
    rds.setLoginTimeout(inVar);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    if (rds == null) {
      bindDataSource();
    }
    return rds.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter inVar) throws SQLException {
    rds.setLogWriter(inVar);
  }

  @Override
  public Connection getConnection() throws SQLException {
    if (rds == null) {
      bindDataSource();
    }
    return P6Core.wrapConnection(((DataSource) rds).getConnection());
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    if (rds == null) {
      bindDataSource();
    }
    return P6Core.wrapConnection(((DataSource) rds).getConnection(username, password));
  }

  /**
   * @param iface
   * @return
   * @throws SQLException
   * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return ((Wrapper) rds).isWrapperFor(iface);
  }

  /**
   * @param <T>
   * @param iface
   * @return
   * @throws SQLException
   * @see java.sql.Wrapper#unwrap(java.lang.Class)
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return ((DataSource) rds).unwrap(iface);
  }

  // since 1.7
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return rds.getParentLogger();
  }
  
  @Override
  public PooledConnection getPooledConnection() throws SQLException {
    if (rds == null) {
      bindDataSource();
    }

    PooledConnection pc = ((ConnectionPoolDataSource) rds).getPooledConnection();
    return new P6PooledConnection(pc);
  }
  
  @Override
  public PooledConnection getPooledConnection(String user, String password) throws SQLException {
    if (rds == null) {
      bindDataSource();
    }

    PooledConnection pc = ((ConnectionPoolDataSource) rds).getPooledConnection(user, password);
    return new P6PooledConnection(pc);
  }

  @Override
  public XAConnection getXAConnection() throws SQLException {
    if (rds == null) {
      bindDataSource();
    }
    
    if (rds instanceof XADataSource) {
      return new P6XAConnection(((XADataSource) rds).getXAConnection());  
    }
    
    throw new IllegalStateException("realdatasource type not supported: " + rds);
  }

  @Override
  public XAConnection getXAConnection(String user, String password) throws SQLException {
    if (rds == null) {
      bindDataSource();
    }
    
    if (rds instanceof XADataSource) {
      return new P6XAConnection(((XADataSource) rds).getXAConnection(user, password));  
    }
    
    throw new IllegalStateException("realdatasource type not supported: " + rds);
  }

}
