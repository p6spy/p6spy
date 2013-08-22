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
 * Description: JDBC Driver Extension implementing P6ResultSetMetaData
 *
 * $Author: cheechq $
 * $Revision: 1.4 $
 * $Date: 2003/06/03 19:20:25 $
 *
 * $Id: P6ResultSetMetaData.java,v 1.4 2003/06/03 19:20:25 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/spy/P6ResultSetMetaData.java,v $
 * $Log: P6ResultSetMetaData.java,v $
 * Revision 1.4  2003/06/03 19:20:25  cheechq
 * removed unused imports
 *
 * Revision 1.3  2003/01/03 20:33:43  aarvesen
 * Added getJDBC() method to return the underlying jdbc object.
 *
 * Revision 1.2  2002/12/06 22:44:12  aarvesen
 * Extend P6Base.
 * New factory registration in the constructor.
 * jdk 1.4
 *
 * Revision 1.1  2002/10/06 18:23:25  jeffgoke
 * no message
 *
 *
 */

package com.p6spy.engine.spy;

import java.sql.*;

public class P6ResultSetMetaData extends P6Base implements java.sql.ResultSetMetaData{


    protected ResultSetMetaData passthru;

    public P6ResultSetMetaData(P6Factory factory, ResultSetMetaData resultSetMetaData) {
        super(factory);
        this.passthru = resultSetMetaData;
    }

    public String getCatalogName(int param) throws java.sql.SQLException {
        return passthru.getCatalogName(param);
    }

    public String getColumnClassName(int param) throws java.sql.SQLException {
        return passthru.getColumnClassName(param);
    }

    public int getColumnCount() throws java.sql.SQLException {
        return passthru.getColumnCount();
    }

    public int getColumnDisplaySize(int param) throws java.sql.SQLException {
        return passthru.getColumnDisplaySize(param);
    }

    public String getColumnLabel(int param) throws java.sql.SQLException {
        return passthru.getColumnLabel(param);
    }

    public String getColumnName(int param) throws java.sql.SQLException {
        return passthru.getColumnName(param);
    }

    public int getColumnType(int param) throws java.sql.SQLException {
        return passthru.getColumnType(param);
    }

    public String getColumnTypeName(int param) throws java.sql.SQLException {
        return passthru.getColumnTypeName(param);
    }

    public int getPrecision(int param) throws java.sql.SQLException {
        return passthru.getPrecision(param);
    }

    public int getScale(int param) throws java.sql.SQLException {
        return passthru.getScale(param);
    }

    public String getSchemaName(int param) throws java.sql.SQLException {
        return passthru.getSchemaName(param);
    }

    public String getTableName(int param) throws java.sql.SQLException {
        return passthru.getTableName(param);
    }

    public boolean isAutoIncrement(int param) throws java.sql.SQLException {
        return passthru.isAutoIncrement(param);
    }

    public boolean isCaseSensitive(int param) throws java.sql.SQLException {
        return passthru.isCaseSensitive(param);
    }

    public boolean isCurrency(int param) throws java.sql.SQLException {
        return passthru.isCurrency(param);
    }

    public boolean isDefinitelyWritable(int param) throws java.sql.SQLException {
        return passthru.isDefinitelyWritable(param);
    }

    public int isNullable(int param) throws java.sql.SQLException {
        return passthru.isNullable(param);
    }

    public boolean isReadOnly(int param) throws java.sql.SQLException {
        return passthru.isReadOnly(param);
    }

    public boolean isSearchable(int param) throws java.sql.SQLException {
        return passthru.isSearchable(param);
    }

    public boolean isSigned(int param) throws java.sql.SQLException {
        return passthru.isSigned(param);
    }

    public boolean isWritable(int param) throws java.sql.SQLException {
        return passthru.isWritable(param);
    }

    /**
     * Returns the underlying JDBC object (in this case, a
     * java.sql.ResultSetMetaData)
     * @return the wrapped JDBC object
     */
    public ResultSetMetaData getJDBC() {
	ResultSetMetaData wrapped = (passthru instanceof P6ResultSetMetaData) ?
	    ((P6ResultSetMetaData) passthru).getJDBC() :
	    passthru;

	return wrapped;
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
