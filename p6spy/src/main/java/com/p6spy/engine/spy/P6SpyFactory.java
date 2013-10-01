package com.p6spy.engine.spy;

import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.common.P6LoadableOptions;

public class P6SpyFactory implements P6Factory {

  @Override
  public P6LoadableOptions getOptions() {
    return new P6SpyOptionsImpl();
  }

  @Override
  public Connection getConnection(Connection conn) throws SQLException {
    // no need to decorate connections, as other modules should
    return null;
  }

}
