/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
 *
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
 */

package com.p6spy.engine.spy;

import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Wrapper;
import java.util.HashMap;
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

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.wrapper.ConnectionWrapper;

/**
 * P6Spy {@link DataSource} implementation.
 */
@SuppressWarnings("serial")
public class P6DataSource implements DataSource, ConnectionPoolDataSource, XADataSource, Referenceable, Serializable {

  protected CommonDataSource realDataSource;
  protected String rdsName;
  protected JdbcEventListenerFactory jdbcEventListenerFactory;
  
  /**
   * Default no-arg constructor for Serialization
   */
  public P6DataSource() {
  }

  /**
   * Create a P6Spy DataSource wrapping another DataSource.  This constructor is primarily used by dependency
   * injection frameworks.
   *
   * @param delegate the DataSource to wrap
   */
  public P6DataSource(DataSource delegate) {
    realDataSource = delegate;
  }

  /**
   * Returns the JNDI name of the real data source.
   *
   * @return the JNDI name of the DataSource to proxy
   */
  public String getRealDataSource() {
    return rdsName;
  }

  /**
   * Sets the JNDI name of the DataSource to proxy.
   *
   * @param jndiName
   */
  public void setRealDataSource(String jndiName) {
    rdsName = jndiName;
  }

  /**
   * Binds the JNDI DataSource to proxy.
   *
   * @throws SQLException
   */
  protected synchronized void bindDataSource() throws SQLException {
    // we'll check in the synchronized section again (to prevent unnecessary reinitialization)
    if (null != realDataSource) {
      return;
    }

    final P6SpyLoadableOptions options = P6SpyOptions.getActiveInstance();

    // can be set when object is bound to JNDI, or
    // can be loaded from spy.properties
    if (rdsName == null) {
      rdsName = options.getRealDataSource();
    }
    if (rdsName == null) {
      throw new SQLException("P6DataSource: no value for Real Data Source Name, cannot perform jndi lookup");
    }

    // setup environment for the JNDI lookup
    Hashtable<String, String> env = null;
    String factory;

    if ((factory = options.getJNDIContextFactory()) != null) {
      env = new Hashtable<String, String>();
      env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
      String url = options.getJNDIContextProviderURL();
      if (url != null) {
        env.put(Context.PROVIDER_URL, url);
      }
      String custom = options.getJNDIContextCustom();
      if( custom != null ) {
        env.putAll(parseDelimitedString(custom));
      }
    }

    // lookup the real data source
    InitialContext ctx;
    try {
      if (env != null) {
        ctx = new InitialContext(env);
      } else {
        ctx = new InitialContext();
      }
      realDataSource = (CommonDataSource) ctx.lookup(rdsName);
    } catch (NamingException e) {
      throw new SQLException("P6DataSource: naming exception during jndi lookup of Real Data Source Name of '" + rdsName + "'. "
        + e.getMessage(), e);
    }

    // Set any properties that the spy.properties file contains
    // that are supported by set methods in this class
    HashMap<String, String> props = parseDelimitedString(options.getRealDataSourceProperties());
    if (props != null) {
      setDataSourceProperties(props);
    }

    if (realDataSource == null) {
      throw new SQLException("P6DataSource: jndi lookup for Real Data Source Name of '" + rdsName + "' failed, cannot bind named data source.");
    }
  }

