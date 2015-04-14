/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2014 P6Spy
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
package com.p6spy.engine.spy.option;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.spy.P6TestUtil;
import com.p6spy.engine.spy.appender.P6TestLogger;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;
import com.p6spy.engine.test.P6TestOptions;

public class P6TestConnectionProperties extends BaseTestCase {

  @BeforeClass
  public static void setUpAll() throws SQLException, IOException {
    // make sure to reinit properly
    new P6TestFramework("connection_properties") {
    };
  }

  private static final String SQL = "SELECT 1";

  @Test
  public void testInstaceId() throws Exception {

    String url = P6TestOptions.getActiveInstance().getUrl();
    String user = P6TestOptions.getActiveInstance().getUser();
    String password = P6TestOptions.getActiveInstance().getPassword();
    Connection connection = openConnection(url, user, password);
    
    String url2 = P6TestOptions.getActiveInstance().getUrl2();
    String user2 = P6TestOptions.getActiveInstance().getUser2();
    String password2 = P6TestOptions.getActiveInstance().getPassword2();
    Connection connection2 = openConnection(url2, user2, password2);
    
    String url3 = P6TestOptions.getActiveInstance().getUrl3();
    String user3 = P6TestOptions.getActiveInstance().getUser3();
    String password3 = P6TestOptions.getActiveInstance().getPassword3();
    Connection connection3 = openConnection(url3, user3, password3);
    
    
    P6TestUtil.execute(connection, SQL);
    Assert.assertEquals("db0", ((P6TestLogger) P6LogQuery.getLogger()).getLastInstanceId());
    
    P6TestUtil.execute(connection2, SQL);
    Assert.assertEquals("db1", ((P6TestLogger) P6LogQuery.getLogger()).getLastInstanceId());
    
    P6TestUtil.execute(connection3, SQL);
    Assert.assertEquals("db2", ((P6TestLogger) P6LogQuery.getLogger()).getLastInstanceId());
    
    closeConnection(connection);
    closeConnection(connection2);
    closeConnection(connection3);
  }

  //
  // helpers
  //
  
  private void closeConnection(Connection connection) throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  private Connection openConnection(String url, String user, String password) throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }
}