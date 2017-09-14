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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.SimpleJdbcEventListener;
import com.p6spy.engine.test.P6TestFramework;
import com.p6spy.engine.test.P6TestLoadableOptions;
import com.p6spy.engine.test.P6TestOptions;
import com.p6spy.engine.wrapper.ConnectionWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class P6TestPool extends P6TestFramework {

  public P6TestPool(String db) throws SQLException, IOException {
    super(db);

    final P6TestLoadableOptions testOptions = P6TestOptions.getActiveInstance();

    org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
    ds.setDriverClassName(P6SpyDriver.class.getName());
    ds.setUsername(testOptions.getUser());
    ds.setPassword(testOptions.getPassword());
    ds.setUrl(testOptions.getUrl());
    ds.setTestOnBorrow(true);
    ds.setTestOnConnect(true);
    ds.setValidationQuery(testOptions.getValidationQuery());

    connection = ds.getConnection().unwrap(ConnectionWrapper.class);
  }

  @Test
  public void testExecute() throws SQLException {
    String query = "select * from customers";

    try (Connection connectionWrapper = //
        ConnectionWrapper.wrap( //
            this.connection, new SimpleJdbcEventListener() {
              @Override
              public void onBeforeAnyExecute(StatementInformation statementInformation) {
                assertThat("sql of statementInformation", statementInformation.getSql(), is(notNullValue()));
              }
            }, //
            ConnectionInformation.fromTestConnection(this.connection) //
        ) //
    ) {
      P6TestUtil.execute(connectionWrapper, query);
    }
  }
}