  private void setDataSourceProperties(HashMap<String, String> props) throws SQLException {
    HashMap<String, String> matchedProps = new HashMap<String, String>();

    Class<?> klass = realDataSource.getClass();

    // find the setter methods in the class, and
    // see if the datasource properties collected
    // from the spy.properties file contains any matching
    // name
    for (Method method : klass.getMethods()) {
      String methodName = method.getName();
      // see if the method is a setXXX
      if (methodName.startsWith("set")) {
        String propertyName = methodName.substring(3).toLowerCase();
        // found a setXXX method, so see if there is an XXX
        // property in the list read in from spy.properties.
        for (String key : props.keySet()) {
          // all checks are all lower case
          if (key.toLowerCase().equals(propertyName)) {
            try {
              // this is a parameter for the current method,
              // so find out which supported type the method
              // expects
              String value = props.get(key);
              Class<?>[] types = method.getParameterTypes();
              if (types[0].getName().equals(value.getClass().getName())) {
                // the method expects a string
                String[] args = new String[1];
                args[0] = value;
                P6LogQuery.debug("calling " + methodName + " on DataSource " + rdsName + " with " + value);
                method.invoke(realDataSource, args);
                matchedProps.put(key, value);
              } else if (types[0].isPrimitive() && "int".equals(types[0].getName())) {
                // the method expects an int, so we pass an Integer
                Integer[] args = new Integer[1];
                args[0] = Integer.valueOf(value);
                P6LogQuery.debug("calling " + methodName + " on DataSource " + rdsName + " with " + value);
                method.invoke(realDataSource, args);
                matchedProps.put(key, value);
              } else {
                P6LogQuery.debug("method " + methodName + " on DataSource " + rdsName + " matches property "
                  + propertyName + " but expects unsupported type " + types[0].getName());
                matchedProps.put(key, value);
              }
            } catch (IllegalAccessException e) {
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

    // log properties defined in spy.properties that were not found on the data source.
    for (String key : props.keySet()) {
      if (!matchedProps.containsKey(key)) {
        P6LogQuery.debug("spy.properties file includes" + " datasource property " + key + " for datasource " + rdsName
          + " but class " + klass.getName() + " has no method" + " by that name");
      }
    }
  }

  private HashMap<String, String> parseDelimitedString(final String delimitedString) {
    if (delimitedString == null) {
      return null;
    }

    HashMap<String, String> result = new HashMap<String, String>();

    StringTokenizer st = new StringTokenizer(delimitedString, ",", false);
    while (st.hasMoreElements()) {
      String pair = st.nextToken();
      StringTokenizer pst = new StringTokenizer(pair, ";", false);
      if (pst.hasMoreElements()) {
        String name = pst.nextToken();
        if (pst.hasMoreElements()) {
          String value = pst.nextToken();
          result.put(name, value);
        }
      }
    }

    return result;

  }

  @Override
  public Reference getReference() throws NamingException {
    final Reference reference = new Reference(getClass().getName(), P6DataSourceFactory.class.getName(), null);
    reference.add(new StringRefAddr("dataSourceName", getRealDataSource()));
    return reference;
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    if (realDataSource == null) {
      bindDataSource();
    }
    return realDataSource.getLoginTimeout();
  }

  @Override
  public void setLoginTimeout(int inVar) throws SQLException {
    if (realDataSource == null) {
      bindDataSource();
    }
    realDataSource.setLoginTimeout(inVar);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    if (realDataSource == null) {
      bindDataSource();
    }
    return realDataSource.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter inVar) throws SQLException {
    realDataSource.setLogWriter(inVar);
  }

  @Override
  public Connection getConnection() throws SQLException {
    if (realDataSource == null) {
      bindDataSource();
    }
    
    final long start = System.nanoTime();
    
    if (this.jdbcEventListenerFactory == null) {
      this.jdbcEventListenerFactory = new DefaultJdbcEventListenerFactory();
    }

    final Connection conn;
    final JdbcEventListener jdbcEventListener = this.jdbcEventListenerFactory.createJdbcEventListener();
    final ConnectionInformation connectionInformation = ConnectionInformation.fromDataSource(realDataSource);
    jdbcEventListener.onBeforeGetConnection(connectionInformation);
    try {
      conn = ((DataSource) realDataSource).getConnection();
      connectionInformation.setConnection(conn);
      connectionInformation.setTimeToGetConnectionNs(System.nanoTime() - start);
      jdbcEventListener.onAfterGetConnection(connectionInformation, null);
    } catch (SQLException e) {
      connectionInformation.setTimeToGetConnectionNs(System.nanoTime() - start);
      jdbcEventListener.onAfterGetConnection(connectionInformation, e);
      throw e;
    }

    return ConnectionWrapper.wrap(conn, jdbcEventListener, connectionInformation);
  }
  
  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    if (realDataSource == null) {
      bindDataSource();
    }
    
    final long start = System.nanoTime();
    
    if (this.jdbcEventListenerFactory == null) {
      this.jdbcEventListenerFactory = new DefaultJdbcEventListenerFactory();
    }

    final Connection conn;
    final JdbcEventListener jdbcEventListener = this.jdbcEventListenerFactory.createJdbcEventListener();
    final ConnectionInformation connectionInformation = ConnectionInformation.fromDataSource(realDataSource);
    jdbcEventListener.onBeforeGetConnection(connectionInformation);
    try {
      conn = ((DataSource) realDataSource).getConnection(username, password);
      connectionInformation.setConnection(conn);
      connectionInformation.setTimeToGetConnectionNs(System.nanoTime() - start);
      jdbcEventListener.onAfterGetConnection(connectionInformation, null);
    } catch (SQLException e) {
      connectionInformation.setTimeToGetConnectionNs(System.nanoTime() - start);
      jdbcEventListener.onAfterGetConnection(connectionInformation, e);
      throw e;
    }

    return ConnectionWrapper.wrap(conn, jdbcEventListener, connectionInformation);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return ((Wrapper) realDataSource).isWrapperFor(iface);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return ((DataSource) realDataSource).unwrap(iface);
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return realDataSource.getParentLogger();
  }

  @Override
  public PooledConnection getPooledConnection() throws SQLException {
    if (this.jdbcEventListenerFactory == null) {
      this.jdbcEventListenerFactory = new DefaultJdbcEventListenerFactory();
    }
    return new P6XAConnection(castRealDS(ConnectionPoolDataSource.class).getPooledConnection(), this.jdbcEventListenerFactory);
  }

  @Override
  public PooledConnection getPooledConnection(String user, String password) throws SQLException {
    if (this.jdbcEventListenerFactory == null) {
      this.jdbcEventListenerFactory = new DefaultJdbcEventListenerFactory();
    }
    return new P6XAConnection(castRealDS(ConnectionPoolDataSource.class).getPooledConnection(user, password), this.jdbcEventListenerFactory);
  }

  @Override
  public XAConnection getXAConnection() throws SQLException {
    if (this.jdbcEventListenerFactory == null) {
      this.jdbcEventListenerFactory = new DefaultJdbcEventListenerFactory();
    }
    return new P6XAConnection(castRealDS(XADataSource.class).getXAConnection(), this.jdbcEventListenerFactory);
  }

  @Override
  public XAConnection getXAConnection(String user, String password) throws SQLException {
    if (this.jdbcEventListenerFactory == null) {
      this.jdbcEventListenerFactory = new DefaultJdbcEventListenerFactory();
    }
    return new P6XAConnection(castRealDS(XADataSource.class).getXAConnection(user, password), this.jdbcEventListenerFactory);
  }

  @SuppressWarnings("unchecked")
  <T> T castRealDS(Class<T> iface) throws SQLException {
    if (realDataSource == null) {
      bindDataSource();
    }

    if (iface.isInstance(realDataSource)) {
      return ((T) realDataSource);
    } else if (isWrapperFor(iface)) {
      return unwrap(iface);
    } else {
      throw new IllegalStateException("realdatasource type not supported: " + realDataSource);
    }
  }

  public void setJdbcEventListenerFactory(JdbcEventListenerFactory jdbcEventListenerFactory) {
    this.jdbcEventListenerFactory = jdbcEventListenerFactory;
  }

}
