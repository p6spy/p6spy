package com.p6spy.engine.leak;

import com.p6spy.engine.proxy.ProxyFactory;
import com.p6spy.engine.spy.P6CoreFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class P6LeakFactory extends P6CoreFactory {

  public P6LeakFactory() {
  }

  @Override
  public Connection getConnection(Connection conn) throws SQLException {
    P6LeakConnectionInvocationHandler invocationHandler = new P6LeakConnectionInvocationHandler(conn);
    return ProxyFactory.createProxy(conn, Connection.class, invocationHandler);
  }


}
