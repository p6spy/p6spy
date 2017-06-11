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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.test.P6TestFramework;
import com.p6spy.engine.wrapper.AbstractWrapper;

@RunWith(Parameterized.class)
public class P6TestPreparedStatement extends P6TestFramework {

  private boolean originalExcludeBinaryFlag;
 
  public P6TestPreparedStatement(String db) throws SQLException, IOException {
    super(db);
  }
 
  @Before
  public void setUpPreparedStatement() {
    P6LogOptions.getActiveInstance().setExcludecategories("info,debug,result");
    try {
      {
        Statement statement = connection.createStatement();
        dropPrepared(statement);
        statement.execute("create table prepstmt_test (col1 varchar(255), col2 integer)");
        statement.execute("create table prepstmt_test2 (col1 varchar(255), col2 integer)");
        statement.execute("create table prepstmt_test3  (col1 timestamp)");
        statement.close();
      }
    } catch (Exception e) {
      fail(e.getMessage() + " due to error:\n" + getStackTrace(e));
    }
  }

  @Before
  public void before() {
    this.originalExcludeBinaryFlag = P6LogOptions.getActiveInstance().getExcludebinary();
  }
  
  @After
  public void after() {
    P6LogOptions.getActiveInstance().setExcludebinary(this.originalExcludeBinaryFlag);
  }
  
  @Test
  public void testExecuteQuery() {
    try {
      // insert test data
      String update = "insert into prepstmt_test values (?, ?)";
      PreparedStatement prep = getPreparedStatement(update);
      prep.setString(1, "execQueryTest");
      prep.setInt(2, 1);
      prep.executeUpdate();
      prep.setString(1, "execQueryTest");
      prep.setInt(2, 2);
      prep.executeUpdate();
      prep.close();
      
      String query = "select * from prepstmt_test where col1 = ?";
      prep = getPreparedStatement(query);
      prep.setString(1, "execQueryTest");
      ResultSet rs = prep.executeQuery();
      
      // verify that we got back a proxy for the result set
      assertTrue("Resultset was not a proxy", AbstractWrapper.isProxy(rs.getClass()));
      
      // verify the log message for the select
      assertTrue(getLastLogEntry().contains(query));
      
      rs.close();
      prep.close();
    } catch (Exception e) {
      fail(e.getMessage() + " due to error:\n" + getStackTrace(e));
    }
  }
  
  @Test
  public void testSameColumnNameInMultipleTables() throws SQLException {
    try {
        // insert test data
        {
          final String update = "insert into prepstmt_test values (?, ?)";
          final PreparedStatement prep = getPreparedStatement(update);
          prep.setString(1, "prepstmt_test_col1");
          prep.setInt(2, 1);
          prep.executeUpdate();
          prep.close();
        }
        {
          final String update = "insert into prepstmt_test2 values (?, ?)";
          final PreparedStatement prep = getPreparedStatement(update);
          prep.setString(1, "prepstmt_test_col2");
          prep.setInt(2, 1);
          prep.executeUpdate();
          prep.close();
        }
  
        super.clearLogEntries();
  
        // let's check that returned data are reported correctly
        // => don't filter 'result' and 'resultset'
        P6LogOptions.getActiveInstance().setExcludecategories("");
  
        final String query = "select prepstmt_test.col1, prepstmt_test2.col1, prepstmt_test.col2, prepstmt_test2.col2 from prepstmt_test, prepstmt_test2 where prepstmt_test.col2 = prepstmt_test2.col2 and prepstmt_test.col1 = ? and prepstmt_test2.col1 = ?";
        final PreparedStatement prep = getPreparedStatement(query);
        prep.setString(1, "prepstmt_test_col1");
        prep.setString(2, "prepstmt_test_col2");
        final ResultSet rs = prep.executeQuery();
  
        // check "statement" logged properly
        assertNotNull(super.getLastLogEntry());
        assertTrue("prepared statement not logged properly",
            super.getLastLogEntry().contains("statement"));
        assertTrue("prepared statement not logged properly", super.getLastLogEntry().contains(query));
        assertTrue(
            "prepared statement not logged properly",
            super.getLastLogEntry().contains(
                query.replaceFirst("\\?", "\'prepstmt_test_col1\'").replaceFirst("\\?",
                    "\'prepstmt_test_col2\'")));
  
        // check returned (real) data not messed up
        while (rs.next()) {
          assertEquals("returned values messed up", "prepstmt_test_col1", rs.getString(1));
          assertEquals("returned values messed up", "prepstmt_test_col2", rs.getString(2));
          assertEquals("returned values messed up", 1, rs.getInt(3));
          assertEquals("returned values messed up", 1, rs.getInt(4));
        }
        rs.close();
        prep.close();
  
        // check "resultset" logged properly
        assertNotNull(super.getLastLogEntry());
        assertTrue("resultset not logged", super.getLastLogEntry().contains("resultset"));
        assertTrue(
            "logged resultset holds incorrect values",
            super.getLastLogEntry().endsWith(
                "1 = 'prepstmt_test_col1', 2 = 'prepstmt_test_col2', 3 = 1, 4 = 1"));
  
        P6LogOptions.getActiveInstance().setExcludecategories("result,resultset");
      
    } catch (Exception e) {
      fail(e.getMessage() + " due to error:\n" + getStackTrace(e));
    }
  }

