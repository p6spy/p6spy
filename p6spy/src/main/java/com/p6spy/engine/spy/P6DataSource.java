/*
 *
 * ====================================================================
 *
 * The P6Spy Software License, Version 1.1
 *
 * This license is derived and fully compatible with the Apache Software
 * license, see http://www.apache.org/LICENSE.txt
 *
 * Copyright (c) 2001-2002 Andy Martin, Ph.D. and Jeff Goke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement:
 * "The original concept and code base for P6Spy was conceived
 * and developed by Andy Martin, Ph.D. who generously contribued
 * the first complete release to the public under this license.
 * This product was due to the pioneering work of Andy
 * that began in December of 1995 developing applications that could
 * seamlessly be deployed with minimal effort but with dramatic results.
 * This code is maintained and extended by Jeff Goke and with the ideas
 * and contributions of other P6Spy contributors.
 * (http://www.p6spy.com)"
 * Alternately, this acknowlegement may appear in the software itself,
 * if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "P6Spy", "Jeff Goke", and "Andy Martin" must not be used
 * to endorse or promote products derived from this software without
 * prior written permission. For written permission, please contact
 * license@p6spy.com.
 *
 * 5. Products derived from this software may not be called "P6Spy"
 * nor may "P6Spy" appear in their names without prior written
 * permission of Jeff Goke and Andy Martin.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

/**
 * $Author: aarvesen $
 * $Revision: 1.5 $
 * $Date: 2003/08/07 19:07:21 $
 *
 * $Id: P6DataSource.java,v 1.5 2003/08/07 19:07:21 aarvesen Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/spy/P6DataSource.java,v $
 * $Log: P6DataSource.java,v $
 * Revision 1.5  2003/08/07 19:07:21  aarvesen
 * moved the existing constructor to the top of the file
 * added a new constructor for wrapping a datasource on instantiation
 *
 * Revision 1.4  2003/06/03 16:07:33  aarvesen
 * renamed setDataSourceName to setRealDataSource
 *
 * Revision 1.3  2003/01/30 23:35:22  dlukeparker
 *
 *
 * Added support for the javax.sql operations for pooled connections. This
 * is required for support of WebSphere.
 *
 * Finished implementation of com/p6spy/engine/spy/P6DataSource.java
 *
 * Added com/p6spy/engine/spy/P6ConnectionPoolDataSource.java
 * Added com/p6spy/engine/spy/P6DataSourceFactory.java
 * Added com/p6spy/engine/spy/P6PooledConnection.java
 * Added com/p6spy/engine/spy/P6ProxyConnection.java
 *
 * Made changes in spy.properties and com/p6spy/engine/common/P6SpyOptions.java
 * to enable datasource name, driver and properties setting. Also added support
 * for specifying the JNDI context for finding the real datasource.
 *
 * Revision 1.2  2002/12/20 00:29:45  aarvesen
 * removed the unneeded factory
 *
 * Revision 1.1  2002/12/19 23:51:45  aarvesen
 * Data Source implementation
 *
*/

package com.p6spy.engine.spy;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6SpyOptions;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class P6DataSource extends P6Base implements DataSource, Referenceable, Serializable {

    protected DataSource rds;
    protected String rdsName;

    static {
        // Normal use of a datastore means that the core has not
        // been initialized.
        initMethod();
    }

    /**
     * Default no-arg constructor for Serialization
     */

    public P6DataSource() {
        super(null);
    }

    public P6DataSource(DataSource source) {
        super(null);
        rds = source;
    }

    public static void initMethod() {
        P6SpyDriverCore.initMethod(P6SpyDriver.class.getName());
    }

    public String getRealDataSource() {
        return rdsName;
    }

    public void setRealDataSource(String inVar) {
        rdsName = inVar;
    }

    protected void bindDataSource() throws SQLException {
        // can be set when object is bound to JDNI, or
        // can be loaded from spy.properties
        if (rdsName == null) {
            rdsName = P6SpyOptions.getRealDataSource();
        }
        if (rdsName == null) {

            throw new SQLException("P6DataSource: no value for Real Data Source Name, cannot perform jndi lookup");
        }
        try {
            Hashtable env = null;
            String factory;
            if ((factory = P6SpyOptions.getJNDIContextFactory()) != null) {
                env = new Hashtable();
                env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
                String url = P6SpyOptions.getJNDIContextProviderURL();
                if (url != null) {
                    env.put(Context.PROVIDER_URL, url);
                }
                String custom = P6SpyOptions.getJNDIContextCustom();
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
            if (env != null) {
                ctx = new InitialContext(env);
            } else {
                ctx = new InitialContext();
            }
            rds = (DataSource) ctx.lookup(rdsName);

            // Set any properties that the spy.properties file contains
            // that are supported by set methods in this class

            String dsProps = P6SpyOptions.getRealDataSourceProperties();

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
                                        throw (new SQLException("spy.properties file includes" + " datasource property " + key + " for datasource "
                                            + rdsName + " but access is denied to method " + methodName));
                                    } catch (java.lang.reflect.InvocationTargetException e2) {
                                        throw (new SQLException("spy.properties file includes" + " datasource property " + key + " for datasource "
                                            + rdsName + " but call method " + methodName + " fails"));
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

        } catch (NamingException e) {
            throw new SQLException("P6DataSource: naming exception during jndi lookup of Real Data Source Name of '" + rdsName + "'. "
                + e.getMessage());
        }

        if (rds == null) {
            throw new SQLException("P6DataSource: jndi lookup for Real Data Source Name of '" + rdsName + "' failed, cannot bind named data source.");
        }
    }

    /**
     * Required method to support this class as a <CODE>Referenceable</CODE>.
     */

    public Reference getReference() throws NamingException {
        String FactoryName = "com.p6spy.engine.spy.P6DataSourceFactory";

        Reference Ref = new Reference(getClass().getName(), FactoryName, null);

        Ref.add(new StringRefAddr("dataSourceName", getRealDataSource()));
        return Ref;
    }

    public int getLoginTimeout() throws SQLException {
        if (rds == null) {
            bindDataSource();
        }
        return rds.getLoginTimeout();
    }

    public void setLoginTimeout(int inVar) throws SQLException {
        if (rds == null) {
            bindDataSource();
        }
        rds.setLoginTimeout(inVar);
    }

    public PrintWriter getLogWriter() throws SQLException {
        if (rds == null) {
            bindDataSource();
        }
        return rds.getLogWriter();
    }

    public void setLogWriter(PrintWriter inVar) throws SQLException {
        rds.setLogWriter(inVar);
    }

    public Connection getConnection() throws SQLException {
        if (rds == null) {
            bindDataSource();
        }
        return P6SpyDriverCore.wrapConnection(rds.getConnection());
    }

    public Connection getConnection(String username, String password) throws SQLException {
        if (rds == null) {
            bindDataSource();
        }
        return P6SpyDriverCore.wrapConnection(rds.getConnection(username, password));
    }

    /**
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return rds.isWrapperFor(iface);
    }

    /**
     * @param <T>
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return rds.unwrap(iface);
    }

    // since 1.7
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return rds.getParentLogger();
    }
}
