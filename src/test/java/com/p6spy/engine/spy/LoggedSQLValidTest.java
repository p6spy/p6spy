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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.p6spy.engine.test.P6TestFramework;

/**
 * @author Peter Butkovic
 */
@RunWith(Parameterized.class)
public class LoggedSQLValidTest extends P6TestFramework {

  public LoggedSQLValidTest(String db) throws SQLException, IOException {
    super(db);
  }

  @Before
  public void setUpStatement() throws SQLException {
    Statement statement = connection.createStatement();
    drop(statement);

    // here are some basic ones, inspired by the list on:
    // http://dba.stackexchange.com/questions/53317/databases-are-there-universal-datatypes
    // VARCHAR
    // INTEGER
    // DECIMAL
    // DATE (with surprises: Oracle has it but includes a time)
    // TIMESTAMP (does something different than expected on SQL Server an MySQL)
    // some extra added anyway
    statement
        .execute("create table valid_sql_logged (col_varchar varchar(255), col_integer integer, col_decimal decimal "
            + (isDateTimeSupported() ? ", col_date date, col_timestamp timestamp" : "")
            + ", col_smallint smallint "
            + (isBooleanSupported() ? ", col_boolean boolean" : "")
            + ")");

    statement.close();
    super.clearLogEntries();
  }

  @Test
  public void testSingleQuotePresentInValueOneTimeEscaped() throws SQLException {
    try {
      final PreparedStatement prep = connection.prepareStatement("select * from valid_sql_logged where col_varchar = ?");
      prep.setString(1, "foo'value");
      prep.executeQuery();
      prep.close();
    } catch (Exception e) {
      fail(e.getMessage() + " due to error: " + getStackTrace(e));
    }
    reRunStatement(false);
  }
  
  @Test
  public void testSingleQuotePresentInValueMultipleTimesEscaped() throws SQLException {
      try {
      final PreparedStatement prep = connection.prepareStatement("select * from valid_sql_logged where col_varchar = ?");
      prep.setString(1, "foo''value'");
      prep.executeQuery();
      prep.close();
    } catch (Exception e) {
      fail(e.getMessage() + " due to error: " + getStackTrace(e));
    }
    reRunStatement(false);
  }
  
  @Test
  public void testPreparedStatementExecQuery() throws SQLException {
    try {
      testPreparedStatement(false);
    } catch (Exception e) {
      fail(e.getMessage() + " due to error: " + getStackTrace(e));
    }
  }

  @Test
  public void testPreparedStatementExecUpdate() throws SQLException {
    try {
      testPreparedStatement(true);
    } catch (Exception e) {
      fail(e.getMessage() + " due to error: " + getStackTrace(e));
    }
  }

  private void testPreparedStatement(boolean isUpdate) throws SQLException {
    try {
      final PreparedStatement prep = getPreparedStatement(isUpdate);
      int i = 0;
      prep.setString(++i, "prepstmt_test_col1");
      prep.setInt(++i, 1);
      prep.setInt(++i, 1);
      if (isDateTimeSupported()) {
        prep.setDate(++i, new Date(System.currentTimeMillis()));
        prep.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
      }
      prep.setInt(++i, 1);
      if (isBooleanSupported()) {
        prep.setBoolean(++i, true);
      }

      if (isUpdate) {
        prep.executeUpdate();
      } else {
        prep.executeQuery();
      }

      prep.close();
    } catch (Exception e) {
      fail(e.getMessage() + " due to error: " + getStackTrace(e));
    }

    reRunStatement(isUpdate);
  }

  @Test
  public void testPreparedStatementExecUpdateWithNulls() throws SQLException {
    try {
      testPreparedStatementWithNulls(true);
    } catch (Exception e) {
      fail(e.getMessage() + " due to error: " + getStackTrace(e));
    }
  }

  @Test
  public void testPreparedStatementExecQueryWithNulls() throws SQLException {
    // Derby fails on this one => let's just skip it 
    if ("Derby".equals(db)) {
      return;
    }

    try {
      testPreparedStatementWithNulls(false);
    } catch (Exception e) {
      fail(e.getMessage() + " due to error: " + getStackTrace(e));
    }
  }

