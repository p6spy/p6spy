package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

public class P6LogStatementExecuteDelegate implements Delegate {
  private final P6LogStatementInvocationHandler statementInvocationHandler;

  public P6LogStatementExecuteDelegate(P6LogStatementInvocationHandler statementInvocationHandler) {
    this.statementInvocationHandler = statementInvocationHandler;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    statementInvocationHandler.setStatementQuery((String) args[0]);
    long startTime = System.currentTimeMillis();

    try {
      return method.invoke(target, args);
    }
    finally {
      P6LogQuery.logElapsed(statementInvocationHandler.getConnectionId(), startTime, "statement", "",
          statementInvocationHandler.getStatementQuery());
    }
  }
}
