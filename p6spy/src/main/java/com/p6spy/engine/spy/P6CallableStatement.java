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
 * Description: JDBC Driver Extension implementing CallableStatement.
 *
 * $Author: bradleydot $
 * $Revision: 1.5 $
 * $Date: 2003/08/04 20:18:23 $
 *
 * $Id: P6CallableStatement.java,v 1.5 2003/08/04 20:18:23 bradleydot Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/spy/P6CallableStatement.java,v $
 * $Log: P6CallableStatement.java,v $
 * Revision 1.5  2003/08/04 20:18:23  bradleydot
 * Added calls to growvalues in registerOutParameters if they are outside the current array size.
 *
 * Revision 1.4  2003/06/03 19:20:24  cheechq
 * removed unused imports
 *
 * Revision 1.3  2003/01/03 20:33:42  aarvesen
 * Added getJDBC() method to return the underlying jdbc object.
 *
 * Revision 1.2  2002/12/06 22:40:13  aarvesen
 * Extend P6Base.
 * New factory registration in the constructor.
 *
 * Revision 1.1  2002/05/24 07:31:13  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.4  2002/04/18 06:54:39  jeffgoke
 * added batch statement logging support
 *
 * Revision 1.3  2002/04/15 05:13:32  jeffgoke
 * Simon Sadedin added timing support.  Fixed bug where batch execute was not
 * getting logged.  Added result set timing.  Updated the log format to include
 * categories, and updated options to control the categories.  Updated
 * documentation.
 *
 * Revision 1.2  2002/04/11 04:18:03  jeffgoke
 * fixed bug where callable & prepared were not passing their ancestors the correct constructor information
 *
 * Revision 1.1  2002/04/10 04:24:26  jeffgoke
 * added support for callable statements and fixed numerous bugs that allowed the real class to be returned
 *
 * Revision 1.1.1.1  2002/04/07 04:52:25  jeffgoke
 * no message
 *
 * Revision 1.2  2001-08-05 09:16:04-05  andy
 * final version on the website
 *
 * Revision 1.1  2001-08-02 07:52:43-05  andy
 * <>
 *
 * Revision 1.0  2001-08-02 06:37:42-05  andy
 * Initial revision
 *
 *
 */

package com.p6spy.engine.spy;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

public class P6CallableStatement extends P6PreparedStatement implements java.sql.CallableStatement {


    protected CallableStatement callStmtPassthru;
    protected String callableQuery;

    public P6CallableStatement(P6Factory factory, CallableStatement statement, P6Connection conn, String query) {
        super(factory, statement, conn, query);
        this.callableQuery = query;
        this.callStmtPassthru = statement;
    }

    public String getString(int p0) throws SQLException {
        return callStmtPassthru.getString(p0);
    }

    public void registerOutParameter(int p0, int p1) throws SQLException {
        if (p0>=values.length){
          growValues(p0);
        }
        callStmtPassthru.registerOutParameter(p0, p1);
    }

    public void registerOutParameter(int p0, int p1, int p2) throws SQLException {
        if (p0>=values.length){
          growValues(p0);
        }
        callStmtPassthru.registerOutParameter(p0, p1, p2);
    }

    public void registerOutParameter(int p0, int p1, String p2) throws SQLException {
        if (p0>=values.length){
          growValues(p0);
        }
        callStmtPassthru.registerOutParameter(p0, p1, p2);
    }

    public boolean wasNull() throws SQLException {
        return callStmtPassthru.wasNull();
    }

    public java.sql.Array getArray(int p0) throws java.sql.SQLException {
        return getP6Factory().getArray(callStmtPassthru.getArray(p0),this,callableQuery,getQueryFromPreparedStatement());
    }

    public java.math.BigDecimal getBigDecimal(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getBigDecimal(p0);
    }

    public java.math.BigDecimal getBigDecimal(int p0, int p1) throws java.sql.SQLException {
        return callStmtPassthru.getBigDecimal(p0,p1);
    }

    public java.sql.Blob getBlob(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getBlob(p0);
    }

    public boolean getBoolean(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getBoolean(p0);
    }

    public byte getByte(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getByte(p0);
    }

    public byte[] getBytes(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getBytes(p0);
    }

    public java.sql.Clob getClob(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getClob(p0);
    }

    public java.sql.Date getDate(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getDate(p0);
    }

