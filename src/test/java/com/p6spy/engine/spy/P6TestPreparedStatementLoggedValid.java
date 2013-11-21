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

import static org.junit.Assert.assertEquals;

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
public class P6TestPreparedStatementLoggedValid extends P6TestFramework {

  public P6TestPreparedStatementLoggedValid(String db) throws SQLException, IOException {
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
        .execute("create table valid_sql_logged (col_varchar varchar(255), col_integer integer, col_decimal decimal, col_date date, col_timestamp timestamp, col_smallint smallint"
            + (isBooleanSupported() ? ", col_boolean boolean" : "") + ")");

    statement.close();
    super.clearLogEnties();
  }

  @Test
  public void testPreparedStatementExecQuery() throws SQLException {
    testPreparedStatement(false);
  }
  
  @Test
  public void testPreparedStatementExecUpdate() throws SQLException {
    testPreparedStatement(true);
  }
  
  private void testPreparedStatement(boolean isUpdate) throws SQLException {
    final PreparedStatement prep = getPreparedStatement(isUpdate);
    prep.setString(1, "prepstmt_test_col1");
    prep.setInt(2, 1);
    prep.setInt(3, 1);
    prep.setDate(4, new Date(0));
    prep.setTimestamp(5, new Timestamp(0));
    prep.setInt(6, 1);
    if (isBooleanSupported()) {
      prep.setBoolean(7, true);  
    }
    prep.executeQuery();
    prep.close();

    reRunStatement(isUpdate);
  }

  @Test
  public void testPreparedStatementExecUpdateWithNulls() throws SQLException {
    testPreparedStatementWithNulls(true);
  }
  
  @Test
  public void testPreparedStatementExecQueryWithNulls() throws SQLException {
    testPreparedStatementWithNulls(false); 
  }
  
  private void testPreparedStatementWithNulls(boolean isUpdate) throws SQLException {
    final PreparedStatement prep = getPreparedStatement(isUpdate);
    prep.setNull(1, java.sql.Types.VARCHAR);
    prep.setNull(2, java.sql.Types.INTEGER);
    prep.setNull(3, java.sql.Types.INTEGER);
    prep.setNull(4, java.sql.Types.DATE);
    prep.setNull(5, java.sql.Types.TIMESTAMP);
    prep.setNull(6, java.sql.Types.INTEGER);
    if (isBooleanSupported()) {
      prep.setNull(7, java.sql.Types.BOOLEAN);  
    }    
    prep.executeQuery();
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
    super.clearLogEnties();
    final Statement stmt = connection.createStatement();
    if (isUpdate) {
      stmt.executeUpdate(sql);
    } else {
      stmt.execute(sql);      
    }
    stmt.close();
    assertEquals(sql, loggedStmt.substring(loggedStmt.lastIndexOf("|") + 1));
  }

  private PreparedStatement getPreparedStatement(boolean isUpdate) throws SQLException {
    if (isUpdate) {
      return connection
          .prepareStatement("insert into valid_sql_logged (col_varchar, col_integer, col_decimal, col_date, col_timestamp, col_smallint"
              + (isBooleanSupported() ? ", col_boolean" : "") + ") values (?,?,?,?,?,?" + (isBooleanSupported() ? ",?" : "") + ")");
    } else {
      return connection
          .prepareStatement("select * from valid_sql_logged where col_varchar = ? and col_integer = ? and col_decimal = ? and col_date = ? and col_timestamp = ? and col_smallint = ?"
              + (isBooleanSupported() ? " and col_boolean = ?" : ""));
    }
  }
  

  private boolean isBooleanSupported() {
    return !"Oracle".equals(db) 
        && !"Firebird".equals(db) /* see: http://firebirdsql.org/manual/migration-mssql-data-types.html */
        && !"DB2".equals(db) /* http://publib.boulder.ibm.com/infocenter/db2luw/v9/index.jsp?topic=%2Fcom.ibm.db2.udb.apdv.java.doc%2Fdoc%2Frjvjdata.htm */ 
        && !"SQLite".equals(db) /* https://www.sqlite.org/datatype3.html */;
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
