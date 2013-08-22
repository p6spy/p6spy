/*
 * ==================================================================== The P6Spy Software License,
 * Version 1.1 This license is derived and fully compatible with the Apache Software license, see
 * http://www.apache.org/LICENSE.txt Copyright (c) 2001-2002 Andy Martin, Ph.D. and Jeff Goke All
 * rights reserved. Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met: 1. Redistributions of source code
 * must retain the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "The original concept and code base for P6Spy was conceived
 * and developed by Andy Martin, Ph.D. who generously contribued the first complete release to the
 * public under this license. This product was due to the pioneering work of Andy that began in
 * December of 1995 developing applications that could seamlessly be deployed with minimal effort
 * but with dramatic results. This code is maintained and extended by Jeff Goke and with the ideas
 * and contributions of other P6Spy contributors. (http://www.p6spy.com)" Alternately, this
 * acknowlegement may appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "P6Spy", "Jeff Goke", and "Andy Martin" must not be
 * used to endorse or promote products derived from this software without prior written permission.
 * For written permission, please contact license@p6spy.com. 5. Products derived from this software
 * may not be called "P6Spy" nor may "P6Spy" appear in their names without prior written permission
 * of Jeff Goke and Andy Martin. THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Description: JDBC Driver Extension implementing PreparedStatement. $Author: bradleydot $
 * $Revision: 1.8 $ $Date: 2003/08/06 18:35:18 $ $Id: P6PreparedStatement.java,v 1.8 2003/08/06
 * 18:35:18 bradleydot Exp $ $Source:
 * /cvsroot/p6spy/p6spy/com/p6spy/engine/spy/P6PreparedStatement.java,v $ $Log:
 * P6PreparedStatement.java,v $ Revision 1.8 2003/08/06 18:35:18 bradleydot Minor changes so
 * TestCallable will work: added getValuesLength method and set P6_Grow_Max as public static. Also
 * changed setURL so that it is registered in values array. Revision 1.7 2003/06/20 20:31:37
 * aarvesen fix for bug 161: null result sets Revision 1.6 2003/06/05 20:10:00 aarvesen bradley
 * 'dot' johnson (bradley@irongrid.com) added in dynamic array allocation Revision 1.5 2003/06/03
 * 19:20:25 cheechq removed unused imports Revision 1.4 2003/01/03 20:33:43 aarvesen Added getJDBC()
 * method to return the underlying jdbc object. Revision 1.3 2002/12/06 22:42:47 aarvesen New
 * factory registration in the constructor. jdk 1.4 Revision 1.2 2002/10/06 18:23:25 jeffgoke no
 * message Revision 1.1 2002/05/24 07:31:13 jeffgoke version 1 rewrite Revision 1.8 2002/05/18
 * 06:39:52 jeffgoke Peter Laird added Outage detection. Added junit tests for outage detection.
 * Fixed multi-driver tests. Revision 1.7 2002/05/16 04:58:40 jeffgoke Viktor Szathmary added
 * multi-driver support. Rewrote P6SpyOptions to be easier to manage. Fixed several bugs. Revision
 * 1.6 2002/04/21 06:15:34 jeffgoke added test cases, fixed batch bugs Revision 1.5 2002/04/18
 * 06:54:39 jeffgoke added batch statement logging support Revision 1.4 2002/04/15 05:13:32 jeffgoke
 * Simon Sadedin added timing support. Fixed bug where batch execute was not getting logged. Added
 * result set timing. Updated the log format to include categories, and updated options to control
 * the categories. Updated documentation. Revision 1.3 2002/04/11 04:18:03 jeffgoke fixed bug where
 * callable & prepared were not passing their ancestors the correct constructor information Revision
 * 1.2 2002/04/10 04:24:26 jeffgoke added support for callable statements and fixed numerous bugs
 * that allowed the real class to be returned Revision 1.1.1.1 2002/04/07 04:52:25 jeffgoke no
 * message Revision 1.2 2001-08-05 09:16:04-05 andy final version on the website Revision 1.1
 * 2001-08-02 07:52:43-05 andy <> Revision 1.0 2001-08-02 06:37:42-05 andy Initial revision
 */

package com.p6spy.engine.spy;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.math.*;

import com.p6spy.engine.common.P6SpyOptions;

public class P6PreparedStatement extends P6Statement implements PreparedStatement {

    public final static int P6_MAX_FIELDS = 32;

    public static int P6_GROW_MAX = 32;