    public java.sql.Date getDate(int p0, java.util.Calendar calendar) throws java.sql.SQLException {
        return callStmtPassthru.getDate(p0,calendar);
    }

    public double getDouble(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getDouble(p0);
    }

    public float getFloat(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getFloat(p0);
    }

    public int getInt(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getInt(p0);
    }

    public long getLong(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getLong(p0);
    }

    public Object getObject(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getObject(p0);
    }

    public Object getObject(int p0, java.util.Map p1) throws java.sql.SQLException {
        return callStmtPassthru.getObject(p0, p1);
    }

    public java.sql.Ref getRef(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getRef(p0);
    }

    public short getShort(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getShort(p0);
    }

    public java.sql.Time getTime(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getTime(p0);
    }

    public java.sql.Time getTime(int p0, java.util.Calendar p1) throws java.sql.SQLException {
        return callStmtPassthru.getTime(p0,p1);
    }

    public java.sql.Timestamp getTimestamp(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getTimestamp(p0);
    }

    public java.sql.Timestamp getTimestamp(int p0, java.util.Calendar p1) throws java.sql.SQLException {
        return callStmtPassthru.getTimestamp(p0,p1);
    }

    // Since JDK 1.4
    public void registerOutParameter(String p0, int p1) throws java.sql.SQLException {
        callStmtPassthru.registerOutParameter(p0, p1);
    }

    // Since JDK 1.4
    public void registerOutParameter(String p0, int p1, int p2) throws java.sql.SQLException {
        callStmtPassthru.registerOutParameter(p0, p1, p2);
    }

    // Since JDK 1.4
    public void registerOutParameter(String p0, int p1, String p2) throws java.sql.SQLException {
        callStmtPassthru.registerOutParameter(p0, p1, p2);
    }

    // Since JDK 1.4
    public java.net.URL getURL(int p0) throws java.sql.SQLException {
        return(callStmtPassthru.getURL(p0));
    }

    // Since JDK 1.4
    public void setURL(String p0, java.net.URL p1) throws java.sql.SQLException {
        callStmtPassthru.setURL(p0, p1);
    }

    // Since JDK 1.4
    public void setNull(String p0, int p1) throws java.sql.SQLException {
        callStmtPassthru.setNull(p0, p1);
    }

    // Since JDK 1.4
    public void setBoolean(String p0, boolean p1) throws java.sql.SQLException {
        callStmtPassthru.setBoolean(p0, p1);
    }

    // Since JDK 1.4
    public void setByte(String p0, byte p1) throws java.sql.SQLException {
        callStmtPassthru.setByte(p0, p1);
    }

    // Since JDK 1.4
    public void setShort(String p0, short p1) throws java.sql.SQLException {
        callStmtPassthru.setShort(p0, p1);
    }

    // Since JDK 1.4
    public void setInt(String p0, int p1) throws java.sql.SQLException {
        callStmtPassthru.setInt(p0, p1);
    }

    // Since JDK 1.4
    public void setLong(String p0, long p1) throws java.sql.SQLException {
        callStmtPassthru.setLong(p0, p1);
    }

    // Since JDK 1.4
    public void setFloat(String p0, float p1) throws java.sql.SQLException {
        callStmtPassthru.setFloat(p0, p1);
    }

    // Since JDK 1.4
    public void setDouble(String p0, double p1) throws java.sql.SQLException {
        callStmtPassthru.setDouble(p0, p1);
    }

    // Since JDK 1.4
    public void setBigDecimal(String p0, java.math.BigDecimal p1) throws java.sql.SQLException {
        callStmtPassthru.setBigDecimal(p0, p1);
    }

    // Since JDK 1.4
    public void setString(String p0, String p1) throws java.sql.SQLException {
        callStmtPassthru.setString(p0, p1);
    }

    // Since JDK 1.4
    public void setBytes(String p0, byte p1[]) throws java.sql.SQLException {
        callStmtPassthru.setBytes(p0, p1);
    }

    // Since JDK 1.4
    public void setDate(String p0, java.sql.Date p1) throws java.sql.SQLException {
        callStmtPassthru.setDate(p0, p1);
    }

    // Since JDK 1.4
    public void setTime(String p0, java.sql.Time p1) throws java.sql.SQLException {
        callStmtPassthru.setTime(p0, p1);
    }

