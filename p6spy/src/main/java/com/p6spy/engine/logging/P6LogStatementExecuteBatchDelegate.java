package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

public class P6LogStatementExecuteBatchDelegate implements Delegate {
  private final P6LogStatementInvocationHandler statementProxy;

  public P6LogStatementExecuteBatchDelegate(P6LogStatementInvocationHandler statementProxy) {
    this.statementProxy = statementProxy;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();

    try {
      return method.invoke(target, args);
    }
    finally {
      P6LogQuery.logElapsed(statementProxy.getConnectionId(), startTime, "statement", "", statementProxy.getStatementQuery());
    }
  }
}
