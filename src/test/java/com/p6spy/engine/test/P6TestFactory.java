package com.p6spy.engine.test;

import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6TestFactory implements P6Factory {

  @Override
  public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
    return new P6TestOptions(optionsRepository);
  }

  @Override
  public Connection getConnection(Connection conn) throws SQLException {
    // no wrapping required here
    return conn;
  }

}
