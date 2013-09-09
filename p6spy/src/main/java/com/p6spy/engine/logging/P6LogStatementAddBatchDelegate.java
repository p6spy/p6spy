package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

public class P6LogStatementAddBatchDelegate implements Delegate {

  private final StatementInformation statementInformation;

  public P6LogStatementAddBatchDelegate(final StatementInformation statementInformation) {
    this.statementInformation = statementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    statementInformation.setStatementQuery((String) args[0]);
    long startTime = System.currentTimeMillis();

    try {
      return method.invoke(target, args);
    }
    finally {
      P6LogQuery.logElapsed(statementInformation.getConnectionId(), startTime, "batch", "", statementInformation.getStatementQuery());
    }
  }
}
