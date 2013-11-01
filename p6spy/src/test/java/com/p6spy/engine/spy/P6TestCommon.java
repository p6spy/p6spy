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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.appender.MultiLineFormat;
import com.p6spy.engine.spy.appender.SingleLineFormat;

@RunWith(Parameterized.class)
public class P6TestCommon extends P6TestFramework {

    private Statement statement;
  
    public P6TestCommon(String db) throws SQLException, IOException {
      super(db);
    }
    
    @Before
    public void setUpCommon() throws SQLException {
        try {
            statement = connection.createStatement();

            dropTestTable();
            statement.execute("create table common_test (col1 varchar(255), col2 integer)");
            // sample data
            {
              final String insert = "insert into common_test values (\'foo1\', 1)";
              final Statement statement = connection.createStatement();
              statement.executeUpdate(insert);
            }
            {
              final String insert = "insert into common_test values (\'foo2\', 2)";
              final Statement statement = connection.createStatement();
              statement.executeUpdate(insert);
            }
            
            statement.close();
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
          if (statement != null) {
            statement.close();
          }
          statement = connection.createStatement();
        }
    }
    
    @After
    public void tearDownCommon() throws SQLException {
        try {
            dropTestTable();
            statement.close();
        }  catch (Exception e) {
            fail(e.getMessage());
        } finally {
          if (statement != null) {
            statement.close();  
          }
        }
    }

    @Test
    public void testMatcher() throws SQLException {
        // first should match
        String query = "select count(*) from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));

        // now match still fail because table is excluded
        P6LogOptions.getActiveInstance().setExclude("common_test");
        query = "select 'x' from common_test";
        statement.executeQuery(query);
        P6LogOptions.getActiveInstance().setExclude("-common_test");
        assertFalse(super.getLastLogEntry().contains(query));

        tryRegEx();
    }

    protected void tryRegEx() throws SQLException  {
      // should match (basic)
      String query = "select 'y' from common_test";
      statement.executeQuery(query);
      assertTrue(super.getLastLogEntry().contains(query));

      // now match should match (test regex)
      P6LogOptions.getActiveInstance().setExclude("[a-z]ommon_test");
      query = "select 'x' from common_test";
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setExclude("-[a-z]ommon_test");
      assertFalse(super.getLastLogEntry().contains(query));

      // now match should fail (test regex again)
      P6LogOptions.getActiveInstance().setExclude("[0-9]tmt_test");
      query = "select 'z' from common_test";
      P6LogOptions.getActiveInstance().setExclude("-[0-9]tmt_test");
      statement.executeQuery(query);
      assertTrue(super.getLastLogEntry().contains(query));
    }

    @Test
    public void testCategories() throws Exception {
    	// we would like to see transactions in action here => prevent autocommit
    	connection.setAutoCommit(false);

    	try {
        // test rollback logging
        String query = "select 'y' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));
        connection.rollback();
        assertTrue(super.getLastLogEntry().contains("rollback"));

        // test commit logging
        query = "select 'y' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));
        connection.commit();
        assertTrue(super.getLastLogEntry().contains("commit"));

        // test debug logging
        P6LogOptions.getActiveInstance().setExclude("common_test");
        P6LogOptions.getActiveInstance().setExcludecategories("-debug");
        P6LogOptions.getActiveInstance().setIncludecategories("debug,info");
        query = "select 'y' from common_test";
        statement.executeQuery(query);
        P6LogOptions.getActiveInstance().setExclude("-common_test,debug");
        P6LogOptions.getActiveInstance().setIncludecategories("-debug,-info");
        assertTrue(super.getLastLogEntry().contains("intentionally"));

        // test result + resultset logging
        query = "select col1, col2 from common_test";
        P6LogOptions.getActiveInstance().setExcludecategories("-resultset,-result");
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
          String col1 = resultSet.getString("col1");
          assertTrue(col1.startsWith("foo"));
          // check result logged (just once)
//          assertFalse(super.getLastButOneLogEntry().contains("result"));
          assertTrue(super.getLastLogEntry().contains("result"));
          assertTrue(super.getLastLogEntry().contains(query));
        }

        assertTrue(super.getLastButOneLogEntry().contains("resultset"));
        assertTrue(super.getLastButOneLogEntry().contains(query));

        P6LogOptions.getActiveInstance().setExcludecategories("resultset,result");
        
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
    public void testMessageFormatStrategies() throws Exception {
      // SingleLineFormat case (by default)
      {
        String query = "select count(*) from common_test";
        statement.executeQuery(query);
        assertFalse(super.getLastLogEntry().contains("\n"));
      }
      
      // MultiLineFormat case
      {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(MultiLineFormat.class.getName());
        String query = "select count(*) from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains("\n"));
      }
      
      // reset to default line format strategy
      P6SpyOptions.getActiveInstance().setLogMessageFormat(SingleLineFormat.class.getName());
    }
    
    @Test
    public void testStacktrace() throws SQLException {
        P6SpyOptions.getActiveInstance().setStackTrace("true");
  
        // perform a query & make sure we get the stack trace
        String query = "select 'y' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));
        assertTrue(super.getLastLogStackTrace().contains("Stack"));
  
        // filter on stack trace that will not match
        super.clearLastLogStackTrace();
        P6SpyOptions.getActiveInstance().setStackTraceClass("com.dont.match");
        query = "select 'a' from common_test";
        statement.executeQuery(query);
        // this will actually match - just the stack trace wont fire
        assertTrue(super.getLastLogEntry().contains(query));
        assertNull(super.getLastLogStackTrace());
  
        super.clearLastLogStackTrace();
        P6SpyOptions.getActiveInstance().setStackTraceClass("com.p6spy");
        query = "select 'b' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));
        assertTrue(super.getLastLogStackTrace().contains("Stack"));
    }

    protected void dropTestTable() {
        if (statement == null) {
            return;
        }
        try {
            statement.execute("drop table common_test");
        } catch (Exception e) {
            // we don't really care about cleanup failing
        }
    }
}
