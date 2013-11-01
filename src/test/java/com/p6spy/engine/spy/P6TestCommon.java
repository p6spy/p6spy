/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
    public void testMatcher() throws SQLException {
        // first should match
        P6LogOptions.getActiveInstance().setFilter(Boolean.toString(true));
//        P6LogOptions.getActiveInstance().setExclude("");
//        P6LogOptions.getActiveInstance().setInclude("");
        Statement statement = connection.createStatement();
        try {
            String query = "select count(*) from common_test";
            statement.executeQuery(query);
            assertTrue(super.getLastLogEntry().contains(query));

            // now it should fail due to filter = false
            P6LogOptions.getActiveInstance().setFilter(Boolean.toString(false));
//            P6LogOptions.getActiveInstance().setExclude("");
//            P6LogOptions.getActiveInstance().setInclude("");
            query = "select 'w' from common_test";
            statement.executeQuery(query);
            assertTrue(super.getLastLogEntry().contains(query));

            // now match should still fail because table is excluded
            P6LogOptions.getActiveInstance().setFilter(Boolean.toString(true));
            P6LogOptions.getActiveInstance().setExclude("common_test");
//            P6LogOptions.getActiveInstance().setInclude("");
            query = "select 'x' from common_test";
            statement.executeQuery(query);
            P6LogOptions.getActiveInstance().setExclude("-common_test");
            assertFalse(super.getLastLogEntry().contains(query));

            tryRegEx();
        } finally {
          if (statement != null) {
            statement.close();
          }
        }
    }

    protected void tryRegEx() throws SQLException  {
        Statement statement = connection.createStatement();
        
        try {
          // should match (basic)
          P6LogOptions.getActiveInstance().setFilter(Boolean.toString(true));
//          P6LogOptions.getActiveInstance().setExclude("");
//          P6LogOptions.getActiveInstance().setInclude("");
          String query = "select 'y' from common_test";
          statement.executeQuery(query);
          assertTrue(super.getLastLogEntry().contains(query));
  
          // now match should match (test regex)
          P6LogOptions.getActiveInstance().setFilter(Boolean.toString(true));
          P6LogOptions.getActiveInstance().setExclude("[a-z]ommon_test");
          P6LogOptions.getActiveInstance().setInclude("");
          query = "select 'x' from common_test";
          statement.executeQuery(query);
          P6LogOptions.getActiveInstance().setExclude("-[a-z]ommon_test");
          assertFalse(super.getLastLogEntry().contains(query));
  
          // now match should fail (test regex again)
          P6LogOptions.getActiveInstance().setFilter(Boolean.toString(true));
          P6LogOptions.getActiveInstance().setExclude("[0-9]tmt_test");
//          P6LogOptions.getActiveInstance().setInclude("");
          query = "select 'z' from common_test";
          P6LogOptions.getActiveInstance().setExclude("-[0-9]tmt_test");
          statement.executeQuery(query);
          assertTrue(super.getLastLogEntry().contains(query));
        } finally {
          if (statement != null) {
            statement.close();
          }
        }
    }

    @Test
    public void testCategories() throws Exception {
    	// we would like to see transactions in action here => prevent autocommit
    	connection.setAutoCommit(false);

    	Statement statement = connection.createStatement();
    	try {
        // test rollback logging
      	P6LogOptions.getActiveInstance().setFilter(Boolean.toString(true));
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
        P6LogOptions.getActiveInstance().setFilter(Boolean.toString(true));
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
        P6LogOptions.getActiveInstance().setFilter(Boolean.toString(true));
        P6LogOptions.getActiveInstance().setExclude("common_test");
        P6LogOptions.getActiveInstance().setInclude("");
        P6LogOptions.getActiveInstance().setExcludecategories("-debug");
        P6LogOptions.getActiveInstance().setIncludecategories("debug,info");
        query = "select 'y' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains("intentionally"));

      } finally {
        if (statement != null) {
          statement.close();
        }
      }
    	
      // set back, otherwise we have problems in PostgresSQL, statement exec
      // waits for commit
      connection.setAutoCommit(true);
    }

    @Test
    public void testStacktrace() throws SQLException {
      // get a statement
      Statement statement = connection.createStatement();
      
        try {
            P6SpyOptions.getActiveInstance().setStackTrace("true");

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
            P6SpyOptions.getActiveInstance().setStackTraceClass("com.dont.match");
            query = "select 'a' from common_test";
            statement.executeQuery(query);
            // this will actually match - just the stack trace wont fire
            assertTrue(super.getLastLogEntry().contains(query));
            assertNull(super.getLastLogStackTrace());

            super.clearLastLogStackTrace();
            P6LogOptions.getActiveInstance().setFilter("true");
            P6LogOptions.getActiveInstance().setExclude("");
            P6LogOptions.getActiveInstance().setInclude("");
            P6SpyOptions.getActiveInstance().setStackTraceClass("com.p6spy");
            query = "select 'b' from common_test";
            statement.executeQuery(query);
            assertTrue(super.getLastLogEntry().contains(query));
            assertTrue(super.getLastLogStackTrace().contains("Stack"));

        } finally {
          if (statement != null) {
            statement.close();
          }
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
