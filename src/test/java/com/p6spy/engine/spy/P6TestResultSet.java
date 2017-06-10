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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.test.P6TestFramework;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class P6TestResultSet extends P6TestFramework {

  ResultSet resultSet = null;

  public P6TestResultSet(String db) throws SQLException, IOException {
    super(db);
  }

  @Before
  public void setup() throws SQLException {
    resultSet = executeQuery("select id from customers where id in (1,2,3,4) order by id");

    P6LogOptions.getActiveInstance().setExcludecategories("info,debug,result");
    clearLogEntries();
    clearLastLogStackTrace();
  }

  private ResultSet executeQuery(String sql) throws SQLException {
    Statement statement = connection.createStatement();
    return statement.executeQuery(sql);
  }

  @Test
  public void testSkipLoggingBeforeFirstRowOnNext() throws SQLException {
    // The first call to ResultSet.next() should not log anything since
    // it is the advance to the first row.  No columns could have been accessed.

    //when
    resultSet.next();

    //then
    assertEquals("Incorrect number of log messages", 0, getLogEntriesCount());
  }

  @Test
  public void testSkipLoggingBeforeFirstRowOnClose() throws SQLException {
    // Calling ResultSet.close() before the first row is accessed should not log anything

    //when
    resultSet.close();

    //then
    assertEquals("Incorrect number of log messages", 0, getLogEntriesCount());
  }

  @Test
  public void testLoggingOnNext() throws SQLException {
    // Columns accessed on the current row should be logged when
    // ResultSet.next() is called.

    // given
    resultSet.next();  // advance to first row
    
    //when
    resultSet.getInt("id");
    resultSet.next();

    //then
    assertEquals("Incorrect number of log messages", 1, getLogEntriesCount());
  }

  @Test
  public void testLoggingOnNextWithNoColumnsAccessed() throws SQLException {
    // Even rows on which no columns have been accessed should be logged

    // given
    resultSet.next();  // advance to first row
    
    //when
    resultSet.next();

    //then
    assertEquals("Incorrect number of log messages", 1, getLogEntriesCount());
  }

  @Test
  public void testLoggingOnCloseBeforeEndOfResultSet() throws SQLException {
    // Calling ResultSet.close() after ResultSet.next() returns true should result in a
    // log message.

    // given
    resultSet.next();  // advance to first row
    
    //when
    resultSet.getInt("id");
    resultSet.close();

    //then
    assertEquals("Incorrect number of log messages", 1, getLogEntriesCount());
  }

  @Test
  public void testLoggingOnCloseAfterEndOfResultSet() throws SQLException {
    // Calling ResultSet.close() after ResultSet.next() returns false should not result in a
    // log message.

    // given
    while( resultSet.next() ) {
      resultSet.getInt("id");
    }
    clearLogEntries();

    //when
    resultSet.close();

    //then
    assertEquals("Incorrect number of log messages", 0, getLogEntriesCount());
  }

  @Test
  public void testLogMessageCountWhenAllRowsRead() throws SQLException {
    // The query will return 4 records.  4 log messages will be created.

    // when
    while( resultSet.next() ) {
      resultSet.getInt("id");
    }
    resultSet.close();

    //then
    assertEquals("Incorrect number of log messages", 4, getLogEntriesCount());
    assertTrue(getLogEntries().get(0).contains("id = 1"));
    assertTrue(getLogEntries().get(1).contains("id = 2"));
    assertTrue(getLogEntries().get(2).contains("id = 3"));
    assertTrue(getLogEntries().get(3).contains("id = 4"));
  }

  @Test
  public void testLogMessageCountWhenLessThanAllRowsAreRead() throws SQLException {
    // The query will return 4 records.  Only the first record will be accessed.

    // given
    resultSet.next();  // advance to first row
    
    // when
    resultSet.getInt("id");
    resultSet.close();

    //then
    assertEquals("Incorrect number of log messages", 1, getLogEntriesCount());
  }

  @Test
  public void testNoLoggingOnEmptyResultSetOnClose() throws SQLException {
    // Calls to ResultSet.close() should not produce
    // log messages if no rows were returned

    // given
    resultSet = executeQuery("select id from customers where 1=0");
    clearLogEntries();

    //when
    resultSet.next(); // this will return false
    resultSet.close();

    // then
    assertEquals("Incorrect number of log messages", 0, getLogEntriesCount());
  }

  @Test
  public void testResultLogging() throws SQLException {
    // Every call to ResultSet.next() that returns true should log the execution time in RESULT category 

    // given
    P6LogOptions.getActiveInstance().setExcludecategories("info,debug,resultset");
    int rowCount = 0;

    // when
    while( resultSet.next() ) {
      rowCount++;
    }

    // then
    assertEquals("Incorrect number of log messages", rowCount, getLogEntriesCount());
  }

}
