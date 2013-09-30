package com.p6spy.engine.leak;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.Delegate;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.Statement;

class P6LeakConnectionCreateStatementDelegate implements Delegate {
  private final ConnectionInformation connectionInformation;

  public P6LeakConnectionCreateStatementDelegate(final ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    Statement statement = (Statement) method.invoke(target, args);
    P6LeakStatementInvocationHandler invocationHandler = new P6LeakStatementInvocationHandler(statement, connectionInformation);
    return ProxyFactory.createProxy(statement, Statement.class, invocationHandler);
  }

  ConnectionInformation getConnectionInformation() {
    return connectionInformation;
  }
}
