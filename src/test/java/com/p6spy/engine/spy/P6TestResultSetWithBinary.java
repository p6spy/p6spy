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

import static org.junit.Assert.assertTrue;

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

@RunWith(Parameterized.class)
public class P6TestResultSetWithBinary extends P6TestFramework {

  ResultSet resultSet = null;

  private boolean originalExcludeBinaryFlag;
  
  public P6TestResultSetWithBinary(String db) throws SQLException, IOException {
    super(db);
  }

  @Before
  public void before() {
    this.originalExcludeBinaryFlag = P6LogOptions.getActiveInstance().getExcludebinary();
  }
  
  @After
  public void after() {
    P6LogOptions.getActiveInstance().setExcludebinary(this.originalExcludeBinaryFlag);
  }
  
  @Before
  public void setup() throws SQLException {
    P6LogOptions.getActiveInstance().setExcludebinary(true);
    
    String update = "insert into img values (?, ?, ?)";
    PreparedStatement prep = getPreparedStatement(update);
    prep.setInt(1, 1000);
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

    resultSet = executeQuery("select val from img where id=1000");

    P6LogOptions.getActiveInstance().setExcludecategories("info,debug,result");
    clearLogEntries();
    clearLastLogStackTrace();
  }
  
  private ResultSet executeQuery(String sql) throws SQLException {
    Statement statement = connection.createStatement();
    return statement.executeQuery(sql);
  }

  
  @Test
  public void binaryExcludedTrue() throws SQLException {
    boolean original = P6LogOptions.getActiveInstance().getExcludebinary();

    try {
      // given
      P6LogOptions.getActiveInstance().setExcludebinary(true);
      resultSet.next();
      
      // when
      resultSet.getBytes("val");
      resultSet.next();

      // then
      assertTrue(super.getLastLogEntry().contains("val = '[binary]'"));
    } finally {
      P6LogOptions.getActiveInstance().setExcludebinary(original);
    }
  }
  
  @Test
  public void binaryExcludedFalse() throws SQLException {
    boolean original = P6LogOptions.getActiveInstance().getExcludebinary();

    try {
      // given
      P6LogOptions.getActiveInstance().setExcludebinary(false);
      resultSet.next();
      
      // when
      resultSet.getBytes("val");
      resultSet.next();

      // then
      assertTrue(super.getLastLogEntry().contains("val = '666F6F'"));
    } finally {
      P6LogOptions.getActiveInstance().setExcludebinary(original);
    }
  }
  
  protected PreparedStatement getPreparedStatement(String query) throws SQLException {
    return connection.prepareStatement(query);
  }
}
