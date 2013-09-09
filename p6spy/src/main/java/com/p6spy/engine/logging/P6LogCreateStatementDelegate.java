package com.p6spy.engine.logging;

import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;

public class P6LogCreateStatementDelegate implements Delegate {
  private final ConnectionInformation connectionInformation;

  public P6LogCreateStatementDelegate(final ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    Statement statement = (Statement) method.invoke(target, args);
    P6LogStatementInvocationHandler invocationHandler = new P6LogStatementInvocationHandler(statement, connectionInformation);
    return Proxy.newProxyInstance(
        statement.getClass().getClassLoader(),
        new Class[]{Statement.class},
        invocationHandler);
  }

  protected ConnectionInformation getConnectionInformation() {
    return connectionInformation;
  }
}
