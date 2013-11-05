package com.p6spy.engine.spy;

import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6SpyFactory implements P6Factory {

  @Override
  public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
    return new P6SpyOptions(optionsRepository);
  }

  @Override
  public Connection getConnection(Connection conn) throws SQLException {
    // no wrapping required here
    return conn;
  }

}
