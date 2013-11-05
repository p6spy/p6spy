package com.p6spy.engine.spy;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import java.sql.SQLException;

public class P6ConnectionPoolDataSource extends P6DataSource implements ConnectionPoolDataSource {

  public P6ConnectionPoolDataSource() {
    super();
  }

  public P6ConnectionPoolDataSource(DataSource ds) {
    super(ds);
  }

  @Override
  public PooledConnection getPooledConnection() throws SQLException {
    if (rds == null) {
      bindDataSource();
    }

    PooledConnection pc = ((ConnectionPoolDataSource) rds).getPooledConnection();
    P6PooledConnection pooledConnection = new P6PooledConnection(pc);
    return pooledConnection;
  }

  @Override
  public PooledConnection getPooledConnection(String user, String password) throws SQLException {
    if (rds == null) {
      bindDataSource();
    }

    PooledConnection pc = ((ConnectionPoolDataSource) rds).getPooledConnection(user, password);
    P6PooledConnection pooledConnection = new P6PooledConnection(pc);
    return pooledConnection;
  }

}