    protected PreparedStatement prepStmtPassthru;

    protected String preparedQuery;

    protected Object values[];

    protected boolean isString[];

    private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public P6PreparedStatement(P6Factory factory, PreparedStatement statement, P6Connection conn, String query) {
        super(factory, statement, conn);
        prepStmtPassthru = statement;
        this.preparedQuery = query;
        initValues();
    }

    protected void initValues() {
        values = new Object[P6_MAX_FIELDS + 1];
        isString = new boolean[P6_MAX_FIELDS + 1];
    }

    public void addBatch() throws SQLException {
        prepStmtPassthru.addBatch();
    }

    public void clearParameters() throws SQLException {
        prepStmtPassthru.clearParameters();
    }

    public boolean execute() throws SQLException {
        return prepStmtPassthru.execute();
    }

    public ResultSet executeQuery() throws SQLException {
        ResultSet resultSet = prepStmtPassthru.executeQuery();
        return (getP6Factory().getResultSet(resultSet, this, preparedQuery, getQueryFromPreparedStatement()));
    }

    public int executeUpdate() throws SQLException {
        return prepStmtPassthru.executeUpdate();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return prepStmtPassthru.getMetaData();
    }

    public void setArray(int p0, Array p1) throws SQLException {
        setObjectAsString(p0, p1);
        // we need to make sure we get the real object in this case
        if (p1 instanceof P6Array) {
            prepStmtPassthru.setArray(p0, ((P6Array) p1).passthru);
        } else {
            prepStmtPassthru.setArray(p0, p1);
        }
    }

    public void setAsciiStream(int p0, InputStream p1, int p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setAsciiStream(p0, p1, p2);
    }

    public void setBigDecimal(int p0, BigDecimal p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setBigDecimal(p0, p1);
    }

    public void setBinaryStream(int p0, InputStream p1, int p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setBinaryStream(p0, p1, p2);
    }

    public void setBlob(int p0, Blob p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setBlob(p0, p1);
    }

    public void setBoolean(int p0, boolean p1) throws SQLException {
        setObjectAsString(p0, new Boolean(p1));
        prepStmtPassthru.setBoolean(p0, p1);
    }

    public void setByte(int p0, byte p1) throws SQLException {
        setObjectAsString(p0, new Byte(p1));
        prepStmtPassthru.setByte(p0, p1);
    }

    public void setBytes(int p0, byte[] p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setBytes(p0, p1);
    }

    public void setCharacterStream(int p0, Reader p1, int p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setCharacterStream(p0, p1, p2);
    }

    public void setClob(int p0, Clob p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setClob(p0, p1);
    }

    public void setDate(int p0, Date p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setDate(p0, p1);
    }

    public void setDate(int p0, Date p1, java.util.Calendar p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setDate(p0, p1, p2);
    }

    public void setDouble(int p0, double p1) throws SQLException {
        setObjectAsInt(p0, Double.valueOf(p1));
        prepStmtPassthru.setDouble(p0, p1);
    }

    public void setFloat(int p0, float p1) throws SQLException {
        setObjectAsInt(p0, new Float(p1));
        prepStmtPassthru.setFloat(p0, p1);
    }

    public void setInt(int p0, int p1) throws SQLException {
        setObjectAsInt(p0, Integer.valueOf(p1));
        prepStmtPassthru.setInt(p0, p1);
    }

    public void setLong(int p0, long p1) throws SQLException {
        setObjectAsInt(p0, Long.valueOf(p1));
        prepStmtPassthru.setLong(p0, p1);
    }

    public void setNull(int p0, int p1, String p2) throws SQLException {
        setObjectAsString(p0, null);
        prepStmtPassthru.setNull(p0, p1, p2);
    }

    public void setNull(int p0, int p1) throws SQLException {
        setObjectAsString(p0, null);
        prepStmtPassthru.setNull(p0, p1);
    }

