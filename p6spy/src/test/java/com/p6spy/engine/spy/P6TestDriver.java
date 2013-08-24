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

package com.p6spy.engine.spy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Test;

public class P6TestDriver extends P6TestFramework {
	
	private static final String JDBC_DRIVER_CLASS_NAME_SQLITE = "org.sqlite.JDBC";
	private static final String JDBC_DRIVER_CLASS_NAME_MYSQL = "com.mysql.jdbc.Driver";

	@Test
    public void testMajorVersion() throws Exception {
		Driver driver = getWrappedDriver();
	    if (driver.getClass().getName().equals(JDBC_DRIVER_CLASS_NAME_MYSQL)) {
	    	assertEquals(5, driver.getMajorVersion());
		    assertEquals(1, driver.getMinorVersion());
	    }
    }

    @Test
    public void testGetJDBC() throws SQLException, IOException {
	    P6Connection p6con = (P6Connection) connection;
	    chkGetJDBC(p6con, p6con.getJDBC());

	    P6DatabaseMetaData p6md = (P6DatabaseMetaData) connection.getMetaData();
	    chkGetJDBC(p6md, p6md.getJDBC());

	    P6Statement p6stmt = (P6Statement) connection.createStatement();
	    chkGetJDBC(p6stmt, p6stmt.getJDBC());

		if (!getWrappedDriver().getClass().getName().equals(JDBC_DRIVER_CLASS_NAME_SQLITE)) {
			P6CallableStatement p6cs = null;
			try {
				p6cs = (P6CallableStatement) connection
						.prepareCall("select current_timestamp from (values(0))");
				chkGetJDBC(p6cs, p6cs.getJDBC());
			} finally {
				if (null != p6cs) {
					p6cs.close();
				}
			}
		}
	    
	    P6PreparedStatement p6ps = null;
	    P6ResultSet p6rs = null;
	    // some drivers just don't like the syntax, so let's go for the fallback one in case
	    try {
	      p6ps = (P6PreparedStatement) connection.prepareStatement("select 1 + 1 from (values(0))");
	      chkGetJDBC(p6ps, p6ps.getJDBC());

	      p6rs = (P6ResultSet) p6ps.executeQuery();
	      chkGetJDBC(p6rs, p6rs.getJDBC());

	      P6ResultSetMetaData p6rsmd = (P6ResultSetMetaData) p6rs.getMetaData();
	      chkGetJDBC(p6rsmd, p6rsmd.getJDBC());
	    } catch (SQLException e) {
	      p6ps = (P6PreparedStatement) connection.prepareStatement("select 1 + 1");
	      chkGetJDBC(p6ps, p6ps.getJDBC());
	      
	      p6rs = (P6ResultSet) p6ps.executeQuery();
        chkGetJDBC(p6rs, p6rs.getJDBC());

        P6ResultSetMetaData p6rsmd = (P6ResultSetMetaData) p6rs.getMetaData();
        chkGetJDBC(p6rsmd, p6rsmd.getJDBC());
	    }
	    
	
	    // try to release everything
	    p6ps.close();
	    p6rs.close();
	    p6stmt.close();
	    p6con.close();
    }

    protected void chkGetJDBC(P6Base p6object, Object jdbcObject) {
    	String p6class = p6object.getClass().getName();
    	String jdbcClass = jdbcObject.getClass().getName();
    
    	assertTrue("Class " + p6class + " is supposed to be a p6 class, but it is not", (p6class.indexOf("p6spy") != -1));
    	assertTrue("Class " + jdbcClass + " is supposed to be a jdbc class, but it is not", (jdbcClass.indexOf("p6spy") == -1));
    }

    /**
     * Gets the wrapped {@link Driver} registered for the jdbc url test currently run for.
     *
     * @return the driver registered for the current run.
     * @throws IOException 
     * @throws SQLException
     */
    private Driver getWrappedDriver() throws IOException, SQLException {
    	Properties props = loadProperties(P6TestFramework.P6_TEST_PROPERTIES);
    	String url = props.getProperty("url");
    	Driver driver = DriverManager.getDriver(url);

	    // make sure you have a p6 driver
	    if (! (driver instanceof P6SpyDriverCore)) {
	        fail("Expected to get back a p6spy driver, got back a " + driver);
	    }
	    return ((P6SpyDriverCore) driver).getPassthru();
    }
}
