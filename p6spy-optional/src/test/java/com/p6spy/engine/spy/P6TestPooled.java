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

import java.util.*;
import java.sql.*;
import javax.sql.*;

import oracle.jdbc.pool.*;

import com.p6spy.engine.common.*;
import com.p6spy.engine.spy.*;
import junit.framework.*;

public class P6TestPooled extends TestCase {
    public P6TestPooled (String name) {
	super(name);
    }

    public void testPooling() throws Exception {
      Properties props = P6TestUtil.loadProperties("P6Test.properties");
      String user = props.getProperty("user");
      String password = props.getProperty("password");
      String url = props.getProperty("url");


      //System.out.println(">>>>> url is " + url);

      OracleConnectionPoolDataSource ods;
      ods = new OracleConnectionPoolDataSource();

      // Oracle specific operations
      ods.setURL(url);
      ods.setUser(user);
      ods.setPassword(password);

      String firstDate = chkDataSource(ods);

      P6ConnectionPoolDataSource pds = new P6ConnectionPoolDataSource(ods);
      String secondDate = chkDataSource(pds);

      if (firstDate == null) {
	fail("No date for the first call");
      }
      if (secondDate == null) {
	fail("No date for the second call");
      }
      assertEquals("Dates are different", firstDate, secondDate);
	  
    }

    protected String chkDataSource(ConnectionPoolDataSource ds) throws SQLException {
      String rv = null;
      PooledConnection pc = null;
      Connection con = null;
      Statement s = null;
      ResultSet rs = null;
      try {
	pc = ds.getPooledConnection();
	con = pc.getConnection();
	s = con.createStatement();
	rs = s.executeQuery("SELECT SYSDATE FROM DUAL");
	if (!rs.next()) {
	  fail("No date for sysdate");
	}
	rv = rs.getString(1);
      } finally {
	if (rs != null) rs.close();
	if (s != null) s.close();
	if (con != null) con.close();
	if (pc != null) pc.close();
      }

      return rv;
    }
}
