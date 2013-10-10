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
 * $Author: cheechq $
 * $Revision: 1.5 $
 * $Date: 2003/06/03 19:20:26 $
 *
 * $Id: P6TestCommon.java,v 1.5 2003/06/03 19:20:26 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestCommon.java,v $
 * $Log: P6TestCommon.java,v $
 * Revision 1.5  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.4  2003/02/14 22:22:57  aarvesen
 * use a define for the property file
 *
 * Revision 1.3  2003/01/28 17:59:12  jeffgoke
 * fixed test cases to use new options
 *
 * Revision 1.2  2003/01/03 21:19:24  aarvesen
 * use the new P6Util.forName
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.p6spy.engine.logging.P6LogOptions;

@RunWith(Parameterized.class)
public class P6TestCommon extends P6TestFramework {

    public P6TestCommon(String db) throws SQLException, IOException {
      super(db);
    }

    @Before
    public void setUpCommon() {
        try {
        	Statement statement = connection.createStatement();
            drop(statement);
            statement.execute("create table common_test (col1 varchar(255), col2 integer)");
            statement.close();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testMatcher() {
        try {

            // first should match
            P6LogOptions.getActiveInstance().setFilter("true");
            P6LogOptions.getActiveInstance().setExclude("");
            P6LogOptions.getActiveInstance().setInclude("");
            Statement statement = connection.createStatement();
            String query = "select count(*) from common_test";
            statement.executeQuery(query);
            assertTrue(super.getLastLogEntry().contains(query));

            // now it should fail due to filter = false
            P6LogOptions.getActiveInstance().setFilter("false");
            P6LogOptions.getActiveInstance().setExclude("");
            P6LogOptions.getActiveInstance().setInclude("");
            query = "select 'w' from common_test";
            statement.executeQuery(query);
            assertTrue(super.getLastLogEntry().contains(query));

            // now match should still fail because table is excluded
            P6LogOptions.getActiveInstance().setFilter("true");
            P6LogOptions.getActiveInstance().setExclude("common_test");
            P6LogOptions.getActiveInstance().setInclude("");
            query = "select 'x' from common_test";
            statement.executeQuery(query);
            assertFalse(super.getLastLogEntry().contains(query));

            tryRegEx();
        } catch (Exception e) {
            fail(e.getMessage()+getStackTrace(e));
        }
    }

    protected void tryRegEx() throws Exception {
        Statement statement = connection.createStatement();

        // should match (basic)
        P6LogOptions.getActiveInstance().setFilter("true");
        P6LogOptions.getActiveInstance().setExclude("");
        P6LogOptions.getActiveInstance().setInclude("");
        String query = "select 'y' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));

        // now match should match (test regex)
        P6LogOptions.getActiveInstance().setFilter("true");
        P6LogOptions.getActiveInstance().setExclude("[a-z]ommon_test");
        P6LogOptions.getActiveInstance().setInclude("");
        query = "select 'x' from common_test";
        statement.executeQuery(query);
        assertFalse(super.getLastLogEntry().contains(query));

        // now match should fail (test regex again)
        P6LogOptions.getActiveInstance().setFilter("true");
        P6LogOptions.getActiveInstance().setExclude("[0-9]tmt_test");
        P6LogOptions.getActiveInstance().setInclude("");
        query = "select 'z' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));
    }

    @Test
    public void testCategories() throws Exception {
    	// we would like to see transactions in action here => prevent autocommit
    	connection.setAutoCommit(false);

    	Statement statement = connection.createStatement();
        
        // test rollback logging
      	P6LogOptions.getActiveInstance().setFilter("true");
        P6LogOptions.getActiveInstance().setExclude("");
        P6LogOptions.getActiveInstance().setInclude("");
        P6LogOptions.getActiveInstance().setExcludecategories("");
        P6LogOptions.getActiveInstance().setIncludecategories("");
        String query = "select 'y' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));
        connection.rollback();
        assertTrue(super.getLastLogEntry().contains("rollback"));

        // test commit logging
        P6LogOptions.getActiveInstance().setFilter("true");
        P6LogOptions.getActiveInstance().setExclude("");
        P6LogOptions.getActiveInstance().setInclude("");
        P6LogOptions.getActiveInstance().setExcludecategories("");
        P6LogOptions.getActiveInstance().setIncludecategories("");
        query = "select 'y' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));
        connection.commit();
        assertTrue(super.getLastLogEntry().contains("commit"));

        // test debug logging
        P6LogOptions.getActiveInstance().setFilter("true");
        P6LogOptions.getActiveInstance().setExclude("common_test");
        P6LogOptions.getActiveInstance().setInclude("");
        P6LogOptions.getActiveInstance().setExcludecategories("");
        P6LogOptions.getActiveInstance().setIncludecategories("debug,info");
        query = "select 'y' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains("intentionally"));

        // set back, otherwise we have problems in PostgresSQL, statement exec
        // waits for commit
        connection.setAutoCommit(true);
    }

    @Test
    public void testStacktrace() {
        try {
            // get a statement
            Statement statement = connection.createStatement();
            P6LogOptions.getActiveInstance().setStackTrace("true");

            // perform a query & make sure we get the stack trace
            P6LogOptions.getActiveInstance().setFilter("true");
            P6LogOptions.getActiveInstance().setExclude("");
            P6LogOptions.getActiveInstance().setInclude("");
            String query = "select 'y' from common_test";
            statement.executeQuery(query);
            assertTrue(super.getLastLogEntry().contains(query));
            assertTrue(super.getLastLogStackTrace().contains("Stack"));

            // filter on stack trace that will not match
            super.clearLastLogStackTrace();
            P6LogOptions.getActiveInstance().setFilter("true");
            P6LogOptions.getActiveInstance().setExclude("");
            P6LogOptions.getActiveInstance().setInclude("");
            P6LogOptions.getActiveInstance().setStackTraceClass("com.dont.match");
            query = "select 'a' from common_test";
            statement.executeQuery(query);
            // this will actually match - just the stack trace wont fire
            assertTrue(super.getLastLogEntry().contains(query));
            assertNull(super.getLastLogStackTrace());

            super.clearLastLogStackTrace();
            P6LogOptions.getActiveInstance().setFilter("true");
            P6LogOptions.getActiveInstance().setExclude("");
            P6LogOptions.getActiveInstance().setInclude("");
            P6LogOptions.getActiveInstance().setStackTraceClass("com.p6spy");
            query = "select 'b' from common_test";
            statement.executeQuery(query);
            assertTrue(super.getLastLogEntry().contains(query));
            assertTrue(super.getLastLogStackTrace().contains("Stack"));

        } catch (Exception e) {
            fail(e.getMessage()+getStackTrace(e));
        }
    }

    @After
    public void tearDownCommon() {
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
        dropStatement("drop table common_test", statement);
    }

    protected void dropStatement(String sql, Statement statement) {
        try {
            statement.execute(sql);
        } catch (Exception e) {
            // we don't really care about cleanup failing
        }
    }
}