    public void setObject(int p0, Object p1, int p2, int p3) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setObject(p0, p1, p2, p3);
    }

    public void setObject(int p0, Object p1, int p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setObject(p0, p1, p2);
    }

    public void setObject(int p0, Object p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setObject(p0, p1);
    }

    public void setRef(int p0, Ref p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setRef(p0, p1);
    }

    public void setShort(int p0, short p1) throws SQLException {
        setObjectAsString(p0, new Short(p1));
        prepStmtPassthru.setShort(p0, p1);
    }

    public void setString(int p0, String p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setString(p0, p1);
    }

    public void setTime(int p0, Time p1, java.util.Calendar p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setTime(p0, p1, p2);
    }

    public void setTime(int p0, Time p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setTime(p0, p1);
    }

    public void setTimestamp(int p0, Timestamp p1, java.util.Calendar p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setTimestamp(p0, p1, p2);
    }

    public void setTimestamp(int p0, Timestamp p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setTimestamp(p0, p1);
    }

    public void setUnicodeStream(int p0, InputStream p1, int p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setUnicodeStream(p0, p1, p2);
    }

    /*
     * we override this because the p6statement version will not be able to return the accurate
     * prepared statement or query information
     */
    // bug 161: getResultSet() should return null if this is an update
    // count or there are not more result sets
    @Override
    public java.sql.ResultSet getResultSet() throws java.sql.SQLException {
        ResultSet rs = passthru.getResultSet();
        return (rs == null) ? null : getP6Factory().getResultSet(rs, this, preparedQuery, getQueryFromPreparedStatement());
    }

    /*
     * P6Spy specific functionality
     */
    public final String getQueryFromPreparedStatement() {
        int len = preparedQuery.length();
        StringBuffer t = new StringBuffer(len * 2);

        if (values != null) {
            int i = 1, limit = 0, base = 0;

            while ((limit = preparedQuery.indexOf('?', limit)) != -1) {
                t.append(preparedQuery.substring(base, limit));
                if (values[i] == null) {
                    t.append("NULL");
                } else if (isString[i]) {
                    t.append("'");
                    t.append(values[i]);
                    t.append("'");
                } else {
                    t.append(values[i]);
                }
                i++;
                limit++;
                base = limit;
            }
            if (base < len) {
                t.append(preparedQuery.substring(base));
            }
        }

        return t.toString();
    }

    protected void growValues(int newMax) {
        int size = values.length;
        Object[] values_tmp = new Object[newMax + P6_GROW_MAX];
        boolean[] isString_tmp = new boolean[newMax + P6_GROW_MAX];
        System.arraycopy(values, 0, values_tmp, 0, size);
        values = values_tmp;
        System.arraycopy(isString, 0, isString_tmp, 0, size);
        isString = isString_tmp;
    }

    protected void setObjectAsString(int i, Object o) {
        if (values != null) {
            if (i >= 0) {
                if (i >= values.length) {
                    growValues(i);
                }
                if (o instanceof java.util.Date) {
                    values[i] = new SimpleDateFormat(P6SpyOptions.getDatabaseDialectDateFormat()).format(o);
                }
                else if ( o instanceof byte[] ) {
                    values[i] = toHexString( (byte[])o );
                }
                else {
                    values[i] = (o == null) ? null : o.toString();
                }
                isString[i] = true;
            }
        }
    }

    private static final String toHexString( byte[] bytes ) {
        String value = "";
        for ( byte b : bytes ) {
            int temp = (int)b & 0xFF;
            value += HEX_CHARS[ temp/16 ];
            value += HEX_CHARS[ temp%16 ];
        }
        return value;
    }

    protected void setObjectAsInt(int i, Object o) {
        if (values != null) {
            if (i >= 0) {
                if (i >= values.length) {
                    growValues(i);
                }
                values[i] = (o == null) ? null : o.toString();
                isString[i] = false;
            }
        }
    }

    // Since JDK 1.4
    public void setURL(int p0, java.net.URL p1) throws java.sql.SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setURL(p0, p1);
    }

    // Since JDK 1.4
    public java.sql.ParameterMetaData getParameterMetaData() throws java.sql.SQLException {
        return (prepStmtPassthru.getParameterMetaData());
    }

    /**
     * Returns the underlying JDBC object (in this case, a java.sql.PreparedStatement).
     * <p>
     * The returned object is a java.sql.Statement due to inheritance reasons, so you'll need to
     * cast appropriately.
     *
     * @return the wrapped JDBC object
     */
    @Override
    public Statement getJDBC() {
        Statement wrapped = (prepStmtPassthru instanceof P6Statement) ? ((P6Statement) prepStmtPassthru).getJDBC() : prepStmtPassthru;

        return wrapped;
    }

    public int getValuesLength() {
        return values.length;
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream)
     */
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        prepStmtPassthru.setAsciiStream(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, long)
     */
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        prepStmtPassthru.setAsciiStream(parameterIndex, x, length);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream)
     */
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        prepStmtPassthru.setBinaryStream(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, long)
     */
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        prepStmtPassthru.setBinaryStream(parameterIndex, x, length);
    }

    /**
     * @param parameterIndex
     * @param inputStream
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream)
     */
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        prepStmtPassthru.setBlob(parameterIndex, inputStream);
    }

    /**
     * @param parameterIndex
     * @param inputStream
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream, long)
     */
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        prepStmtPassthru.setBlob(parameterIndex, inputStream, length);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @throws SQLException
     * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader)
     */
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        prepStmtPassthru.setCharacterStream(parameterIndex, reader);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, long)
     */
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        prepStmtPassthru.setCharacterStream(parameterIndex, reader, length);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @throws SQLException
     * @see java.sql.PreparedStatement#setClob(int, java.io.Reader)
     */
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        prepStmtPassthru.setClob(parameterIndex, reader);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setClob(int, java.io.Reader, long)
     */
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        prepStmtPassthru.setClob(parameterIndex, reader, length);
    }

    /**
     * @param parameterIndex
     * @param value
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader)
     */
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        prepStmtPassthru.setNCharacterStream(parameterIndex, value);
    }

    /**
     * @param parameterIndex
     * @param value
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader, long)
     */
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        prepStmtPassthru.setNCharacterStream(parameterIndex, value, length);
    }

    /**
     * @param parameterIndex
     * @param value
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNClob(int, java.sql.NClob)
     */
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        prepStmtPassthru.setNClob(parameterIndex, value);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader)
     */
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        prepStmtPassthru.setNClob(parameterIndex, reader);
    }

    /**
     * @param parameterIndex
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader, long)
     */
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        prepStmtPassthru.setNClob(parameterIndex, reader, length);
    }

    /**
     * @param parameterIndex
     * @param value
     * @throws SQLException
     * @see java.sql.PreparedStatement#setNString(int, java.lang.String)
     */
    public void setNString(int parameterIndex, String value) throws SQLException {
        prepStmtPassthru.setNString(parameterIndex, value);
    }

    /**
     * @param parameterIndex
     * @param x
     * @throws SQLException
     * @see java.sql.PreparedStatement#setRowId(int, java.sql.RowId)
     */
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        prepStmtPassthru.setRowId(parameterIndex, x);
    }

    /**
     * @param parameterIndex
     * @param xmlObject
     * @throws SQLException
     * @see java.sql.PreparedStatement#setSQLXML(int, java.sql.SQLXML)
     */
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        prepStmtPassthru.setSQLXML(parameterIndex, xmlObject);
    }

    /**
     * @param sql
     * @throws SQLException
     * @see java.sql.Statement#addBatch(java.lang.String)
     */
    @Override
    public void addBatch(String sql) throws SQLException {
        prepStmtPassthru.addBatch(sql);
    }

    /**
     * @throws SQLException
     * @see java.sql.Statement#cancel()
     */
    @Override
    public void cancel() throws SQLException {
        prepStmtPassthru.cancel();
    }

    /**
     * @throws SQLException
     * @see java.sql.Statement#clearBatch()
     */
    @Override
    public void clearBatch() throws SQLException {
        prepStmtPassthru.clearBatch();
    }

    /**
     * @throws SQLException
     * @see java.sql.Statement#clearWarnings()
     */
    @Override
    public void clearWarnings() throws SQLException {
        prepStmtPassthru.clearWarnings();
    }

    /**
     * @throws SQLException
     * @see java.sql.Statement#close()
     */
    @Override
    public void close() throws SQLException {
        prepStmtPassthru.close();
    }

    /**
     * @param sql
     * @return
     * @throws SQLException
     * @see java.sql.Statement#execute(java.lang.String)
     */
    @Override
    public boolean execute(String sql) throws SQLException {
        return prepStmtPassthru.execute(sql);
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
        return prepStmtPassthru.execute(sql, autoGeneratedKeys);
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
        return prepStmtPassthru.execute(sql, columnIndexes);
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
        return prepStmtPassthru.execute(sql, columnNames);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#executeBatch()
     */
    @Override
    public int[] executeBatch() throws SQLException {
        return prepStmtPassthru.executeBatch();
    }

    /**
     * @param sql
     * @return
     * @throws SQLException
     * @see java.sql.Statement#executeQuery(java.lang.String)
     */
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return prepStmtPassthru.executeQuery(sql);
    }

    /**
     * @param sql
     * @return
     * @throws SQLException
     * @see java.sql.Statement#executeUpdate(java.lang.String)
     */
    @Override
    public int executeUpdate(String sql) throws SQLException {
        return prepStmtPassthru.executeUpdate(sql);
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
        return prepStmtPassthru.executeUpdate(sql, autoGeneratedKeys);
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
        return prepStmtPassthru.executeUpdate(sql, columnIndexes);
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
        return prepStmtPassthru.executeUpdate(sql, columnNames);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getConnection()
     */
    @Override
    public Connection getConnection() throws SQLException {
        return prepStmtPassthru.getConnection();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getFetchDirection()
     */
    @Override
    public int getFetchDirection() throws SQLException {
        return prepStmtPassthru.getFetchDirection();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getFetchSize()
     */
    @Override
    public int getFetchSize() throws SQLException {
        return prepStmtPassthru.getFetchSize();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getGeneratedKeys()
     */
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return prepStmtPassthru.getGeneratedKeys();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getMaxFieldSize()
     */
    @Override
    public int getMaxFieldSize() throws SQLException {
        return prepStmtPassthru.getMaxFieldSize();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getMaxRows()
     */
    @Override
    public int getMaxRows() throws SQLException {
        return prepStmtPassthru.getMaxRows();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getMoreResults()
     */
    @Override
    public boolean getMoreResults() throws SQLException {
        return prepStmtPassthru.getMoreResults();
    }

    /**
     * @param current
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getMoreResults(int)
     */
    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return prepStmtPassthru.getMoreResults(current);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getQueryTimeout()
     */
    @Override
    public int getQueryTimeout() throws SQLException {
        return prepStmtPassthru.getQueryTimeout();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getResultSetConcurrency()
     */
    @Override
    public int getResultSetConcurrency() throws SQLException {
        return prepStmtPassthru.getResultSetConcurrency();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getResultSetHoldability()
     */
    @Override
    public int getResultSetHoldability() throws SQLException {
        return prepStmtPassthru.getResultSetHoldability();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getResultSetType()
     */
    @Override
    public int getResultSetType() throws SQLException {
        return prepStmtPassthru.getResultSetType();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getUpdateCount()
     */
    @Override
    public int getUpdateCount() throws SQLException {
        return prepStmtPassthru.getUpdateCount();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#getWarnings()
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return prepStmtPassthru.getWarnings();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#isClosed()
     */
    @Override
    public boolean isClosed() throws SQLException {
        return prepStmtPassthru.isClosed();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#isPoolable()
     */
    @Override
    public boolean isPoolable() throws SQLException {
        return prepStmtPassthru.isPoolable();
    }

    /**
     * @param name
     * @throws SQLException
     * @see java.sql.Statement#setCursorName(java.lang.String)
     */
    @Override
    public void setCursorName(String name) throws SQLException {
        prepStmtPassthru.setCursorName(name);
    }

    /**
     * @param enable
     * @throws SQLException
     * @see java.sql.Statement#setEscapeProcessing(boolean)
     */
    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        prepStmtPassthru.setEscapeProcessing(enable);
    }

    /**
     * @param direction
     * @throws SQLException
     * @see java.sql.Statement#setFetchDirection(int)
     */
    @Override
    public void setFetchDirection(int direction) throws SQLException {
        prepStmtPassthru.setFetchDirection(direction);
    }

    /**
     * @param rows
     * @throws SQLException
     * @see java.sql.Statement#setFetchSize(int)
     */
    @Override
    public void setFetchSize(int rows) throws SQLException {
        prepStmtPassthru.setFetchSize(rows);
    }

    /**
     * @param max
     * @throws SQLException
     * @see java.sql.Statement#setMaxFieldSize(int)
     */
    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        prepStmtPassthru.setMaxFieldSize(max);
    }

    /**
     * @param max
     * @throws SQLException
     * @see java.sql.Statement#setMaxRows(int)
     */
    @Override
    public void setMaxRows(int max) throws SQLException {
        prepStmtPassthru.setMaxRows(max);
    }

    /**
     * @param poolable
     * @throws SQLException
     * @see java.sql.Statement#setPoolable(boolean)
     */
    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        prepStmtPassthru.setPoolable(poolable);
    }

    /**
     * @param seconds
     * @throws SQLException
     * @see java.sql.Statement#setQueryTimeout(int)
     */
    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        prepStmtPassthru.setQueryTimeout(seconds);
    }

    /**
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return prepStmtPassthru.isWrapperFor(iface);
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
        return prepStmtPassthru.unwrap(iface);
    }
}
