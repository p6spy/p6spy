/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
 *
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
 */

package com.p6spy.engine.spy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.appender.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.option.SystemProperties;
import com.p6spy.engine.test.P6TestFramework;

@RunWith(Parameterized.class)
public class P6TestCommon extends P6TestFramework {

  Statement statement = null;

  public P6TestCommon(String db) throws SQLException, IOException {
    super(db);
  }

  @Before
  public void setUpCommon() throws SQLException {
    statement = connection.createStatement();
  }

  @Test
  public void testIncludeExclude() throws SQLException {

    final String query = "select 'x' from customers";

    // include null && exclude null => logged
    {
      super.clearLogEntries();

      assertNull(P6LogOptions.getActiveInstance().getExcludeList());
      assertNull(P6LogOptions.getActiveInstance().getIncludeList());
      statement.executeQuery(query);
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }

    // include empty && exclude empty => logged
    {
      super.clearLogEntries();

      // adding and clearing afterwards causes nulled sets
      P6LogOptions.getActiveInstance().setInclude("non_existing_table");
      P6LogOptions.getActiveInstance().setInclude("");
      P6LogOptions.getActiveInstance().setExclude("non_existing_table");
      P6LogOptions.getActiveInstance().setExclude("");

      assertNull(P6LogOptions.getActiveInstance().getIncludeList());
      assertNull(P6LogOptions.getActiveInstance().getExcludeList());
      statement.executeQuery(query);
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }

    // table is excluded => NOT logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setExclude(
          "non_existing_table1,customers,non_existing_table2,non_existing_table3");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setExclude("");
      assertEquals(0, super.getLogEntriesCount());
    }

    // table is included => logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setInclude("customers");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setInclude("");
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }

    // table is NOT included (but include is non-empty) => NOT logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setInclude("non_existing_table");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setInclude("");
      assertEquals(0, super.getLogEntriesCount());
    }
    
    // excluded - case insensitive => not logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setExclude("SELECT");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setExclude("");
      assertEquals(0, super.getLogEntriesCount());
    }

    // included - case insensitive => logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setInclude("SELECT");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setInclude("");
      assertEquals(1, super.getLogEntriesCount());
    }

    final String queryMultiline = "select * \nfrom customers";
    // excluded - multiline => not logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setExclude("customers");
      statement.executeQuery(queryMultiline);
      P6LogOptions.getActiveInstance().setExclude("");
      assertEquals(0, super.getLogEntriesCount());
    }

    // included - multiline => logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setInclude("customers");
      statement.executeQuery(queryMultiline);
      P6LogOptions.getActiveInstance().setInclude("");
      assertEquals(1, super.getLogEntriesCount());
    }
  }
  
  @Test
  public void testIncludeOtherTableShouldLogResultSet() throws SQLException {
      final String query = "select 'x' from customers";
      super.clearLogEntries();
      P6SpyOptions.getActiveInstance().setStackTrace("true");

      // other table is included => resultset is still logged
      P6LogOptions.getActiveInstance().setInclude("non_existing_table1");
      P6LogOptions.getActiveInstance().setExcludecategories("info,debug,batch");
      ResultSet rs = statement.executeQuery(query);
      while (rs.next()) {
          @SuppressWarnings("unused")
          String value = rs.getString(1);
      }
      P6LogOptions.getActiveInstance().setInclude("");
      assertEquals(0, super.getLogEntriesCount());
  }

  @Test
  public void testExcludeThisTableShouldLogResultSet() throws SQLException {
      super.clearLogEntries();
      final String query = "select 'x' from customers";

      // table is excluded => resultset is still logged
      P6LogOptions.getActiveInstance().setExclude("customers");
      P6LogOptions.getActiveInstance().setExcludecategories("info,debug,batch");
      ResultSet rs = statement.executeQuery(query);
      if (rs.next()) {
          @SuppressWarnings("unused")
          String value = rs.getString(1);
      }
      P6LogOptions.getActiveInstance().setExclude("");
      assertEquals(0, super.getLogEntriesCount());
  }


  @Test
  public void testIncludeExcludeRegexp() throws SQLException {
    final String query = "select 'y' from customers";


    // table is excluded (matches regexp) => NOT logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setExclude("[a-z]ustomers");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setExclude("");
      assertEquals(0, super.getLogEntriesCount());
    }

    // table is NOT excluded (doesn't match regexp) => logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setExclude("[0-9]tmt_test");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setExclude("");
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }
    
    // backslashes (have to be doubled)
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setExclude("from\\scustomer");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setExclude("");
      assertEquals(0, super.getLogEntriesCount());
    }
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setInclude("from\\scustomer");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setInclude("");
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }
  }
  
  @Test
  public void testSqlExpressionPattern() throws SQLException {
    final String query = "select 'y' from customers";

    // sql expression NOT matched => NOT logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setSQLExpression("^select[ ]'x'.*$");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().unSetSQLExpression();
      assertEquals(0, super.getLogEntriesCount());
    }

    // sql expression matched => NOT logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setSQLExpression("^select[ ]'y'.*$");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().unSetSQLExpression();
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }
    
    // multiline + case insensitive + dotall matched => logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setSQLExpression("(?mis)^.*FROM.*$");
      final String queryMultiline = "select * \nfrom customers";
      statement.executeQuery(queryMultiline);
      P6LogOptions.getActiveInstance().unSetSQLExpression();
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(P6Util.singleLine(queryMultiline)));
    }
    
    // backslashes (have to be doubled)
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setSQLExpression("^.*from\\scustomer.*$");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().unSetSQLExpression();
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }
  }
  
  @Test
  public void testIncludeExcludeWithSqlExpressionPattern() throws SQLException {
    final String query = "select 'x' from customers";
    
    // include/exclude passes AND sqlexpression does NOT pass => NOT logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setInclude("customers");
      P6LogOptions.getActiveInstance().setExclude("foo");
      P6LogOptions.getActiveInstance().setSQLExpression("^.*bar\\s.*$");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setInclude("");
      P6LogOptions.getActiveInstance().setExclude("");
      P6LogOptions.getActiveInstance().unSetSQLExpression();
      assertEquals(0, super.getLogEntriesCount());
    }
    
    // include/exclude does NOT pass AND sqlexpression passes => NOT logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setExclude("customers");
      P6LogOptions.getActiveInstance().setSQLExpression("^.*from\\s.*$");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setExclude("");
      P6LogOptions.getActiveInstance().unSetSQLExpression();
      assertEquals(0, super.getLogEntriesCount());
    }
    
    // include/exclude passes AND sqlexpression passes => logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setInclude("customers");
      P6LogOptions.getActiveInstance().setExclude("foo");
      P6LogOptions.getActiveInstance().setSQLExpression("^.*from\\s.*$");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setInclude("");
      P6LogOptions.getActiveInstance().setExclude("");
      P6LogOptions.getActiveInstance().unSetSQLExpression();
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }
    
    // include/exclude NOT SET AND sqlexpression passes => logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setSQLExpression("^.*from\\s.*$");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().unSetSQLExpression();
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }
    
    // include/exclude passes AND sqlexpression NOT SET => logged
    {
      super.clearLogEntries();
      P6LogOptions.getActiveInstance().setInclude("customers");
      P6LogOptions.getActiveInstance().setExclude("foo");
      statement.executeQuery(query);
      P6LogOptions.getActiveInstance().setInclude("");
      P6LogOptions.getActiveInstance().setExclude("");
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }
    
    // include/exclude NOT SET AND sqlexpression NOT SET => logged
    {
      super.clearLogEntries();
      statement.executeQuery(query);
      assertEquals(1, super.getLogEntriesCount());
      assertTrue(super.getLastLogEntry().contains(query));
    }
  }

  @Test
  public void testCategories() throws Exception {
    // we would like to see transactions in action here => prevent autocommit
    connection.setAutoCommit(false);

    // test rollback logging
    super.clearLogEntries();
    String query = "select 'y' from customers";
    statement.executeQuery(query);
    assertTrue(super.getLastLogEntry().contains(query));
    statement.close();  // required for sqllite
    connection.rollback();
    assertTrue(super.getLastLogEntry().contains(Category.ROLLBACK.toString()));
    statement = connection.createStatement();

    // test commit logging
    super.clearLogEntries();
    query = "select 'y' from customers";
    statement.executeQuery(query);
    assertTrue(super.getLastLogEntry().contains(query));
    connection.commit();
    assertTrue(super.getLastLogEntry().contains(Category.COMMIT.toString()));

    // test debug logging
    super.clearLogEntries();
    P6LogOptions.getActiveInstance().setExclude("customers");
    P6LogOptions.getActiveInstance().setExcludecategories("");
    query = "select 'y' from customers";
    statement.executeQuery(query);
    P6LogOptions.getActiveInstance().setExclude("");
    P6LogOptions.getActiveInstance().setExcludecategories("debug");
    assertTrue(super.getLastLogEntry().contains("intentionally"));

    // test result + resultset logging
    testResultAndResultSetCategory(true, true);
    testResultAndResultSetCategory(true, false);
    testResultAndResultSetCategory(false, true);
    testResultAndResultSetCategory(false, false);

    // set back, otherwise we have problems in PostgresSQL, statement exec
    // waits for commit
    connection.setAutoCommit(true);
  }

  private void testResultAndResultSetCategory(final boolean resultCategoryNotExcluded,
                                              final boolean resultsetCategoryNotExcluded)
      throws SQLException {
    final String query = "select id, name from customers where id in (1,2)";
    
    // debug would corrupt count checks here
    final StringBuilder sb = new StringBuilder(Category.DEBUG.toString());
    if (!resultCategoryNotExcluded) {
    	sb.append(",").append(Category.RESULT.toString());
    }
    if (!resultsetCategoryNotExcluded) {
    	sb.append(",").append(Category.RESULTSET.toString());
    }
    P6LogOptions.getActiveInstance().setExcludecategories(sb.toString());
    
    final ResultSet resultSet = statement.executeQuery(query);
    super.clearLogEntries();

    while (resultSet.next()) {
      String col1 = resultSet.getString("name");
      assertTrue(col1.startsWith("david") || col1.startsWith("mary"));
    }
    int resultCount = 0;
    int resultSetCount = 0;
    for (String logMessage : getLogEntries()) {
      if (logMessage.contains(Category.RESULT.toString()) && !logMessage.contains(Category.RESULTSET.toString())) {
        resultCount++;
      } else {
        resultSetCount++;
      }
    }
    assertEquals("incorrect number of log messages", resultCategoryNotExcluded ? 2 : 0, resultCount);
    assertEquals("incorrect number of log messages", resultsetCategoryNotExcluded ? 2 : 0, resultSetCount);

    resultSet.close();
    // reset back to original setup
//    P6LogOptions.getActiveInstance().setExcludecategories("resultset,result");

    if (!resultCategoryNotExcluded && !resultsetCategoryNotExcluded) {
      assertEquals(
          "if \"result\" \"resultset\" are in excludecategories they should NOT be logged", 0,
          super.getLogEntriesCount());
    } else {
      assertNotEquals(
          "if \"result\" \"resultset\" are NOT in excludecategories they should be logged", 0,
          super.getLogEntriesCount());
    }
  }

  @Test
  public void testMessageFormatStrategies() throws Exception {
    // SingleLineFormat case (by default)
    {
      String query = "select count(*) from customers";
      statement.executeQuery(query);
      assertFalse(super.getLastLogEntry().contains("\n"));
    }

    // MultiLineFormat case
    {
      P6SpyOptions.getActiveInstance().setLogMessageFormat(MultiLineFormat.class.getName());
      String query = "select count(*) from customers";
      statement.executeQuery(query);
      assertTrue(super.getLastLogEntry().contains("\n"));
    }

    // CustomLineFormat cases
    {
      // misconfiguration of CustomLineFormat cause it fall back to SingleLineFormat
      P6SpyOptions.getActiveInstance().setLogMessageFormat(CustomLineFormat.class.getName());
      P6SpyOptions.getActiveInstance().setCustomLogMessageFormat(null);


      String query = "select count(*) from customers";
      statement.executeQuery(query);
      assertFalse(super.getLastLogEntry().contains("\n"));
      assertTrue(super.getLastLogEntry().contains("select count(*) from customers"));

      // reset formatting setting
      P6SpyOptions.getActiveInstance().setCustomLogMessageFormat(null);
    }
    
    // prep statement
    {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(CustomLineFormat.class.getName());
        P6SpyOptions.getActiveInstance().setCustomLogMessageFormat("%(currentTime)|%(executionTime)|%(category)|connection%(connectionId)\n%(effectiveSqlSingleLine)\n%(sqlSingleLine);\n");

        String query = "select count(*) from customers where name IN (?, ?)";
        PreparedStatement prep = connection.prepareStatement(query);
        prep.setString(1, "foo");
        prep.setString(2, "bar");
        prep.executeQuery();
        assertTrue(super.getLastLogEntry().contains("\nselect count(*) from customers where name IN (?, ?)\nselect count(*) from customers where name IN ('foo', 'bar');"));

        // reset formatting setting
        P6SpyOptions.getActiveInstance().setCustomLogMessageFormat(null);
      }
    {
      P6SpyOptions.getActiveInstance().setLogMessageFormat(CustomLineFormat.class.getName());
      P6SpyOptions.getActiveInstance().setCustomLogMessageFormat("SQL in custom format: #"+
        CustomLineFormat.SQL+"#");


      String query = "select count(*) from customers";
      statement.executeQuery(query);
      assertEquals("SQL in custom format: #select count(*) from customers#", super.getLastLogEntry());

      // reset formatting setting
      P6SpyOptions.getActiveInstance().setCustomLogMessageFormat(null);
    }
    {
      P6SpyOptions.getActiveInstance().setLogMessageFormat(CustomLineFormat.class.getName());
      P6SpyOptions.getActiveInstance().setCustomLogMessageFormat("SQL in custom format: #"+
        CustomLineFormat.SQL +"#\n");


      String query = "select count(*) from customers";
      statement.executeQuery(query);
      assertEquals("SQL in custom format: #select count(*) from customers#\n", super.getLastLogEntry());

      // reset formatting setting
      P6SpyOptions.getActiveInstance().setCustomLogMessageFormat(null);
    }

    // reset to default line format strategy
    P6SpyOptions.getActiveInstance().setLogMessageFormat(SingleLineFormat.class.getName());

  }

  @Test
  public void testStacktrace() throws SQLException {
    P6SpyOptions.getActiveInstance().setStackTrace("true");

    // perform a query & make sure we get the stack trace
    String query = "select 'y' from customers";
    statement.executeQuery(query);
    assertTrue(super.getLastLogEntry().contains(query));
    assertTrue(super.getLastLogStackTrace().contains("Stack"));

    // filter on stack trace that will not match
    super.clearLastLogStackTrace();
    P6SpyOptions.getActiveInstance().setStackTraceClass("com.dont.match");
    query = "select 'a' from customers";
    statement.executeQuery(query);
    // this will actually match - just the stack trace wont fire
    assertTrue(super.getLastLogEntry().contains(query));
    assertNull(super.getLastLogStackTrace());

    super.clearLastLogStackTrace();
    P6SpyOptions.getActiveInstance().setStackTraceClass("com.p6spy");
    query = "select 'b' from customers";
    statement.executeQuery(query);
    assertTrue(super.getLastLogEntry().contains(query));
    assertTrue(super.getLastLogStackTrace().contains("Stack"));
  }

  @Test
  public void testAppenderReconfigurationTakesPlaceImediatelly() throws Exception {
    final String sql = "select count(*) from customers";
    P6TestLogger oldAppenderRef = null;
    
    // precondition - logging via P6TestLogger works OK
    {
      assertEquals(P6TestLogger.class.getName(), P6SpyOptions.getActiveInstance().getAppender());
      super.clearLogEntries();
      statement.executeQuery(sql);
      oldAppenderRef = (P6TestLogger) P6SpyOptions.getActiveInstance().getAppenderInstance();
      assertNotNull(oldAppenderRef.getLastEntry());
      assertTrue(oldAppenderRef.getLastEntry().contains(sql));
    }
    
    super.clearLogEntries();
    
    // let's log via StdoutLogger 
    P6SpyOptions.getActiveInstance().setAppender(StdoutLogger.class.getName());
    assertEquals(StdoutLogger.class.getName(), P6SpyOptions.getActiveInstance().getAppender());
    statement.executeQuery(sql);
    
    // old appender should not be in use any more!
    assertNull(oldAppenderRef.getLastEntry());
    
    // cleanup stuff - go for the default logger
    {
      P6SpyOptions.getActiveInstance().setAppender(P6TestLogger.class.getName());
    }
  }
  
  @Test
  public void testDisableLogModule() throws SQLException {
    P6SpyLoadableOptions o = P6SpyOptions.getActiveInstance();
    assertNotNull(P6LogOptions.getActiveInstance());

    clearLogEntries();
    statement.executeQuery("select 'x' from customers");
    // one log message should have been written - normal behavior
    assertEquals("A log message should have been written", 1, getLogEntriesCount());
    
    // hot module unload doesn't work
    { 
    	clearLogEntries();
    	o.setModulelist("");
    	
	    statement.executeQuery("select 'x' from customers");
	    assertEquals("A log message should not have been written", 1, getLogEntriesCount());
    }
    
    // module unload with reload works
    { 
    	clearLogEntries();
    	System.setProperty(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST,
    			"");
    	o.reload();
    	
	    statement.executeQuery("select 'x' from customers");
	    assertEquals("A log message should not have been written", 0, getLogEntriesCount());
	    System.clearProperty(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST);
    }
  }
  
}
