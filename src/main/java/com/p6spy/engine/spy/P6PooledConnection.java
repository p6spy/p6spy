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

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.wrapper.ConnectionWrapper;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

public class P6PooledConnection implements PooledConnection {

  protected PooledConnection passthru;

  public P6PooledConnection(PooledConnection connection) {
    passthru = connection;
  }

  @Override
  public Connection getConnection() throws SQLException {
    JdbcEventListener jdbcEventListener = P6Core.getJdbcEventListener();
    final long start = System.nanoTime();
    try {
      final Connection conn = passthru.getConnection();
      ConnectionInformation connectionInformation = ConnectionInformation.fromPooledConnection(passthru, conn, System.nanoTime() - start);
      jdbcEventListener.onAfterGetConnection(connectionInformation, null);
      return ConnectionWrapper.wrap(conn, jdbcEventListener, connectionInformation);
    } catch (SQLException e) {
      jdbcEventListener.onAfterGetConnection(ConnectionInformation.fromPooledConnection(passthru, null, System.nanoTime() - start), e);
      throw e;
    }
  }

  @Override
  public void close() throws SQLException {
    passthru.close();
  }

  @Override
  public void addConnectionEventListener(ConnectionEventListener eventTarget) {
    passthru.addConnectionEventListener(eventTarget);
  }


  @Override
  public void removeConnectionEventListener(ConnectionEventListener eventTarget) {
    passthru.removeConnectionEventListener(eventTarget);
  }

  @Override
  public void addStatementEventListener(StatementEventListener listener) {
    passthru.addStatementEventListener(listener);
  }


  @Override
  public void removeStatementEventListener(StatementEventListener listener) {
    passthru.removeStatementEventListener(listener);
  }

}
