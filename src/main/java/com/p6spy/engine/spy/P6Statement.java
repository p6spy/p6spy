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
 * Description: Wrapper class for Statement
 *
 * $Author: aarvesen $
 * $Revision: 1.4 $
 * $Date: 2003/06/20 20:31:55 $
 *
 * $Id: P6Statement.java,v 1.4 2003/06/20 20:31:55 aarvesen Exp $
 * $Log: P6Statement.java,v $
 * Revision 1.4  2003/06/20 20:31:55  aarvesen
 * fix for bug 161:  null result sets
 *
 * Revision 1.3  2003/01/03 20:33:43  aarvesen
 * Added getJDBC() method to return the underlying jdbc object.
 *
 * Revision 1.2  2002/12/06 22:44:42  aarvesen
 * Extend P6Base.
 * New factory registration in the constructor.
 * jdk 1.4
 *
 * Revision 1.1  2002/05/24 07:31:13  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.8  2002/05/18 06:39:52  jeffgoke
 * Peter Laird added Outage detection.  Added junit tests for outage detection.
 * Fixed multi-driver tests.
 *
 * Revision 1.7  2002/05/16 04:58:40  jeffgoke
 * Viktor Szathmary added multi-driver support.
 * Rewrote P6SpyOptions to be easier to manage.
 * Fixed several bugs.
 *
 * Revision 1.6  2002/04/21 06:15:35  jeffgoke
 * added test cases, fixed batch bugs
 *
 * Revision 1.5  2002/04/18 06:54:39  jeffgoke
 * added batch statement logging support
 *
 * Revision 1.4  2002/04/15 05:13:32  jeffgoke
 * Simon Sadedin added timing support.  Fixed bug where batch execute was not
 * getting logged.  Added result set timing.  Updated the log format to include
 * categories, and updated options to control the categories.  Updated
 * documentation.
 *
 * Revision 1.3  2002/04/11 04:18:03  jeffgoke
 * fixed bug where callable & prepared were not passing their ancestors the correct constructor information
 *
 * Revision 1.2  2002/04/10 04:24:26  jeffgoke
 * added support for callable statements and fixed numerous bugs that allowed the real class to be returned
 *
 * Revision 1.1.1.1  2002/04/07 04:52:26  jeffgoke
 * no message
 *
 * Revision 1.3  2001-08-05 09:16:04-05  andy
 * final version on the website
 *
 * Revision 1.2  2001-08-02 07:52:44-05  andy
 * <>
 *
 * Revision 1.1  2001-07-30 23:03:31-05  andy
 * <>
 *
 * Revision 1.0  2001-07-30 17:46:23-05  andy
 * Initial revision
 *
 *
 */

package com.p6spy.engine.spy;

import java.sql.*;

public class P6Statement extends P6Base implements Statement {

    protected Statement passthru;

    protected P6Connection connection;

    protected String statementQuery;

    public P6Statement(P6Factory factory, Statement statement, P6Connection conn) {
        super(factory);
        this.passthru = statement;
        this.connection = conn;
        this.statementQuery = "";
    }

    public void close() throws java.sql.SQLException {
        passthru.close();
    }

    public boolean execute(String p0) throws java.sql.SQLException {
        return passthru.execute(p0);
    }

    // Bug 161:  this method, unlike getResultSet(), should  never return null
    public ResultSet executeQuery(String p0) throws java.sql.SQLException {
        return (getP6Factory().getResultSet(passthru.executeQuery(p0), this, "", p0));
    }

    public int executeUpdate(String p0) throws java.sql.SQLException {
        return (passthru.executeUpdate(p0));
    }

    public int getMaxFieldSize() throws java.sql.SQLException {
        return (passthru.getMaxFieldSize());
    }

    public void setMaxFieldSize(int p0) throws java.sql.SQLException {
        passthru.setMaxFieldSize(p0);
    }

    public int getMaxRows() throws java.sql.SQLException {
        return (passthru.getMaxRows());
    }

    public void setMaxRows(int p0) throws java.sql.SQLException {
        passthru.setMaxRows(p0);
    }

    public void setEscapeProcessing(boolean p0) throws java.sql.SQLException {
        passthru.setEscapeProcessing(p0);
    }

    public int getQueryTimeout() throws java.sql.SQLException {
        return (passthru.getQueryTimeout());
    }

