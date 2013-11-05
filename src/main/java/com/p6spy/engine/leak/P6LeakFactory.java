package com.p6spy.engine.leak;

import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.proxy.ProxyFactory;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6LeakFactory implements P6Factory {

  @Override
  public Connection getConnection(Connection conn) throws SQLException {
    P6LeakConnectionInvocationHandler invocationHandler = new P6LeakConnectionInvocationHandler(conn);
    return ProxyFactory.createProxy(conn, Connection.class, invocationHandler);
  }

  @Override
  public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
    return new P6LeakOptions(optionsRepository);
  }

}
