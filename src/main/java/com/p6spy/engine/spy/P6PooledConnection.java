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

import com.p6spy.engine.common.ConnectionInformation;

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
    long start = System.nanoTime();
    final Connection connection = passthru.getConnection();
    return P6Core.wrapConnection(connection, ConnectionInformation.fromPooledConnection(passthru, connection, System.nanoTime() - start));
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
