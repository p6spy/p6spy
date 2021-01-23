/**
 * P6Spy
 *
 * Copyright (C) 2002 P6Spy
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
package com.p6spy.engine.test;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.spy.P6SpyDriver;

import com.p6spy.engine.wrapper.ResultSetWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ResultSetInformationTest extends P6TestFramework {

  public ResultSetInformationTest(String db) throws SQLException, IOException {
    super(db);
  }

  @Test
  public void testResultSetInformation() throws Exception {
    final ConnectionInformation connectionInformation = connection.getConnectionInformation();
    assertSame(connection.getDelegate(), connectionInformation.getConnection());
    assertNotNull(connectionInformation.getDriver());
    assertNotEquals(P6SpyDriver.class, connectionInformation.getDriver().getClass());
    assertTrue(connectionInformation.getTimeToGetConnectionNs() > 0);
  }

  @Test
  public void testResultSetResults() throws SQLException {
    ResultSetWrapper resultSet = executeQuery("select * from customers where id in (1,2) order by id");
    ResultSetInformation resultSetInformation = resultSet.getResultSetInformation();
    assertSame(resultSet.getDelegate(), resultSetInformation.getResultSet());

    resultSet.next();
    resultSet.getInt(1);
    resultSet.getString(2);
    resultSet.getString("name");

    assertEquals(3, resultSetInformation.getResultMap().size());
    assertEquals(1, resultSetInformation.getResultMap().get("1").getValue());
    assertEquals("david", resultSetInformation.getResultMap().get("2").getValue());
    assertEquals("david", resultSetInformation.getResultMap().get("name").getValue());

    resultSet.next();
    resultSet.getInt(1);
    resultSet.getString("name");

    assertEquals(2, resultSetInformation.getResultMap().size());
    assertEquals(2, resultSetInformation.getResultMap().get("1").getValue());
    assertEquals("mary", resultSetInformation.getResultMap().get("name").getValue());
  }

  private ResultSetWrapper executeQuery(String sql) throws SQLException {
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);
    assertTrue("ResultSet must've been wrapped into ResultSetWrapper", resultSet instanceof ResultSetWrapper);
    return (ResultSetWrapper) resultSet;
  }
}