  @Test
  public void testExecuteUpdate() {
    try {
      // test a basic insert
      String update = "insert into prepstmt_test values (?, ?)";
      PreparedStatement prep = getPreparedStatement(update);
      prep.setString(1, "miller");
      prep.setInt(2, 1);
      prep.executeUpdate();
      assertTrue(super.getLastLogEntry().contains(update));
      assertTrue(super.getLastLogEntry().contains("miller"));
      assertTrue(super.getLastLogEntry().contains("1"));


      // test dynamic allocation of P6_MAX_FIELDS
      int MaxFields = 10;
      StringBuffer bigSelect = new StringBuffer(MaxFields);
      bigSelect.append("select count(*) from prepstmt_test where");
      for (int i = 0; i < MaxFields; i++) {
        if (i > 0) {
          bigSelect.append(" or ");
        }
        bigSelect.append(" col2=?");
      }
      prep.close();
      
      prep = getPreparedStatement(bigSelect.toString());
      for (int i = 1; i <= MaxFields; i++) {
        prep.setInt(i, i);
      }
      prep.close();
      
      // test batch inserts
      update = "insert into prepstmt_test values (?,?)";
      prep = getPreparedStatement(update);
      prep.setString(1, "danny");
      prep.setInt(2, 2);
      prep.addBatch();
      prep.setString(1, "denver");
      prep.setInt(2, 3);
      prep.addBatch();
      prep.setString(1, "aspen");
      prep.setInt(2, 4);
      prep.addBatch();
      prep.executeBatch();
      assertTrue(super.getLastLogEntry().contains(update));
      assertTrue(super.getLastLogEntry().contains("aspen"));
      assertTrue(super.getLastLogEntry().contains("4"));
      assertTrue(super.getLastLogEntry().contains("batch"));
      prep.close();
      
      String query = "select count(*) from prepstmt_test";
      prep = getPreparedStatement(query);
      ResultSet rs = prep.executeQuery();
      rs.next();
      assertEquals(4, rs.getInt(1));
      
      rs.close();
      prep.close();
    } catch (Exception e) {
      fail(e.getMessage() + " due to error:\n" + getStackTrace(e));
    }
  }
  
  @Test
  public void testCallingSetMethodsOnStatementInterface() throws SQLException {
    String sql = "select * from prepstmt_test where col1 = ?";
    PreparedStatement prep = getPreparedStatement(sql);

    prep.setMaxRows(1);
    assertEquals(1, prep.getMaxRows());
    
    prep.setQueryTimeout(12);
    // The SQLLite driver returns the value in ms
    assertEquals(("SQLite".equals(db) ? 12000 : 12), prep.getQueryTimeout());
    
    prep.close();
  }

