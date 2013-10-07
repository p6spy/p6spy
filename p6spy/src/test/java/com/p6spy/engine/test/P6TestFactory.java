package com.p6spy.engine.test;

import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.common.P6LoadableOptions;
import com.p6spy.engine.spy.P6Factory;

public class P6TestFactory implements P6Factory {

  @Override
  public P6LoadableOptions getOptions() {
    return new P6TestOptions();
  }

  @Override
  public Connection getConnection(Connection conn) throws SQLException {
    // no wrapping required here
    return conn;
  }

}
