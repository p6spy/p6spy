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
 * Description: JDBC Driver Extension implementing PreparedStatement. $Author: cheechq $ $Revision:
 * 1.4 $ $Date: 2003/06/03 19:20:24 $ $Id: P6Array.java,v 1.4 2003/06/03 19:20:24 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/spy/P6Array.java,v $ $Log: P6Array.java,v $
 * Revision 1.4 2003/06/03 19:20:24 cheechq removed unused imports Revision 1.3 2003/01/03 20:33:42
 * aarvesen Added getJDBC() method to return the underlying jdbc object. Revision 1.2 2002/12/06
 * 22:39:43 aarvesen Extend P6Base. New factory registration in the constructor. Revision 1.1
 * 2002/05/24 07:31:13 jeffgoke version 1 rewrite Revision 1.2 2002/04/15 05:13:32 jeffgoke Simon
 * Sadedin added timing support. Fixed bug where batch execute was not getting logged. Added result
 * set timing. Updated the log format to include categories, and updated options to control the
 * categories. Updated documentation. Revision 1.1 2002/04/10 04:24:26 jeffgoke added support for
 * callable statements and fixed numerous bugs that allowed the real class to be returned
 */

package com.p6spy.engine.spy;

import java.sql.*;

public class P6Array extends P6Base implements java.sql.Array {

    protected Array passthru;

    protected P6Statement statement;

    protected String query;

    protected String preparedQuery;

    public P6Array(P6Factory factory, Array array, P6Statement statement, String preparedQuery, String query) {
        super(factory);
        this.passthru = array;
        this.statement = statement;
        this.query = query;
        this.preparedQuery = preparedQuery;
    }

    public Object getArray() throws java.sql.SQLException {
        return passthru.getArray();
    }

    public Object getArray(java.util.Map p0) throws java.sql.SQLException {
        return passthru.getArray(p0);
    }

    public Object getArray(long p0, int p1) throws java.sql.SQLException {
        return passthru.getArray(p0, p1);
    }

    public Object getArray(long p0, int p1, java.util.Map map) throws java.sql.SQLException {
        return passthru.getArray(p0, p1, map);
    }

    public int getBaseType() throws java.sql.SQLException {
        return passthru.getBaseType();
    }

    public String getBaseTypeName() throws java.sql.SQLException {
        return passthru.getBaseTypeName();
    }

    public java.sql.ResultSet getResultSet() throws java.sql.SQLException {
        return getP6Factory().getResultSet(passthru.getResultSet(), statement, preparedQuery, query);
    }

    public java.sql.ResultSet getResultSet(java.util.Map p0) throws java.sql.SQLException {
        return getP6Factory().getResultSet(passthru.getResultSet(p0), statement, preparedQuery, query);
    }

    public java.sql.ResultSet getResultSet(long p0, int p1) throws java.sql.SQLException {
        return getP6Factory().getResultSet(passthru.getResultSet(p0, p1), statement, preparedQuery, query);
    }

    public java.sql.ResultSet getResultSet(long p0, int p1, java.util.Map p2) throws java.sql.SQLException {
        return getP6Factory().getResultSet(passthru.getResultSet(p0, p1, p2), statement, preparedQuery, query);
    }

    /**
     * @throws SQLException
     * @see java.sql.Array#free()
     */
    public void free() throws SQLException {
        passthru.free();
    }

    /**
     * Returns the underlying JDBC object (in this case, a java.sql.Array)
     *
     * @return the wrapped JDBC object
     */
    public Array getJDBC() {
        Array wrapped = (passthru instanceof P6Array) ? ((P6Array) passthru).getJDBC() : passthru;

        return wrapped;
    }
}
