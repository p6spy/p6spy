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

package com.p6spy.engine.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.sql.Connection;

import javax.sql.DataSource;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;
import org.junit.Test;

public class ConnectionWrapperTest {

  private boolean onConnectionWrappedCalled;

  @Test
  public void testOnConnectionWrapped() throws Exception {
    final Connection connection = mock(Connection.class);
    try (Connection connectionWrapper = //
        ConnectionWrapper.wrap( //
            connection, //
            new JdbcEventListener() {
              @Override
              public void onConnectionWrapped(ConnectionInformation connectionInformation) {
                onConnectionWrappedCalled = true;
                assertEquals(42, connectionInformation.getTimeToGetConnectionNs());
              }
            }, //
            ConnectionInformation.fromDataSource( //
                mock(DataSource.class), // 
                connection, //
                42))
        ) {
      assertTrue(onConnectionWrappedCalled);
    }
  }
}
