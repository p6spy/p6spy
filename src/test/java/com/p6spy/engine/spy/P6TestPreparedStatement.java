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

import com.p6spy.engine.test.P6TestFramework;
import net.sf.cglib.proxy.Proxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class P6TestPreparedStatement extends P6TestFramework {

  public P6TestPreparedStatement(String db) throws SQLException, IOException {
    super(db);
  }

  @Test
  public void testExecuteQuery() {
    try {
      String query = "select * from customers where id = ?";
      PreparedStatement prep = getPreparedStatement(query);
      prep.setInt(1, 1);
      ResultSet rs = prep.executeQuery();

      // verify that we got back a proxy for the result set
      assertTrue("Resultset was not a proxy", Proxy.isProxyClass(rs.getClass()));

      // verify the log message for the select
      assertTrue(getLastLogEntry().contains(query));

      rs.close();
      prep.close();
    } catch (Exception e) {
      fail(e.getMessage() + " due to error: " + getStackTrace(e));
    }
  }

  @Test
  public void testExecute() {
    try {
      // test a basic insert
      String update = "insert into customers (id,name) values (?, ?)";
      PreparedStatement prep = getPreparedStatement(update);
      prep.setString(2, "yoller");
      prep.setInt(1, 100);
      prep.execute();
      prep.close();
      assertTrue(super.getLastLogEntry().contains(update));
      assertTrue(super.getLastLogEntry().contains("yoller"));
      assertTrue(super.getLastLogEntry().contains("100"));

      prep.close();
    } catch (Exception e) {
      fail(e.getMessage() + " due to error: " + getStackTrace(e));
    }
  }

  @Test
  public void testExecuteUpdate() {
    try {
      // test a basic insert
      String update = "update customers set name=? where id=?";
      PreparedStatement prep = getPreparedStatement(update);
      prep.setString(1, "yoller");
      prep.setInt(2, 1);
      int rowsUpdated = prep.executeUpdate();
      prep.close();
      assertTrue(super.getLastLogEntry().contains(update));
      assertTrue(super.getLastLogEntry().contains("yoller"));
      assertTrue(super.getLastLogEntry().contains("1"));
      assertEquals(1, rowsUpdated);

      prep.close();
    } catch (Exception e) {
      fail(e.getMessage() + " due to error: " + getStackTrace(e));
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

  protected PreparedStatement getPreparedStatement(String query) throws SQLException {
    return (connection.prepareStatement(query));
  }

}