    // Since JDK 1.4
    public void setTimestamp(String p0, java.sql.Timestamp p1) throws java.sql.SQLException {
        callStmtPassthru.setTimestamp(p0, p1);
    }

    // Since JDK 1.4
    public void setAsciiStream(String p0, java.io.InputStream p1, int p2) throws java.sql.SQLException {
        callStmtPassthru.setAsciiStream(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setBinaryStream(String p0, java.io.InputStream p1, int p2) throws java.sql.SQLException {
        callStmtPassthru.setBinaryStream(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setObject(String p0, Object p1, int p2, int p3) throws java.sql.SQLException {
        callStmtPassthru.setObject(p0, p1, p2, p3);
    }

    // Since JDK 1.4
    public void setObject(String p0, Object p1, int p2) throws java.sql.SQLException {
        callStmtPassthru.setObject(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setObject(String p0, Object p1) throws java.sql.SQLException {
        callStmtPassthru.setObject(p0, p1);
    }

    // Since JDK 1.4
    public void setCharacterStream(String p0, java.io.Reader p1, int p2) throws java.sql.SQLException {
        callStmtPassthru.setCharacterStream(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setDate(String p0, java.sql.Date p1, java.util.Calendar p2) throws java.sql.SQLException {
        callStmtPassthru.setDate(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setTime(String p0, java.sql.Time p1, java.util.Calendar p2) throws java.sql.SQLException {
        callStmtPassthru.setTime(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setTimestamp(String p0, java.sql.Timestamp p1, java.util.Calendar p2) throws java.sql.SQLException {
        callStmtPassthru.setTimestamp(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setNull(String p0, int p1, String p2) throws java.sql.SQLException {
        callStmtPassthru.setNull(p0, p1, p2);
    }

    // Since JDK 1.4
    public String getString(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getString(p0));
    }

    // Since JDK 1.4
    public boolean getBoolean(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getBoolean(p0));
    }

    // Since JDK 1.4
    public byte getByte(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getByte(p0));
    }

    // Since JDK 1.4
    public short getShort(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getShort(p0));
    }

    // Since JDK 1.4
    public int getInt(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getInt(p0));
    }

    // Since JDK 1.4
    public long getLong(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getLong(p0));
    }

    // Since JDK 1.4
    public float getFloat(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getFloat(p0));
    }

    // Since JDK 1.4
    public double getDouble(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getDouble(p0));
    }

    // Since JDK 1.4
    public byte[] getBytes(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getBytes(p0));
    }

    // Since JDK 1.4
    public java.sql.Date getDate(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getDate(p0));
    }

    // Since JDK 1.4
    public java.sql.Time getTime(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getTime(p0));
    }

    // Since JDK 1.4
    public java.sql.Timestamp getTimestamp(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getTimestamp(p0));
    }

    // Since JDK 1.4
    public Object getObject(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getObject(p0));
    }

    // Since JDK 1.4
    public java.math.BigDecimal getBigDecimal(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getBigDecimal(p0));
    }

    // Since JDK 1.4
    public Object getObject(String p0, java.util.Map p1) throws java.sql.SQLException {
        return(callStmtPassthru.getObject(p0, p1));
    }

