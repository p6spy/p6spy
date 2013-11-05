package com.p6spy.engine.logging;

import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.proxy.ProxyFactory;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6LogFactory implements P6Factory {

  @Override
  public Connection getConnection(Connection conn) throws SQLException {
    P6LogConnectionInvocationHandler invocationHandler = new P6LogConnectionInvocationHandler(conn);
    return ProxyFactory.createProxy(conn, Connection.class, invocationHandler);
  }

  @Override
  public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
    return new P6LogOptions(optionsRepository);
  }

}
