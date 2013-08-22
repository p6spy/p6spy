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
 * Description: Wrapper class for Connection
 *
 * $Author: cheechq $
 * $Revision: 1.4 $
 * $Date: 2003/06/03 19:20:25 $
 *
 * $Id: P6Connection.java,v 1.4 2003/06/03 19:20:25 cheechq Exp $
 * $Log: P6Connection.java,v $
 * Revision 1.4  2003/06/03 19:20:25  cheechq
 * removed unused imports
 *
 * Revision 1.3  2003/01/03 20:33:42  aarvesen
 * Added getJDBC() method to return the underlying jdbc object.
 *
 * Revision 1.2  2002/12/06 22:40:50  aarvesen
 * Extend P6Base.
 * New factory registration in the constructor.
 * Some jdk 1.4. hacks
 *
 * Revision 1.1  2002/05/24 07:31:13  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.7  2002/05/18 06:39:52  jeffgoke
 * Peter Laird added Outage detection.  Added junit tests for outage detection.
 * Fixed multi-driver tests.
 *
 * Revision 1.6  2002/05/16 04:58:40  jeffgoke
 * Viktor Szathmary added multi-driver support.
 * Rewrote P6SpyOptions to be easier to manage.
 * Fixed several bugs.
 *
 * Revision 1.5  2002/04/27 20:24:01  jeffgoke
 * added logging of commit statements and rollback statements
 *
 * Revision 1.4  2002/04/11 04:18:03  jeffgoke
 * fixed bug where callable & prepared were not passing their ancestors the correct constructor information
 *
 * Revision 1.3  2002/04/10 04:24:26  jeffgoke
 * added support for callable statements and fixed numerous bugs that allowed the real class to be returned
 *
 * Revision 1.2  2002/04/07 20:43:59  jeffgoke
 * fixed bug that caused null connection to return an empty connection instead of null.
 * added an option allowing the user to truncate.
 * added a release target to the build to create the release files.
 *
 * Revision 1.1.1.1  2002/04/07 04:52:25  jeffgoke
 * no message
 *
 * Revision 1.2  2001-08-02 07:52:43-05  andy
 * <>
 *
 * Revision 1.1  2001-07-30 23:03:31-05  andy
 * <>
 *
 * Revision 1.0  2001-07-30 17:46:22-05  andy
 * Initial revision
 *
 */

package com.p6spy.engine.spy;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.Executor;

public class P6Connection extends P6Base implements java.sql.Connection {


    protected static int counter=0;
    protected int id = counter++;
    protected Connection passthru;

    public P6Connection(P6Factory factory, Connection conn) throws SQLException {
        super(factory);
        this.passthru = conn;
    }

    public void setReadOnly(boolean p0) throws SQLException {
        passthru.setReadOnly(p0);
    }

    public void close() throws SQLException {
        passthru.close();
    }

    public int getId() {
        return this.id;
    }

    public boolean isClosed() throws SQLException {
        return(passthru.isClosed());
    }

    public boolean isReadOnly() throws SQLException {
        return(passthru.isReadOnly());
    }

    public Statement createStatement() throws SQLException {
        Statement statement = getP6Factory().getStatement(passthru.createStatement(), this);
        return(statement);
    }

    public Statement createStatement(int p0, int p1) throws SQLException {
        Statement statement = getP6Factory().getStatement(passthru.createStatement(p0,p1), this);
        return(statement);
    }

    public PreparedStatement prepareStatement(String p0) throws SQLException {
        return (getP6Factory().getPreparedStatement(passthru.prepareStatement(p0), this, p0));
    }

    public PreparedStatement prepareStatement(String p0, int p1, int p2) throws SQLException {
        return (getP6Factory().getPreparedStatement(passthru.prepareStatement(p0,p1,p2), this, p0));
    }

    public CallableStatement prepareCall(String p0) throws SQLException {
        return (getP6Factory().getCallableStatement(passthru.prepareCall(p0), this, p0));
    }

    public CallableStatement prepareCall(String p0, int p1, int p2) throws SQLException {
        return (getP6Factory().getCallableStatement(passthru.prepareCall(p0,p1,p2), this, p0));
    }

    public String nativeSQL(String p0) throws SQLException {
        return(passthru.nativeSQL(p0));
    }

    public void setAutoCommit(boolean p0) throws SQLException {
        passthru.setAutoCommit(p0);
    }

    public boolean getAutoCommit() throws SQLException {
        return(passthru.getAutoCommit());
    }

    public void commit() throws SQLException {
        passthru.commit();
    }

    public void rollback() throws SQLException {
        passthru.rollback();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return getP6Factory().getDatabaseMetaData(passthru.getMetaData(), this);
    }

    public void setCatalog(String p0) throws SQLException {
        passthru.setCatalog(p0);
    }

    public String getCatalog() throws SQLException {
        return(passthru.getCatalog());
    }

    public void setTransactionIsolation(int p0) throws SQLException {
        passthru.setTransactionIsolation(p0);
    }

    public int getTransactionIsolation() throws SQLException {
        return(passthru.getTransactionIsolation());
    }

    public SQLWarning getWarnings() throws SQLException {
        return(passthru.getWarnings());
    }

    public void clearWarnings() throws SQLException {
        passthru.clearWarnings();
    }

