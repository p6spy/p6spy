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
 * Description: Test class for prepared statements
 *
 * $Author: bradleydot $
 * $Revision: 1.3 $
 * $Date: 2003/08/06 18:50:59 $
 *
 * $Id: P6TestCallableStatement.java,v 1.3 2003/08/06 18:50:59 bradleydot Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestCallableStatement.java,v $
 * $Log: P6TestCallableStatement.java,v $
 * Revision 1.3  2003/08/06 18:50:59  bradleydot
 * Added TestCallable to verify that values array size will grow appropriately
 * when registerOutParameter methods are called.
 *
 * Revision 1.2  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.1  2002/05/24 07:30:46  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.1  2002/04/21 06:16:20  jeffgoke
 * added test cases, fixed batch bugs
 *
 *
 *
 */

package com.p6spy.engine.spy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.p6spy.engine.common.P6LogQuery;

@RunWith(Parameterized.class)
public class P6TestCallableStatement extends P6TestPreparedStatement {

  private static final Collection<Object[]> DBS_IN_TEST = Arrays.asList(new Object[][] { { "H2" } });
  
  /**
   * Always returns {@link DBS_IN_TEST} as we don't
   * need to rerun for each DB here.
   * The thing is that not all the DBs support stored procedures. Morever syntax might differ. 
   * We want just to prove that callable statements are correctly prxied.
   * So let's test just with H2 (default DB).
   * 
   * @return {@link DBS_IN_TEST}
   */
  @Parameters
  public static Collection<Object[]> dbs() {
    return DBS_IN_TEST;
  }

  
	public P6TestCallableStatement(String db) throws SQLException, IOException {
    super(db);
  }
	  
    @Test
    public void testCallable() throws SQLException {
      
      // tests inspired by: http://opensourcejavaphp.net/java/h2/org/h2/test/jdbc/TestCallableStatement.java.html
      Statement stat = connection.createStatement();
      {
        stat.execute("CREATE TABLE TEST(ID INT, NAME VARCHAR)");
        CallableStatement call = connection.prepareCall("INSERT INTO TEST VALUES(?, ?)");
        call.setInt(1, 1);
        call.setString(2, "Hello");
        call.execute();
      
        assertTrue(P6LogQuery.getLastEntry().indexOf("INSERT INTO TEST VALUES") != -1);
      }
     
      {
        CallableStatement call = connection.prepareCall("SELECT * FROM TEST", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = call.executeQuery();
        rs.next();
        assertEquals(1, rs.getInt(1));
        assertEquals("Hello", rs.getString(2));
        assertFalse(rs.next());
        
        assertTrue(P6LogQuery.getLastEntry().indexOf("SELECT * FROM TEST") != -1);
      }
      
      {
        CallableStatement call = connection.prepareCall("SELECT * FROM TEST", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
        ResultSet rs = call.executeQuery();
        rs.next();
        assertEquals(1, rs.getInt(1));
        assertEquals("Hello", rs.getString(2));
        assertFalse(rs.next());
        
        assertTrue(P6LogQuery.getLastEntry().indexOf("SELECT * FROM TEST") != -1);
      }
      
  }

}
