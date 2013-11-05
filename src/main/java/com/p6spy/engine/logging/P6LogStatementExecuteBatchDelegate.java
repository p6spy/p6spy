package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

class P6LogStatementExecuteBatchDelegate implements Delegate {

  private final StatementInformation statementInformation;

  public P6LogStatementExecuteBatchDelegate(final StatementInformation statementInformation) {
    this.statementInformation = statementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();

    try {
      return method.invoke(target, args);
    }
    finally {
      P6LogQuery.logElapsed(statementInformation.getConnectionId(), startTime, "statement", "", statementInformation.getStatementQuery());
    }
  }
}