    // Since JDK 1.4
    public java.sql.Ref getRef(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getRef(p0));
    }

    // Since JDK 1.4
    public java.sql.Blob getBlob(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getBlob(p0));
    }

    // Since JDK 1.4
    public java.sql.Clob getClob(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getClob(p0));
    }

    // Since JDK 1.4
    public java.sql.Array getArray(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getArray(p0));
    }

    // Since JDK 1.4
    public java.sql.Date getDate(String p0, java.util.Calendar p1) throws java.sql.SQLException {
        return(callStmtPassthru.getDate(p0, p1));
    }

    // Since JDK 1.4
    public java.sql.Time getTime(String p0, java.util.Calendar p1) throws java.sql.SQLException {
        return(callStmtPassthru.getTime(p0, p1));
    }

    // Since JDK 1.4
    public java.sql.Timestamp getTimestamp(String p0, java.util.Calendar p1) throws java.sql.SQLException {
        return(callStmtPassthru.getTimestamp(p0, p1));
    }

    // Since JDK 1.4
    public java.net.URL getURL(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getURL(p0));
    }
    /**
     * Returns the underlying JDBC object (in this case, a
     * java.sql.CallableStatement).
     * <p>
     * The returned object is a java.sql.Statement due
     * to inheritance reasons, so you'll need to cast
     * appropriately.
     *
     * @return the wrapped JDBC object
     */
    @Override
    public Statement getJDBC() {
	Statement wrapped = (callStmtPassthru instanceof P6Statement) ?
	    ((P6Statement) callStmtPassthru).getJDBC() :
	    callStmtPassthru;

	return wrapped;
    }

    /**
     * @throws SQLException
     * @see java.sql.PreparedStatement#addBatch()
     */
    @Override
    public void addBatch() throws SQLException {
        callStmtPassthru.addBatch();
    }

    /**
     * @param sql
     * @throws SQLException
     * @see java.sql.Statement#addBatch(java.lang.String)
     */
    @Override
    public void addBatch(String sql) throws SQLException {
        callStmtPassthru.addBatch(sql);
    }

    /**
     * @throws SQLException
     * @see java.sql.Statement#cancel()
     */
    @Override
    public void cancel() throws SQLException {
        callStmtPassthru.cancel();
    }

    /**
     * @throws SQLException
     * @see java.sql.Statement#clearBatch()
     */
    @Override
    public void clearBatch() throws SQLException {
        callStmtPassthru.clearBatch();
    }

    /**
     * @throws SQLException
     * @see java.sql.PreparedStatement#clearParameters()
     */
    @Override
    public void clearParameters() throws SQLException {
        callStmtPassthru.clearParameters();
    }

    /**
     * @throws SQLException
     * @see java.sql.Statement#clearWarnings()
     */
    @Override
    public void clearWarnings() throws SQLException {
        callStmtPassthru.clearWarnings();
    }

    /**
     * @throws SQLException
     * @see java.sql.Statement#close()
     */
    @Override
    public void close() throws SQLException {
        callStmtPassthru.close();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.PreparedStatement#execute()
     */
    @Override
    public boolean execute() throws SQLException {
        return callStmtPassthru.execute();
    }

    /**
     * @param sql
     * @param autoGeneratedKeys
     * @return
     * @throws SQLException
     * @see java.sql.Statement#execute(java.lang.String, int)
     */
    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return callStmtPassthru.execute(sql, autoGeneratedKeys);
    }

    /**
     * @param sql
     * @param columnIndexes
     * @return
     * @throws SQLException
     * @see java.sql.Statement#execute(java.lang.String, int[])
     */
    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return callStmtPassthru.execute(sql, columnIndexes);
    }

    /**
     * @param sql
     * @param columnNames
     * @return
     * @throws SQLException
     * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
     */
    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return callStmtPassthru.execute(sql, columnNames);
    }

    /**
     * @param sql
     * @return
     * @throws SQLException
     * @see java.sql.Statement#execute(java.lang.String)
     */
    @Override
    public boolean execute(String sql) throws SQLException {
        return callStmtPassthru.execute(sql);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#executeBatch()
     */
    @Override
    public int[] executeBatch() throws SQLException {
        return callStmtPassthru.executeBatch();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.PreparedStatement#executeQuery()
     */
    @Override
    public ResultSet executeQuery() throws SQLException {
        return callStmtPassthru.executeQuery();
    }

    /**
     * @param sql
     * @return
     * @throws SQLException
     * @see java.sql.Statement#executeQuery(java.lang.String)
     */
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return callStmtPassthru.executeQuery(sql);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.PreparedStatement#executeUpdate()
     */
    @Override
    public int executeUpdate() throws SQLException {
        return callStmtPassthru.executeUpdate();
    }

    /**
     * @param sql
     * @param autoGeneratedKeys
     * @return
     * @throws SQLException
     * @see java.sql.Statement#executeUpdate(java.lang.String, int)
     */
    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return callStmtPassthru.executeUpdate(sql, autoGeneratedKeys);
    }

    /**
     * @param sql
     * @param columnIndexes
     * @return
     * @throws SQLException
     * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
     */
    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return callStmtPassthru.executeUpdate(sql, columnIndexes);
    }

    /**
     * @param sql
     * @param columnNames
     * @return
     * @throws SQLException
     * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
     */
    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return callStmtPassthru.executeUpdate(sql, columnNames);
    }

    /**
     * @param sql
     * @return
     * @throws SQLException
     * @see java.sql.Statement#executeUpdate(java.lang.String)
     */
    @Override
    public int executeUpdate(String sql) throws SQLException {
        return callStmtPassthru.executeUpdate(sql);
    }

    /**
     * @param parameterIndex
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getCharacterStream(int)
     */
    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        return callStmtPassthru.getCharacterStream(parameterIndex);
    }

    /**
     * @param parameterName
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getCharacterStream(java.lang.String)
     */
    public Reader getCharacterStream(String parameterName) throws SQLException {
        return callStmtPassthru.getCharacterStream(parameterName);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getConnection()
     */
    @Override
    public Connection getConnection() throws SQLException {
        return callStmtPassthru.getConnection();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getFetchDirection()
     */
    @Override
    public int getFetchDirection() throws SQLException {
        return callStmtPassthru.getFetchDirection();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getFetchSize()
     */
    @Override
    public int getFetchSize() throws SQLException {
        return callStmtPassthru.getFetchSize();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getGeneratedKeys()
     */
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return callStmtPassthru.getGeneratedKeys();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getMaxFieldSize()
     */
    @Override
    public int getMaxFieldSize() throws SQLException {
        return callStmtPassthru.getMaxFieldSize();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getMaxRows()
     */
    @Override
    public int getMaxRows() throws SQLException {
        return callStmtPassthru.getMaxRows();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.PreparedStatement#getMetaData()
     */
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return callStmtPassthru.getMetaData();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getMoreResults()
     */
    @Override
    public boolean getMoreResults() throws SQLException {
        return callStmtPassthru.getMoreResults();
    }

    /**
     * @param current
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getMoreResults(int)
     */
    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return callStmtPassthru.getMoreResults(current);
    }

    /**
     * @param parameterIndex
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getNCharacterStream(int)
     */
    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return callStmtPassthru.getNCharacterStream(parameterIndex);
    }

    /**
     * @param parameterName
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getNCharacterStream(java.lang.String)
     */
    public Reader getNCharacterStream(String parameterName) throws SQLException {
        return callStmtPassthru.getNCharacterStream(parameterName);
    }

    /**
     * @param parameterIndex
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getNClob(int)
     */
    public NClob getNClob(int parameterIndex) throws SQLException {
        return callStmtPassthru.getNClob(parameterIndex);
    }

    /**
     * @param parameterName
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getNClob(java.lang.String)
     */
    public NClob getNClob(String parameterName) throws SQLException {
        return callStmtPassthru.getNClob(parameterName);
    }

    /**
     * @param parameterIndex
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getNString(int)
     */
    public String getNString(int parameterIndex) throws SQLException {
        return callStmtPassthru.getNString(parameterIndex);
    }

    /**
     * @param parameterName
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getNString(java.lang.String)
     */
    public String getNString(String parameterName) throws SQLException {
        return callStmtPassthru.getNString(parameterName);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.PreparedStatement#getParameterMetaData()
     */
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return callStmtPassthru.getParameterMetaData();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getQueryTimeout()
     */
    @Override
    public int getQueryTimeout() throws SQLException {
        return callStmtPassthru.getQueryTimeout();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getResultSet()
     */
    @Override
    public ResultSet getResultSet() throws SQLException {
        return callStmtPassthru.getResultSet();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getResultSetConcurrency()
     */
    @Override
    public int getResultSetConcurrency() throws SQLException {
        return callStmtPassthru.getResultSetConcurrency();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getResultSetHoldability()
     */
    @Override
    public int getResultSetHoldability() throws SQLException {
        return callStmtPassthru.getResultSetHoldability();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getResultSetType()
     */
    @Override
    public int getResultSetType() throws SQLException {
        return callStmtPassthru.getResultSetType();
    }

    /**
     * @param parameterIndex
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getRowId(int)
     */
    public RowId getRowId(int parameterIndex) throws SQLException {
        return callStmtPassthru.getRowId(parameterIndex);
    }

    /**
     * @param parameterName
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getRowId(java.lang.String)
     */
    public RowId getRowId(String parameterName) throws SQLException {
        return callStmtPassthru.getRowId(parameterName);
    }

    /**
     * @param parameterIndex
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getSQLXML(int)
     */
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return callStmtPassthru.getSQLXML(parameterIndex);
    }

    /**
     * @param parameterName
     * @return
     * @throws SQLException
     * @see java.sql.CallableStatement#getSQLXML(java.lang.String)
     */
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return callStmtPassthru.getSQLXML(parameterName);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getUpdateCount()
     */
    @Override
    public int getUpdateCount() throws SQLException {
        return callStmtPassthru.getUpdateCount();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getWarnings()
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return callStmtPassthru.getWarnings();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#isClosed()
     */
    public boolean isClosed() throws SQLException {
        return callStmtPassthru.isClosed();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#isPoolable()
     */
    public boolean isPoolable() throws SQLException {
        return callStmtPassthru.isPoolable();
    }

    /**
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return callStmtPassthru.isWrapperFor(iface);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
     */
    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        callStmtPassthru.setArray(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
     */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        callStmtPassthru.setAsciiStream(parameterIndex, x, length);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, long)
     */
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        callStmtPassthru.setAsciiStream(parameterIndex, x, length);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream)
     */
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        callStmtPassthru.setAsciiStream(parameterIndex, x);
    }

    /**
     * @param parameterName
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream, long)
     */
    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        callStmtPassthru.setAsciiStream(parameterName, x, length);
    }

    /**
     * @param parameterName
     * @param x
     * @throws SQLException
     * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream)
     */
    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        callStmtPassthru.setAsciiStream(parameterName, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
     */
    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        callStmtPassthru.setBigDecimal(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
     */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        callStmtPassthru.setBinaryStream(parameterIndex, x, length);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, long)
     */
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        callStmtPassthru.setBinaryStream(parameterIndex, x, length);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream)
     */
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        callStmtPassthru.setBinaryStream(parameterIndex, x);
    }

    /**
     * @param parameterName
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream, long)
     */
    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        callStmtPassthru.setBinaryStream(parameterName, x, length);
    }

    /**
     * @param parameterName
     * @param x
     * @throws SQLException
     * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream)
     */
    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        callStmtPassthru.setBinaryStream(parameterName, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
     */
    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        callStmtPassthru.setBlob(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param inputStream
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream, long)
     */
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        callStmtPassthru.setBlob(parameterIndex, inputStream, length);
    }

    /**
     * @param parameterIndex
     * @param inputStream
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream)
     */
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        callStmtPassthru.setBlob(parameterIndex, inputStream);
    }

    /**
     * @param parameterName
     * @param x
     * @throws SQLException
     * @see java.sql.CallableStatement#setBlob(java.lang.String, java.sql.Blob)
     */
    public void setBlob(String parameterName, Blob x) throws SQLException {
        callStmtPassthru.setBlob(parameterName, x);
    }

    /**
     * @param parameterName
     * @param inputStream
     * @param length
     * @throws SQLException
     * @see java.sql.CallableStatement#setBlob(java.lang.String, java.io.InputStream, long)
     */
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        callStmtPassthru.setBlob(parameterName, inputStream, length);
    }

    /**
     * @param parameterName
     * @param inputStream
     * @throws SQLException
     * @see java.sql.CallableStatement#setBlob(java.lang.String, java.io.InputStream)
     */
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        callStmtPassthru.setBlob(parameterName, inputStream);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBoolean(int, boolean)
     */
    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        callStmtPassthru.setBoolean(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setByte(int, byte)
     */
    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        callStmtPassthru.setByte(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBytes(int, byte[])
     */
    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        callStmtPassthru.setBytes(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
     */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        callStmtPassthru.setCharacterStream(parameterIndex, reader, length);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, long)
     */
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        callStmtPassthru.setCharacterStream(parameterIndex, reader, length);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @throws SQLException
     * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader)
     */
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        callStmtPassthru.setCharacterStream(parameterIndex, reader);
    }

    /**
     * @param parameterName
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader, long)
     */
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        callStmtPassthru.setCharacterStream(parameterName, reader, length);
    }

    /**
     * @param parameterName
     * @param reader
     * @throws SQLException
     * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader)
     */
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        callStmtPassthru.setCharacterStream(parameterName, reader);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
     */
    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        callStmtPassthru.setClob(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setClob(int, java.io.Reader, long)
     */
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        callStmtPassthru.setClob(parameterIndex, reader, length);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @throws SQLException
     * @see java.sql.PreparedStatement#setClob(int, java.io.Reader)
     */
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        callStmtPassthru.setClob(parameterIndex, reader);
    }

    /**
     * @param parameterName
     * @param x
     * @throws SQLException
     * @see java.sql.CallableStatement#setClob(java.lang.String, java.sql.Clob)
     */
    public void setClob(String parameterName, Clob x) throws SQLException {
        callStmtPassthru.setClob(parameterName, x);
    }

    /**
     * @param parameterName
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.CallableStatement#setClob(java.lang.String, java.io.Reader, long)
     */
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        callStmtPassthru.setClob(parameterName, reader, length);
    }

    /**
     * @param parameterName
     * @param reader
     * @throws SQLException
     * @see java.sql.CallableStatement#setClob(java.lang.String, java.io.Reader)
     */
    public void setClob(String parameterName, Reader reader) throws SQLException {
        callStmtPassthru.setClob(parameterName, reader);
    }

    /**
     * @param name
     * @throws SQLException
     * @see java.sql.Statement#setCursorName(java.lang.String)
     */
    @Override
    public void setCursorName(String name) throws SQLException {
        callStmtPassthru.setCursorName(name);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param cal
     * @throws SQLException
     * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
     */
    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        callStmtPassthru.setDate(parameterIndex, x, cal);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
     */
    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        callStmtPassthru.setDate(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setDouble(int, double)
     */
    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        callStmtPassthru.setDouble(parameterIndex, x);
    }

    /**
     * @param enable
     * @throws SQLException
     * @see java.sql.Statement#setEscapeProcessing(boolean)
     */
    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        callStmtPassthru.setEscapeProcessing(enable);
    }

    /**
     * @param direction
     * @throws SQLException
     * @see java.sql.Statement#setFetchDirection(int)
     */
    @Override
    public void setFetchDirection(int direction) throws SQLException {
        callStmtPassthru.setFetchDirection(direction);
    }

    /**
     * @param rows
     * @throws SQLException
     * @see java.sql.Statement#setFetchSize(int)
     */
    @Override
    public void setFetchSize(int rows) throws SQLException {
        callStmtPassthru.setFetchSize(rows);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setFloat(int, float)
     */
    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        callStmtPassthru.setFloat(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setInt(int, int)
     */
    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        callStmtPassthru.setInt(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setLong(int, long)
     */
    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        callStmtPassthru.setLong(parameterIndex, x);
    }

    /**
     * @param max
     * @throws SQLException
     * @see java.sql.Statement#setMaxFieldSize(int)
     */
    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        callStmtPassthru.setMaxFieldSize(max);
    }

    /**
     * @param max
     * @throws SQLException
     * @see java.sql.Statement#setMaxRows(int)
     */
    @Override
    public void setMaxRows(int max) throws SQLException {
        callStmtPassthru.setMaxRows(max);
    }

    /**
     * @param parameterIndex
     * @param value
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader, long)
     */
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        callStmtPassthru.setNCharacterStream(parameterIndex, value, length);
    }

    /**
     * @param parameterIndex
     * @param value
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader)
     */
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        callStmtPassthru.setNCharacterStream(parameterIndex, value);
    }

    /**
     * @param parameterName
     * @param value
     * @param length
     * @throws SQLException
     * @see java.sql.CallableStatement#setNCharacterStream(java.lang.String, java.io.Reader, long)
     */
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        callStmtPassthru.setNCharacterStream(parameterName, value, length);
    }

    /**
     * @param parameterName
     * @param value
     * @throws SQLException
     * @see java.sql.CallableStatement#setNCharacterStream(java.lang.String, java.io.Reader)
     */
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        callStmtPassthru.setNCharacterStream(parameterName, value);
    }

    /**
     * @param parameterIndex
     * @param value
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNClob(int, java.sql.NClob)
     */
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        callStmtPassthru.setNClob(parameterIndex, value);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader, long)
     */
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        callStmtPassthru.setNClob(parameterIndex, reader, length);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader)
     */
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        callStmtPassthru.setNClob(parameterIndex, reader);
    }

    /**
     * @param parameterName
     * @param value
     * @throws SQLException
     * @see java.sql.CallableStatement#setNClob(java.lang.String, java.sql.NClob)
     */
    public void setNClob(String parameterName, NClob value) throws SQLException {
        callStmtPassthru.setNClob(parameterName, value);
    }

    /**
     * @param parameterName
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.CallableStatement#setNClob(java.lang.String, java.io.Reader, long)
     */
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        callStmtPassthru.setNClob(parameterName, reader, length);
    }

    /**
     * @param parameterName
     * @param reader
     * @throws SQLException
     * @see java.sql.CallableStatement#setNClob(java.lang.String, java.io.Reader)
     */
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        callStmtPassthru.setNClob(parameterName, reader);
    }

    /**
     * @param parameterIndex
     * @param value
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNString(int, java.lang.String)
     */
    public void setNString(int parameterIndex, String value) throws SQLException {
        callStmtPassthru.setNString(parameterIndex, value);
    }

    /**
     * @param parameterName
     * @param value
     * @throws SQLException
     * @see java.sql.CallableStatement#setNString(java.lang.String, java.lang.String)
     */
    public void setNString(String parameterName, String value) throws SQLException {
        callStmtPassthru.setNString(parameterName, value);
    }

    /**
     * @param parameterIndex
     * @param sqlType
     * @param typeName
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
     */
    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        callStmtPassthru.setNull(parameterIndex, sqlType, typeName);
    }

    /**
     * @param parameterIndex
     * @param sqlType
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNull(int, int)
     */
    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        callStmtPassthru.setNull(parameterIndex, sqlType);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param targetSqlType
     * @param scaleOrLength
     * @throws SQLException
     * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
     */
    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        callStmtPassthru.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param targetSqlType
     * @throws SQLException
     * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
     */
    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        callStmtPassthru.setObject(parameterIndex, x, targetSqlType);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
     */
    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        callStmtPassthru.setObject(parameterIndex, x);
    }

    /**
     * @param poolable
     * @throws SQLException
     * @see java.sql.Statement#setPoolable(boolean)
     */
    public void setPoolable(boolean poolable) throws SQLException {
        callStmtPassthru.setPoolable(poolable);
    }

    /**
     * @param seconds
     * @throws SQLException
     * @see java.sql.Statement#setQueryTimeout(int)
     */
    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        callStmtPassthru.setQueryTimeout(seconds);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
     */
    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        callStmtPassthru.setRef(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setRowId(int, java.sql.RowId)
     */
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        callStmtPassthru.setRowId(parameterIndex, x);
    }

    /**
     * @param parameterName
     * @param x
     * @throws SQLException
     * @see java.sql.CallableStatement#setRowId(java.lang.String, java.sql.RowId)
     */
    public void setRowId(String parameterName, RowId x) throws SQLException {
        callStmtPassthru.setRowId(parameterName, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setShort(int, short)
     */
    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        callStmtPassthru.setShort(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param xmlObject
     * @throws SQLException
     * @see java.sql.PreparedStatement#setSQLXML(int, java.sql.SQLXML)
     */
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        callStmtPassthru.setSQLXML(parameterIndex, xmlObject);
    }

    /**
     * @param parameterName
     * @param xmlObject
     * @throws SQLException
     * @see java.sql.CallableStatement#setSQLXML(java.lang.String, java.sql.SQLXML)
     */
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        callStmtPassthru.setSQLXML(parameterName, xmlObject);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setString(int, java.lang.String)
     */
    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        callStmtPassthru.setString(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param cal
     * @throws SQLException
     * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
     */
    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        callStmtPassthru.setTime(parameterIndex, x, cal);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
     */
    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        callStmtPassthru.setTime(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param cal
     * @throws SQLException
     * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
     */
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        callStmtPassthru.setTimestamp(parameterIndex, x, cal);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
     */
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        callStmtPassthru.setTimestamp(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param length
     * @throws SQLException
     * @deprecated
     * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
     */
    @Deprecated
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        callStmtPassthru.setUnicodeStream(parameterIndex, x, length);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
     */
    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        callStmtPassthru.setURL(parameterIndex, x);
    }

    /**
     * @param <T>
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return callStmtPassthru.unwrap(iface);
    }

    // since 1.7
    @Override
    public <T> T getObject(final int parameterIndex, final Class<T> type) throws SQLException {
        return callStmtPassthru.getObject(parameterIndex, type);
    }

    // since 1.7
    @Override
    public <T> T getObject(final String parameterName, final Class<T> type) throws SQLException {
        return callStmtPassthru.getObject(parameterName, type);
    }
}