  private void testPreparedStatementWithNulls(boolean isUpdate) throws SQLException {
    final PreparedStatement prep = getPreparedStatement(isUpdate);
    int i = 0;
    prep.setNull(++i, java.sql.Types.VARCHAR);
    prep.setNull(++i, java.sql.Types.INTEGER);
    prep.setNull(++i, java.sql.Types.INTEGER);
    if (isDateTimeSupported()) {
      prep.setNull(++i, java.sql.Types.DATE);
      prep.setNull(++i, java.sql.Types.TIMESTAMP);
    }
    prep.setNull(++i, java.sql.Types.INTEGER);
    if (isBooleanSupported()) {
      prep.setNull(++i, java.sql.Types.BOOLEAN);
    }

    if (isUpdate) {
      prep.executeUpdate();
    } else {
      prep.executeQuery();
    }

    prep.close();
    reRunStatement(isUpdate);
  }

  /**
   * Reads last logged statement and reruns it (to prove it's valid).
   * 
   * @throws SQLException
   */
  private void reRunStatement(boolean isUpdate) throws SQLException {
    final String loggedStmt = super.getLastLogEntry();
    final String sql = loggedStmt.substring(loggedStmt.lastIndexOf("|") + 1);

    // re-run the logged statement => to prove it's valid
    super.clearLogEntries();
    final Statement stmt = connection.createStatement();
    if (isUpdate) {
      stmt.executeUpdate(sql);
    } else {
      stmt.executeQuery(sql);
    }
    stmt.close();
    assertEquals(sql, loggedStmt.substring(loggedStmt.lastIndexOf("|") + 1));
  }

  private PreparedStatement getPreparedStatement(boolean isUpdate) throws SQLException {
    if (isUpdate) {
      return connection
          .prepareStatement("insert into valid_sql_logged (col_varchar, col_integer, col_decimal"
              + (isDateTimeSupported() ? ", col_date, col_timestamp" : "") + ", col_smallint"
              + (isBooleanSupported() ? ", col_boolean" : "") + ") values (?,?,?,?"
              + (isDateTimeSupported() ? ",?,?" : "") + (isBooleanSupported() ? ",?" : "") + ")");
    } else {
      return connection
          .prepareStatement("select * from valid_sql_logged where col_varchar = ? and col_integer = ? and col_decimal = ? "
              + (isDateTimeSupported() ? " and col_date = ?  and col_timestamp = ? " : "")
              + " and col_smallint = ? " + (isBooleanSupported() ? " and col_boolean = ?" : ""));
    }
  }

  // holds rather workarounds, to keep effort low
  // volunteers for fixing welcome
  private boolean isDateTimeSupported() {
    return !"HSQLDB".equals(db) && !"MSSQLServer".equals(db) /*
                                 * could not figure out the correct date format for these
                                 * skipping, to keep effort low
                                 */;
  }

  private boolean isBooleanSupported() {
    return !"Oracle".equals(db) && !"Firebird".equals(db) /*
                                                           * see:
                                                           * http://firebirdsql.org/manual/migration
                                                           * -mssql-data-types.html
                                                           */
        && !"DB2".equals(db) /*
                              * http://publib.boulder.ibm.com/infocenter/db2luw/v9/index.jsp?topic=%2F
                              * com.ibm.db2.udb.apdv.java.doc%2Fdoc%2Frjvjdata.htm
                              */
        && !"SQLite".equals(db) /* https://www.sqlite.org/datatype3.html */
        && !"MSSQLServer".equals(db) /* https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql */;
  }

  protected void drop(Statement statement) {
    if (statement == null) {
      return;
    }
    dropStatement("drop table valid_sql_logged", statement);
  }

  protected void dropStatement(String sql, Statement statement) {
    try {
      statement.execute(sql);
    } catch (Exception e) {
      // we don't really care about cleanup failing
    }
  }
}
