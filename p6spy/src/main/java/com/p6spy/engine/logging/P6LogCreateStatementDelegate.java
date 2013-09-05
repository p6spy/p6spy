package com.p6spy.engine.logging;

import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;

public class P6LogCreateStatementDelegate implements Delegate {
  private final P6LogConnectionInvocationHandler invocationHandler;

  public P6LogCreateStatementDelegate(P6LogConnectionInvocationHandler invocationHandler) {
    this.invocationHandler = invocationHandler;
  }

  protected P6LogConnectionInvocationHandler getInvocationHandler() {
    return invocationHandler;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    Statement statement = (Statement) method.invoke(target, args);
    P6LogStatementInvocationHandler statementProxy = new P6LogStatementInvocationHandler(statement, invocationHandler);
    return Proxy.newProxyInstance(
        statement.getClass().getClassLoader(),
        new Class[]{Statement.class},
        statementProxy);
  }
}