    @Test
    public void testAddToDate() throws SQLException {
     if (db.equals("PostgreSQL")) {
         String select = "select * from prepstmt_test3 where (date(col1) + ?) < now()  ";
         PreparedStatement prep = getPreparedStatement(select);
         prep.setInt(1, 10);
         prep.executeQuery();
         prep.close();
     }
    }

  @After
  public void tearDownPreparedStatement() {
    try {
      Statement statement = connection.createStatement();
      dropPrepared(statement);
      statement.close();  
    } catch (Exception e) {
      fail(e.getMessage() + " due to error:\n" + getStackTrace(e));
    }
  }

  protected void dropPrepared(Statement statement) {
    dropPreparedStatement("drop table prepstmt_test", statement);
    dropPreparedStatement("drop table prepstmt_test2", statement);
    dropPreparedStatement("drop table prepstmt_test3", statement);
  }

  @Test
  public void binaryExcludedTrue() throws SQLException {
    // given
    P6LogOptions.getActiveInstance().setExcludebinary(true);

    // when
    String update = "insert into img values (?, ?, ?)";
    PreparedStatement prep = getPreparedStatement(update);
    prep.setInt(1, 1);
    prep.setBytes(2, "foo".getBytes(StandardCharsets.UTF_8));
    if( // java.sql.SQLFeatureNotSupportedException: Method org.postgresql.jdbc4.Jdbc4Connection.createBlob() is not yet implemented.
        "PostgreSQL".equals(db) //
        // org.firebirdsql.jdbc.FBDriverNotCapableException: Not yet implemented.
        // at org.firebirdsql.jdbc.FBBlob.setBytes(FBBlob.java:472)
        || "Firebird".equals(db) //
        // java.lang.AbstractMethodError
        // at com.p6spy.engine.wrapper.ConnectionWrapper.createBlob(ConnectionWrapper.java:315)
        || "SQLite".equals(db)) {
      prep.setBytes(3, "foo".getBytes(StandardCharsets.UTF_8));
    } else {
      Blob data = connection.createBlob();
      data.setBytes(1, "foo".getBytes(StandardCharsets.UTF_8));
      prep.setBlob(3, data);
    }
    prep.execute();
    
    // then
    assertTrue(super.getLastLogEntry().contains("insert into img values (1, '[binary]', "));
  }
  
  @Test
  public void binaryExcludedFalse() throws SQLException {
    // given
    P6LogOptions.getActiveInstance().setExcludebinary(false);

    // when
    String update = "insert into img values (?, ?, ?)";
    PreparedStatement prep = getPreparedStatement(update);
    prep.setInt(1, 1);
    prep.setBytes(2, "foo".getBytes(StandardCharsets.UTF_8));
    if( // java.sql.SQLFeatureNotSupportedException: Method org.postgresql.jdbc4.Jdbc4Connection.createBlob() is not yet implemented.
        "PostgreSQL".equals(db) //
        // org.firebirdsql.jdbc.FBDriverNotCapableException: Not yet implemented.
        // at org.firebirdsql.jdbc.FBBlob.setBytes(FBBlob.java:472)
        || "Firebird".equals(db) //
        // java.lang.AbstractMethodError
        // at com.p6spy.engine.wrapper.ConnectionWrapper.createBlob(ConnectionWrapper.java:315)
        || "SQLite".equals(db)) {
      prep.setBytes(3, "foo".getBytes(StandardCharsets.UTF_8));
    } else {
      Blob data = connection.createBlob();
      data.setBytes(1, "foo".getBytes(StandardCharsets.UTF_8));
      prep.setBlob(3, data);
    }
    prep.execute();
    
    // then
    assertTrue(super.getLastLogEntry().contains("insert into img values (1, '666F6F',"));
  }
  
  protected void dropPreparedStatement(String sql, Statement statement) {
    try {
      statement.execute(sql);
    } catch (Exception e) {
      // we don't really care about cleanup failing
    }
  }

  protected PreparedStatement getPreparedStatement(String query) throws SQLException {
    return connection.prepareStatement(query);
  }

}
