
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
