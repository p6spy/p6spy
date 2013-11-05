package com.p6spy.engine.spy;

import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;
import java.sql.Connection;
import java.sql.SQLException;


public class P6PooledConnection implements PooledConnection {

  protected PooledConnection passthru;

  public P6PooledConnection(PooledConnection connection) {
    passthru = connection;
  }


  @Override
  public Connection getConnection() throws SQLException {
    return P6Core.wrapConnection(passthru.getConnection());
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
