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

package com.p6spy.engine.test;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.spy.P6SpyDriver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.sql.SQLException;

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
}
