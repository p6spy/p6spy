/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.p6spy.engine.spy;

import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.appender.MultiLineFormat;
import com.p6spy.engine.spy.appender.SingleLineFormat;
import com.p6spy.engine.test.P6TestFramework;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

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
              statement.executeUpdate(insert);
            }
            {
              final String insert = "insert into common_test values (\'foo2\', 2)";
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
        }  catch (Exception e) {
            fail(e.getMessage());
        } finally {
          if (statement != null) {
            statement.close();  
          }
        }
    }

    @Test
    public void testIncludeExcludeTableNames() throws SQLException {

      final String query = "select 'x' from common_test";
      final String countQuery = "select count(*) from common_test";

      // include null && exclude null => logged
      {
        super.clearLogEnties();
        
        assertNull(P6LogOptions.getActiveInstance().getExcludeTables());
        assertNull(P6LogOptions.getActiveInstance().getIncludeTables());
        statement.executeQuery(query);
        assertEquals(1, super.getLogEntiesCount());
        assertTrue(super.getLastLogEntry().contains(query));
      }

      // include empty && exclude empty => logged
      {
        super.clearLogEnties();

        // adding and removing afterwards causes empty set
        P6LogOptions.getActiveInstance().setInclude("non_existing_table");
        P6LogOptions.getActiveInstance().setInclude("-non_existing_table");
        P6LogOptions.getActiveInstance().setExclude("non_existing_table");
        P6LogOptions.getActiveInstance().setExclude("-non_existing_table");
        
        assertEquals(0, P6LogOptions.getActiveInstance().getIncludeTables().size());
        assertEquals(0, P6LogOptions.getActiveInstance().getExcludeTables().size());
        statement.executeQuery(query);
        assertEquals(1, super.getLogEntiesCount());
        assertTrue(super.getLastLogEntry().contains(query));
      }
      
      // table is excluded => NOT logged
      {
        super.clearLogEnties();
        P6LogOptions.getActiveInstance().setExclude(
            "non_existing_table1,common_test,non_existing_table2,non_existing_table3");
        statement.executeQuery(query);
        P6LogOptions.getActiveInstance().setExclude(
            "-non_existing_table1,-common_test,-non_existing_table2,-non_existing_table3");
        assertEquals(0, super.getLogEntiesCount());
      }

      // table is included => logged
      {
        super.clearLogEnties();
        P6LogOptions.getActiveInstance().setInclude("common_test");
        statement.executeQuery(query);
        P6LogOptions.getActiveInstance().setInclude("-common_test");
        assertEquals(1, super.getLogEntiesCount());
        assertTrue(super.getLastLogEntry().contains(query));
      }

      // table is NOT included (but include is non-empty) => NOT logged
      {
        super.clearLogEnties();
        P6LogOptions.getActiveInstance().setInclude("non_existing_table");
        statement.executeQuery(query);
        P6LogOptions.getActiveInstance().setInclude("-non_existing_table");
        assertEquals(0, super.getLogEntiesCount());
      }
    }

    @Test
    public void testIncludeExcludeTableNamesRegexp() throws SQLException  {
      final String query = "select 'y' from common_test";
      
      // table is excluded (matches regexp) => NOT logged
      {
        super.clearLogEnties();
        P6LogOptions.getActiveInstance().setExclude("[a-z]ommon_test");
        statement.executeQuery(query);
        P6LogOptions.getActiveInstance().setExclude("-[a-z]ommon_test");
        assertEquals(0, super.getLogEntiesCount());      
      }
      
      // table is NOT excluded (doesn't match regexp) => logged
      {
        super.clearLogEnties();
        P6LogOptions.getActiveInstance().setExclude("[0-9]tmt_test");
        statement.executeQuery(query);
        P6LogOptions.getActiveInstance().setExclude("-[0-9]tmt_test");
        assertEquals(1, super.getLogEntiesCount());
        assertTrue(super.getLastLogEntry().contains(query));
      }
    }

    @Test
    public void testSqlExpressionPattern() throws SQLException  {
      final String query = "select 'y' from common_test";
      
      // sql expression NOT matched => NOT logged
      {
        super.clearLogEnties();
        P6LogOptions.getActiveInstance().setSQLExpression("^select[ ]'x'.*$");
        statement.executeQuery(query);
        P6LogOptions.getActiveInstance().setSQLExpression("-^select[ ]'x'.*$");
        assertEquals(0, super.getLogEntiesCount());
      }
      
      // sql expression matched => NOT logged
      {
        super.clearLogEnties();
        P6LogOptions.getActiveInstance().setSQLExpression("^select[ ]'y'.*$");
        statement.executeQuery(query);
        P6LogOptions.getActiveInstance().setSQLExpression("-^select[ ]'y'.*$");
        assertEquals(1, super.getLogEntiesCount());
        assertTrue(super.getLastLogEntry().contains(query));
      }
    }

    @Test
    public void testCategories() throws Exception {
      // we would like to see transactions in action here => prevent autocommit
    	connection.setAutoCommit(false);

    	try {
        // test rollback logging
    	  super.clearLogEnties();
        String query = "select 'y' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));
        statement.close();
        connection.rollback();
        assertTrue(super.getLastLogEntry().contains("rollback"));

        statement = connection.createStatement();
        // test commit logging
        super.clearLogEnties();
        query = "select 'y' from common_test";
        statement.executeQuery(query);
        assertTrue(super.getLastLogEntry().contains(query));
        connection.commit();
        assertTrue(super.getLastLogEntry().contains("commit"));

        // test debug logging
        super.clearLogEnties();
        P6LogOptions.getActiveInstance().setExclude("common_test");
        P6LogOptions.getActiveInstance().setExcludecategories("-debug");
        query = "select 'y' from common_test";
        statement.executeQuery(query);
        P6LogOptions.getActiveInstance().setExclude("-common_test");
        P6LogOptions.getActiveInstance().setExcludecategories("debug");
        assertTrue(super.getLastLogEntry().contains("intentionally"));

        // test result + resultset logging
        testResultAndResultSetCategory(true, true);
        testResultAndResultSetCategory(true, false);
        testResultAndResultSetCategory(false, true);
        testResultAndResultSetCategory(false, false);
  
      } finally {
        if (statement != null) {
          statement.close();
        }
      }
  
      // set back, otherwise we have problems in PostgresSQL, statement exec
      // waits for commit
      connection.setAutoCommit(true);
    }
  
    private void testResultAndResultSetCategory(final boolean resultCategoryNotExcluded,
                                                final boolean resultsetCategoryNotExcluded)
        throws SQLException {
      final String query = "select col1, col2 from common_test";
      P6LogOptions.getActiveInstance().setExcludecategories(
          (resultCategoryNotExcluded ? "-" : "") + "result," + (resultsetCategoryNotExcluded ? "-" : "")
              + "resultset");
      final ResultSet resultSet = statement.executeQuery(query);
      super.clearLogEnties();
  
      while (resultSet.next()) {
        String col1 = resultSet.getString("col1");
        assertTrue(col1.startsWith("foo"));
      }
      int resultCount = 0;
      int resultSetCount = 0;
      for( String logMessage : getLogEnties() ) {
        if( logMessage.contains("result") && !logMessage.contains("resultset")) {
          resultCount++;
        } else {
          resultSetCount++;
        }
      }
      assertEquals("incorrect number of log messages", resultCategoryNotExcluded ? 2 : 0, resultCount);
      assertEquals("incorrect number of log messages", resultsetCategoryNotExcluded ? 2 :0 , resultSetCount);
      
      resultSet.close();
      // reset back to original setup
      P6LogOptions.getActiveInstance().setExcludecategories("resultset,result");
  
      if (!resultCategoryNotExcluded && !resultsetCategoryNotExcluded) {
        assertEquals(
            "if \"result\" \"resultset\" are in excludecategories they should NOT be logged", 0,
            super.getLogEntiesCount());
      } else {
        assertNotEquals(
            "if \"result\" \"resultset\" are NOT in excludecategories they should be logged", 0,
            super.getLogEntiesCount());
      }
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
