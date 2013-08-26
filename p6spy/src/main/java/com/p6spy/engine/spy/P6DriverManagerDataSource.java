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
 * $Author: cheechq $ $Revision: 1.3 $ $Date: 2003/06/03 19:20:25 $ $Id:
 * P6DriverManagerDataSource.java,v 1.3 2003/06/03 19:20:25 cheechq Exp $ $Source:
 * /cvsroot/p6spy/p6spy/com/p6spy/engine/spy/P6DriverManagerDataSource.java,v $ $Log:
 * P6DriverManagerDataSource.java,v $ Revision 1.3 2003/06/03 19:20:25 cheechq removed unused
 * imports Revision 1.2 2003/01/03 21:17:34 aarvesen use the new P6Util.forName Revision 1.1
 * 2002/12/20 00:30:21 aarvesen Added a simple driver manager around the sucker mc Revision 1.1
 * 2002/12/19 23:51:45 aarvesen Data Source implementation
 */

package com.p6spy.engine.spy;

import com.p6spy.engine.common.P6Util;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class P6DriverManagerDataSource implements DataSource {
    protected DataSource rds;

    protected String url;

    protected String user;

    protected String password;

    public P6DriverManagerDataSource() {
        try {
            P6Util.forName("com.p6spy.engine.spy.P6SpyDriver");
        } catch (Exception e) {
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String inVar) {
        password = inVar;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String inVar) {
        user = inVar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String inVar) {
        url = inVar;
    }

    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    public void setLoginTimeout(int inVar) throws SQLException {
        DriverManager.setLoginTimeout(inVar);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    public void setLogWriter(PrintWriter inVar) throws SQLException {
        DriverManager.setLogWriter(inVar);
    }

    public Connection getConnection() throws SQLException {

        return getConnection(url, user, password);
    }

    public Connection getConnection(String p0, String p1) throws SQLException {
        return getConnection(url, p0, p1);
    }

    public Connection getConnection(String p0, String p1, String p2) throws SQLException {
        return DriverManager.getConnection(p0, p1, p2);
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
