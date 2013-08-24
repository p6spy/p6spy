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
 * Description: Test class for statements
 *
 * $Author: aarvesen $
 * $Revision: 1.4 $
 * $Date: 2003/06/20 20:32:20 $
 *
 * $Id: P6TestStatement.java,v 1.4 2003/06/20 20:32:20 aarvesen Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestStatement.java,v $
 * $Log: P6TestStatement.java,v $
 * Revision 1.4  2003/06/20 20:32:20  aarvesen
 * test for bug 161:  null result sets
 *
 * Revision 1.3  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.2  2003/04/09 16:44:00  jeffgoke
 * Added Jboss JMX support.  Updated documentation.  Added execution threshold property to only log queries taking longer than a specified time.
 *
 * Revision 1.1  2002/05/24 07:30:46  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.7  2002/05/18 06:39:52  jeffgoke
 * Peter Laird added Outage detection.  Added junit tests for outage detection.
 * Fixed multi-driver tests.
 *
 * Revision 1.6  2002/05/16 04:58:40  jeffgoke
 * Viktor Szathmary added multi-driver support.
 * Rewrote P6SpyOptions to be easier to manage.
 * Fixed several bugs.
 *
 * Revision 1.5  2002/05/05 00:43:00  jeffgoke
 * Added Philip's reload code.
 *
 * Revision 1.4  2002/04/27 20:24:01  jeffgoke
 * added logging of commit statements and rollback statements
 *
 * Revision 1.3  2002/04/25 06:51:28  jeffgoke
 * Philip Ogren of BEA contributed installation instructions for BEA WebLogic Portal and Server
 * Jakarta RegEx support (contributed by Philip Ogren)
 * Ability to print stack trace of logged statements. This is very useful to understand where a logged query is being executed in the application (contributed by Philip Ogren)
 * Simplified table monitoring property file option (contributed by Philip Ogren)
 * Updated the RegEx documentation
 *
 * Revision 1.2  2002/04/22 02:26:06  jeffgoke
 * Simon Sadedin added timing information.  Added Junit tests.
 *
 * Revision 1.1  2002/04/21 06:16:20  jeffgoke
 * added test cases, fixed batch bugs
 *
 *
 *
 */

package com.p6spy.engine.spy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6SpyOptions;

public class P6TestStatement extends P6TestFramework {

    @Before
    public void setUpStatement() {
        try {
            Statement statement = connection.createStatement();
            drop(statement);
            statement.execute("create table stmt_test (col1 varchar(255), col2 integer)");
            statement.close();
        } catch (Exception e) {
            fail(e.getMessage()+" due to error: "+getStackTrace(e));
        }
    }
    @Test
    public void testQueryUpdate() {
        try {
	    ResultSet rs = null;

            // test a basic insert
            String update = "insert into stmt_test values (\'bob\', 5)";
            Statement statement = connection.createStatement();
            
            // as executeUpdate Javadocs say:
            // Returns either 
            // (1) the row count for SQL Data Manipulation Language (DML) statements or 
            // (2) 0 for SQL statements that return nothing
            //
            // most of the drivers return != 0, except SQLite, that returns == 0
            // for the SQLite calling: rs = statement.getResultSet() fails with:
            // SQLException statement is not executing getResultset on insert
            // 
            // => let's check for the result and handle correctly
            boolean noResult = 0 != statement.executeUpdate(update);
            assertTrue(P6LogQuery.getLastEntry().indexOf(update) != -1);

		    // most of drivers
		    assertTrue("neither no result indicated, nor statement is not null", noResult || statement.getResultSet() == null);
            
            // test a basic select
            String query = "select count(*) from stmt_test";
            rs = statement.executeQuery(query);
            assertTrue(P6LogQuery.getLastEntry().indexOf(query) != -1);
            rs.next();
            assertEquals(1, rs.getInt(1));
            
            try {
                // test batch inserts
                update = "insert into stmt_test values (\'jim\', 6)";
                statement.addBatch(update);
                update = "insert into stmt_test values (\'billy\', 7)";
                statement.addBatch(update);
                update = "insert into stmt_test values (\'bambi\', 8)";
                statement.addBatch(update);
                statement.executeBatch();
                assertTrue(P6LogQuery.getLastEntry().indexOf(update) != -1);
                
                query = "select count(*) from stmt_test";
                rs = statement.executeQuery(query);
                rs.next();
                assertEquals(4, rs.getInt(1));
            } catch (Exception e) {
                // you may not be able to execute this Prepared & Callable, so
                // this is an okay error, but only this!
                assertTrue(e.getMessage().indexOf("Unsupported feature") != -1);
            }
            
        } catch (Exception e) {
            fail(e.getMessage()+" due to error: "+getStackTrace(e));
        }
    }
    
    @Test
    public void testExecutionThreshold() {
        try {
            // Add some data into the table
            String update = "insert into stmt_test values (\'bob\', 5)";
            Statement statement = connection.createStatement();
            statement.executeUpdate(update);
            assertTrue(P6LogQuery.getLastEntry().indexOf(update) != -1);
            
            // set the execution threshold very low
            P6SpyOptions.setExecutionThreshold("0");
            
            // test a basic select
            String query = "select count(*) from stmt_test";
            ResultSet rs = statement.executeQuery(query);
            assertTrue(P6LogQuery.getLastEntry().indexOf(query) != -1);
            // finally just make sure the query executed!
            rs.next();
            assertTrue(rs.getInt(1) > 0);
            rs.close();
            
            // now increase the execution threshold and make sure the query is not captured
            P6SpyOptions.setExecutionThreshold("10000");
            
            // test a basic select
            String nextQuery = "select count(1) from stmt_test where 1 = 2";
            rs = statement.executeQuery(nextQuery);
            // make sure the previous query is still the last query
            assertTrue(P6LogQuery.getLastEntry().indexOf(query) != -1);
            // and of course that the new query isn't
            assertTrue(P6LogQuery.getLastEntry().indexOf(nextQuery) == -1);
            // finally just make sure the query executed!
            rs.next();
            assertEquals(0, rs.getInt(1));
            rs.close();
            
            P6SpyOptions.setExecutionThreshold("0");
            
            // finally, just make sure it now works as expected
            rs = statement.executeQuery(nextQuery);
            assertTrue(P6LogQuery.getLastEntry().indexOf(nextQuery) != -1);
            rs.next();
            assertEquals(0, rs.getInt(1));
            rs.close();
            
        } catch (Exception e) {
            fail(e.getMessage()+" due to error: "+getStackTrace(e));
        }
    }

    @After
    public void tearDownStatement() {
        try {
            Statement statement = connection.createStatement();
            drop(statement);
            statement.close();
        }  catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    protected void drop(Statement statement) {
        if (statement == null) { return; }
        dropStatement("drop table stmt_test", statement);
    }
    
    protected void dropStatement(String sql, Statement statement) {
        try {
            statement.execute(sql);
        } catch (Exception e) {
            // we don't really care about cleanup failing
        }
    }
}
