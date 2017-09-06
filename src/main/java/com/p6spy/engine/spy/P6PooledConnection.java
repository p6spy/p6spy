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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.wrapper.ConnectionWrapper;

public class P6PooledConnection implements PooledConnection {

  protected final PooledConnection passthru;
  protected final JdbcEventListenerFactory jdbcEventListenerFactory;
  
  public P6PooledConnection(PooledConnection connection, JdbcEventListenerFactory jdbcEventListenerFactory) {
    this.passthru = connection;
    this.jdbcEventListenerFactory = jdbcEventListenerFactory;
  }

  @Override
  public Connection getConnection() throws SQLException {
    final long start = System.nanoTime();
    
    final Connection conn;
    try {
      conn = passthru.getConnection();
    } catch (SQLException e) {
      this.jdbcEventListenerFactory.createJdbcEventListener().onAfterGetConnection(ConnectionInformation.fromPooledConnection(passthru, null, System.nanoTime() - start), e);
      throw e;
    }
    
    ConnectionInformation connectionInformation = ConnectionInformation.fromPooledConnection(passthru, conn, System.nanoTime() - start);
    @SuppressWarnings("resource")
    ConnectionWrapper connectionWrapper = new ConnectionWrapper(conn, this.jdbcEventListenerFactory.createJdbcEventListener(), connectionInformation);
    connectionWrapper.getJdbcEventListener().onAfterGetConnection(connectionInformation, null);
    return connectionWrapper.wrap();
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