    public void setQueryTimeout(int p0) throws java.sql.SQLException {
        passthru.setQueryTimeout(p0);
    }

    public void cancel() throws java.sql.SQLException {
        passthru.cancel();
    }

    public java.sql.SQLWarning getWarnings() throws java.sql.SQLException {
        return (passthru.getWarnings());
    }

    public void clearWarnings() throws java.sql.SQLException {
        passthru.clearWarnings();
    }

    public void setCursorName(String p0) throws java.sql.SQLException {
        passthru.setCursorName(p0);
    }

    // bug 161: getResultSet() should return null if this is an update
    // count or there are not more result sets
    public java.sql.ResultSet getResultSet() throws java.sql.SQLException {
        ResultSet rs = passthru.getResultSet();
        return (rs == null) ? null : getP6Factory().getResultSet(rs, this, "", statementQuery);
    }

    public int getUpdateCount() throws java.sql.SQLException {
        return (passthru.getUpdateCount());
    }

    public boolean getMoreResults() throws java.sql.SQLException {
        return (passthru.getMoreResults());
    }

    public void setFetchDirection(int p0) throws java.sql.SQLException {
        passthru.setFetchDirection(p0);
    }

    public int getFetchDirection() throws java.sql.SQLException {
        return (passthru.getFetchDirection());
    }

    public void setFetchSize(int p0) throws java.sql.SQLException {
        passthru.setFetchSize(p0);
    }

    public int getFetchSize() throws java.sql.SQLException {
        return (passthru.getFetchSize());
    }

    public int getResultSetConcurrency() throws java.sql.SQLException {
        return (passthru.getResultSetConcurrency());
    }

    public int getResultSetType() throws java.sql.SQLException {
        return (passthru.getResultSetType());
    }

    public void addBatch(String p0) throws java.sql.SQLException {
        passthru.addBatch(p0);
    }

    public void clearBatch() throws java.sql.SQLException {
        passthru.clearBatch();
    }

    public int[] executeBatch() throws java.sql.SQLException {
        return (passthru.executeBatch());
    }

    // returns the p6connection
    public java.sql.Connection getConnection() throws java.sql.SQLException {
        return connection;
    }

    // Since JDK 1.4
    public boolean getMoreResults(int p0) throws java.sql.SQLException {
        return (passthru.getMoreResults(p0));
    }

    // Since JDK 1.4
    public java.sql.ResultSet getGeneratedKeys() throws java.sql.SQLException {
        return (passthru.getGeneratedKeys());
    }

    // Since JDK 1.4
    public int executeUpdate(String p0, int p1) throws java.sql.SQLException {
        return (passthru.executeUpdate(p0, p1));
    }

    // Since JDK 1.4
    public int executeUpdate(String p0, int p1[]) throws java.sql.SQLException {
        return (passthru.executeUpdate(p0, p1));
    }

    // Since JDK 1.4
    public int executeUpdate(String p0, String p1[]) throws java.sql.SQLException {
        return (passthru.executeUpdate(p0, p1));
    }

    // Since JDK 1.4
    public boolean execute(String p0, int p1) throws java.sql.SQLException {
        return (passthru.execute(p0, p1));
    }

    // Since JDK 1.4
    public boolean execute(String p0, int p1[]) throws java.sql.SQLException {
        return (passthru.execute(p0, p1));
    }

    // Since JDK 1.4
    public boolean execute(String p0, String p1[]) throws java.sql.SQLException {
        return (passthru.execute(p0, p1));
    }

    // Since JDK 1.4
    public int getResultSetHoldability() throws java.sql.SQLException {
        return (passthru.getResultSetHoldability());
    }

    /**
     * Returns the underlying JDBC object (in this case, a java.sql.Statement)
     *
     * @return the wrapped JDBC object
     */
    public Statement getJDBC() {
        Statement wrapped = (passthru instanceof P6Statement) ? ((P6Statement) passthru).getJDBC() : passthru;

        return wrapped;
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#isClosed()
     */
    public boolean isClosed() throws SQLException {
        return passthru.isClosed();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.Statement#isPoolable()
     */
    public boolean isPoolable() throws SQLException {
        return passthru.isPoolable();
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
     * @param poolable
     * @throws SQLException
     * @see java.sql.Statement#setPoolable(boolean)
     */
    public void setPoolable(boolean poolable) throws SQLException {
        passthru.setPoolable(poolable);
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
}