    public java.util.Map getTypeMap() throws SQLException {
        return(passthru.getTypeMap());
    }

    public void setTypeMap(java.util.Map p0) throws SQLException {
        passthru.setTypeMap(p0);
    }

    // Since JDK 1.4
    public void setHoldability(int p0) throws SQLException {
        passthru.setHoldability(p0);
    }

    // Since JDK 1.4
    public int getHoldability() throws SQLException {
        return(passthru.getHoldability());
    }

    // Since JDK 1.4
    public Savepoint setSavepoint() throws SQLException {
        return(passthru.setSavepoint());
    }

    // Since JDK 1.4
    public Savepoint setSavepoint(String p0) throws SQLException {
        return(passthru.setSavepoint(p0));
    }

    // Since JDK 1.4
    public void rollback(Savepoint p0) throws SQLException {
        passthru.rollback(p0);
    }

    // Since JDK 1.4
    public void releaseSavepoint(Savepoint p0) throws SQLException {
        passthru.releaseSavepoint(p0);
    }

    // Since JDK 1.4
    public Statement createStatement(int p0, int p1, int p2) throws SQLException {
        return getP6Factory().getStatement(passthru.createStatement(p0, p1, p2), this);
    }

    // Since JDK 1.4
    public PreparedStatement prepareStatement(String p0, int p1, int p2, int p3) throws SQLException {
        return (getP6Factory().getPreparedStatement(passthru.prepareStatement(p0, p1, p2, p3), this, p0));
    }

    // Since JDK 1.4
    public CallableStatement prepareCall(String p0, int p1, int p2, int p3) throws SQLException {
        return (getP6Factory().getCallableStatement(passthru.prepareCall(p0, p1, p2, p3), this, p0));
    }

    // Since JDK 1.4
    public PreparedStatement prepareStatement(String p0, int p1) throws SQLException {
        return (getP6Factory().getPreparedStatement(passthru.prepareStatement(p0, p1), this, p0));
    }

    // Since JDK 1.4
    public PreparedStatement prepareStatement(String p0, int p1[]) throws SQLException {
        return (getP6Factory().getPreparedStatement(passthru.prepareStatement(p0, p1), this, p0));
    }

    // Since JDK 1.4
    public PreparedStatement prepareStatement(String p0, String p1[]) throws SQLException {
        return (getP6Factory().getPreparedStatement(passthru.prepareStatement(p0, p1), this, p0));
    }

    /**
     * @param typeName
     * @param elements
     * @return
     * @throws SQLException
     * @see java.sql.Connection#createArrayOf(java.lang.String, java.lang.Object[])
     */
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return passthru.createArrayOf(typeName, elements);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Connection#createBlob()
     */
    public Blob createBlob() throws SQLException {
        return passthru.createBlob();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Connection#createClob()
     */
    public Clob createClob() throws SQLException {
        return passthru.createClob();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Connection#createNClob()
     */
    public NClob createNClob() throws SQLException {
        return passthru.createNClob();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Connection#createSQLXML()
     */
    public SQLXML createSQLXML() throws SQLException {
        return passthru.createSQLXML();
    }

    /**
     * @param typeName
     * @param attributes
     * @return
     * @throws SQLException
     * @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])
     */
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return passthru.createStruct(typeName, attributes);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Connection#getClientInfo()
     */
    public Properties getClientInfo() throws SQLException {
        return passthru.getClientInfo();
    }

    /**
     * @param name
     * @return
     * @throws SQLException
     * @see java.sql.Connection#getClientInfo(java.lang.String)
     */
    public String getClientInfo(String name) throws SQLException {
        return passthru.getClientInfo(name);
    }

    /**
     * @param timeout
     * @return
     * @throws SQLException
     * @see java.sql.Connection#isValid(int)
     */
    public boolean isValid(int timeout) throws SQLException {
        return passthru.isValid(timeout);
    }

    /**
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return passthru.isWrapperFor(iface);
    }

    /**
     * @param properties
     * @throws SQLClientInfoException
     * @see java.sql.Connection#setClientInfo(java.util.Properties)
     */
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        passthru.setClientInfo(properties);
    }

    /**
     * @param name
     * @param value
     * @throws SQLClientInfoException
     * @see java.sql.Connection#setClientInfo(java.lang.String, java.lang.String)
     */
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        passthru.setClientInfo(name, value);
    }

    /**
     * @param <T>
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return passthru.unwrap(iface);
    }

    /**
     * Returns the underlying JDBC object (in this case, a
     * java.sql.Connection)
     * @return the wrapped JDBC object
     */
    public Connection getJDBC() {
	Connection wrapped = (passthru instanceof P6Connection) ?
	    ((P6Connection) passthru).getJDBC() :
	    passthru;

	return wrapped;
    }

    // since 1.7
    @Override
    public void abort(final Executor executor) throws SQLException {
        passthru.abort(executor);
    }

    // since 1.7
    @Override
    public void setSchema(final String schema) throws SQLException {
        passthru.setSchema(schema);
    }

    // since 1.7
    @Override
    public String getSchema() throws SQLException {
        return passthru.getSchema();
    }

    // since 1.7
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        passthru.setNetworkTimeout(executor, milliseconds);
    }

    // since 1.7
    @Override
    public int getNetworkTimeout() throws SQLException {
        return passthru.getNetworkTimeout();
    }
}
