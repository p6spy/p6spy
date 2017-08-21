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
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.test.P6TestFramework;

@RunWith(Parameterized.class)
public class P6TestCallableStatement extends P6TestFramework {
  private static final int TEST_IMG_ID = 2000;
  private static final Logger log = Logger.getLogger(P6TestCallableStatement.class);
  private boolean originalExcludeBinaryFlag;
  
  @Before
  public void before() {
    this.originalExcludeBinaryFlag = P6LogOptions.getActiveInstance().getExcludebinary();
  }
  
  @After
  public void after() {
    P6LogOptions.getActiveInstance().setExcludebinary(this.originalExcludeBinaryFlag);
  }
  
  @Parameterized.Parameters(name = "{index}: {0}")
  public static Collection<Object[]> dbs() {
    Collection<Object[]> result;
    String dbList = (System.getProperty("DB") == null ? "HSQLDB" : System.getProperty("DB"));

    Object[] dbs = dbList.split(",");
    List<Object[]> dbsToTest = new ArrayList<Object[]>();
    for (int i = 0; i < dbs.length; i++) {
      //  Check against list of databases with stored procs
      // As procs become available for other databases, enable them here.
      if( Arrays.asList("Oracle","MySQL","HSQLDB").contains(dbs[i])) {
        dbsToTest.add(new Object[]{dbs[i]});
      } else {
        log.info("Skipping "+dbs[i]+" because stored procedures have not been created for testing");
      }
    }
    result = dbsToTest;

    return result;
  }


  public P6TestCallableStatement(String db) throws SQLException, IOException {
    super(db);
  }

  @Test
  public void testStoredProcedureNoResultSet() throws SQLException {
    this.clearLogEntries();

    // execute the statement
    String query = "{call test_proc(?,?,?)}";
    CallableStatement call = connection.prepareCall(query);
    call.registerOutParameter(3, Types.INTEGER);
    call.setInt(1, 1);
    call.setString(2, "hi");
    call.execute();
    int retVal = call.getInt(3);
    assertEquals(2, retVal);
    call.close();

    // the last log message should have the original query
    assertTrue(getLastLogEntry().contains(query));

    // verify that the bind parameters are resolved in the log message
    assertTrue(getLastLogEntry().contains("1,'hi'"));
  }

  @Test
  public void testStoredProcedureResultSet() throws SQLException {
    if( "Oracle".equals(db)) {
      // Oracle does not support returning a resultset from a store proc via CallableStatement.getResultSet()
      return;
    }
    P6LogOptions.getActiveInstance().setExcludecategories("debug,info,result");
    this.clearLogEntries();

    // execute the statement
    String query = "{call test_proc_rs(?)}";
    CallableStatement call = connection.prepareCall(query);
    call.setString(1, "a");
    call.execute();
    ResultSet rs = call.getResultSet();
    if( rs == null ) {
      // HSQLDB requires you to call ResultSet.getMoreResults() before accessing the resultset.
      call.getMoreResults();
      rs = call.getResultSet();
    }
    while(rs.next()) {
      rs.getString("name");
      rs.getInt("id");
    }
    rs.close();
    call.close();

    // verify that the result set was logged
    assertTrue(getLastLogEntry().contains("resultset"));
  }

  @Test
  public void testNamedParameters() throws SQLException {
    this.clearLogEntries();
    
    String param1Name = "param1";
    String param2Name = "param2";
    String resultParamName = "result_param";
    
    if( "HSQLDB".equals(db) ) {
      // HSQLDB uses @p1, @p2, etc...  as the "names" of the parameters
      param1Name = "@p1";
      param2Name = "@p2";
      resultParamName = "@p3";
    }

    // execute the statement
    String query = "{call test_proc(?,?,?)}";
    CallableStatement call = connection.prepareCall(query);
    call.setInt(param1Name, 1);
    call.setString(param2Name, "hi");
    call.registerOutParameter(resultParamName, Types.INTEGER);
    call.execute();
    int retVal = call.getInt(resultParamName);
    assertEquals(2, retVal);
    call.close();

    // the last log message should have the original query
    assertTrue(getLastLogEntry().contains(query));

    assertTrue(getLastLogEntry().contains("{call test_proc(?,?,?)}"));
    assertTrue(getLastLogEntry().contains(param1Name+":"+"1"));
    assertTrue(getLastLogEntry().contains(param2Name+":"+"'hi'"));
  }

