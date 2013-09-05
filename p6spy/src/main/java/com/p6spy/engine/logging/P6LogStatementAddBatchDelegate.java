package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

public class P6LogStatementAddBatchDelegate implements Delegate {
  private final P6LogStatementInvocationHandler statementProxy;

  public P6LogStatementAddBatchDelegate(P6LogStatementInvocationHandler statementProxy) {
    this.statementProxy = statementProxy;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    statementProxy.setStatementQuery((String) args[0]);
    long startTime = System.currentTimeMillis();

    try {
      return method.invoke(target, args);
    }
    finally {
      P6LogQuery.logElapsed(statementProxy.getConnectionId(), startTime, "batch", "", statementProxy.getStatementQuery());
    }
  }
}
