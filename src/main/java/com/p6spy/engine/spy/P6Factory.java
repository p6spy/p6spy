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
 * Description: P6Factory interface
 *
 * $Author: jeffgoke $
 * $Revision: 1.3 $
 * $Date: 2003/01/28 17:01:12 $
 *
 * $Id: P6Factory.java,v 1.3 2003/01/28 17:01:12 jeffgoke Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/spy/P6Factory.java,v $
 * $Log: P6Factory.java,v $
 * Revision 1.3  2003/01/28 17:01:12  jeffgoke
 * rewrote options to the ability for a module to have its own option set
 *
 * Revision 1.2  2002/10/06 18:23:25  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:31:13  jeffgoke
 * version 1 rewrite
 *
 *
 *
 */

package com.p6spy.engine.spy;

import java.sql.*;
import com.p6spy.engine.common.P6Options;

/**
 *
 * p6factory exists to make extending the spy core easier when making
 * a new module.  Since there are so many methods that
 * return a NEW object of some type (connection, etc)
 * either you would be forced to overload them all, or we could use this
 * factory method to handle that situation.  not perfect, but should make
 * extending and maintaining the code far easier.
 *
 */
public interface P6Factory {
    
    public P6Options getOptions() throws SQLException;
    public Connection getConnection(Connection conn) throws SQLException;
    public PreparedStatement getPreparedStatement(PreparedStatement real, P6Connection conn, String p0) throws SQLException;
    public Statement getStatement(Statement real, P6Connection conn) throws SQLException;
    public CallableStatement getCallableStatement(CallableStatement real, P6Connection conn, String p0) throws SQLException;
    public DatabaseMetaData getDatabaseMetaData(DatabaseMetaData real, P6Connection conn) throws SQLException;
    public ResultSet getResultSet(ResultSet real, P6Statement statement, String preparedQuery, String query) throws SQLException;
    public Array getArray(Array real, P6Statement statement, String preparedQuery, String query) throws SQLException;
    public ResultSetMetaData getResultSetMetaData(ResultSetMetaData real) throws SQLException;
    
}