  @Test
  public void testStoredProcedureWithNullInputParameter() throws SQLException {
    this.clearLogEntries();

    // execute the statement
    String query = "{call test_proc(?,?,?)}";
    CallableStatement stmt = connection.prepareCall(query);
    stmt.registerOutParameter(3, Types.INTEGER);
    stmt.setInt(1, 1);
    stmt.setNull(2, Types.VARCHAR);
    stmt.execute();
    int retVal = stmt.getInt(3);
    assertEquals(2, retVal);
    stmt.close();

    // verify that the third parameter is NULL
    assertTrue(getLastLogEntry().contains("1,NULL"));
  }

  @Test
  public void binaryExcludedTrue() throws SQLException {
    // given
    P6LogOptions.getActiveInstance().setExcludebinary(true);

    // when
    String paramValName = "param_val";
    String paramIntName = "param_id";
    String resultParamName = "result_param";
    
    if( "HSQLDB".equals(db) ) {
      // HSQLDB uses @p1, @p2, etc...  as the "names" of the parameters
      paramValName = "@p1";
      paramIntName = "@p2";
      resultParamName = "@p3";
    }

    // execute the statement
    String query = "{call test_proc_binary(?,?,?)}";
    CallableStatement call = connection.prepareCall(query);
    call.setBytes(paramValName, "foo".getBytes(StandardCharsets.UTF_8));
    call.setInt(paramIntName, TEST_IMG_ID);
    call.registerOutParameter(resultParamName, Types.INTEGER);
    call.execute();
    // out vals not logged anyway https://github.com/p6spy/p6spy/issues/133
//      byte[] retVal = call.getBytes(resultParamName);
//      assertEquals("foo", retVal);
    call.close();

    // then
    assertTrue( getLastLogEntry().contains("{call test_proc_binary(?,?,?)} " + paramIntName + ":2000, " + paramValName + ":'[binary]'") // 
        || getLastLogEntry().contains("{call test_proc_binary(?,?,?)} " + paramValName + ":'[binary]', " + paramIntName + ":2000"));
  }
  
  @Test
  public void binaryExcludedFalse() throws SQLException {
    // given
    P6LogOptions.getActiveInstance().setExcludebinary(false);

    // when
    String paramValName = "param_val";
    String paramIntName = "param_id";
    String resultParamName = "result_param";
    
    if( "HSQLDB".equals(db) ) {
      // HSQLDB uses @p1, @p2, etc...  as the "names" of the parameters
      paramValName = "@p1";
      paramIntName = "@p2";
      resultParamName = "@p3";
    }

    // execute the statement
    String query = "{call test_proc_binary(?,?,?)}";
    CallableStatement call = connection.prepareCall(query);
    call.setBytes(paramValName, "foo".getBytes(StandardCharsets.UTF_8));
    call.setInt(paramIntName, TEST_IMG_ID);
    call.registerOutParameter(resultParamName, Types.INTEGER);
    call.execute();
    // out vals not logged anyway https://github.com/p6spy/p6spy/issues/133
//      byte[] retVal = call.getBytes(resultParamName);
//      assertEquals("foo", retVal);
    call.close();

    // then
    assertTrue( getLastLogEntry().contains("{call test_proc_binary(?,?,?)} " + paramIntName + ":2000, " + paramValName + ":'666F6F'") // 
        || getLastLogEntry().contains("{call test_proc_binary(?,?,?)} " + paramValName + ":'666F6F', " + paramIntName + ":2000"));
  }

  protected PreparedStatement getPreparedStatement(String query) throws SQLException {
    return connection.prepareStatement(query);
  }
}


