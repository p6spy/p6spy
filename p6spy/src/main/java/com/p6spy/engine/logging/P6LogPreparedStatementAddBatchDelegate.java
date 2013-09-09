package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

public class P6LogPreparedStatementAddBatchDelegate implements Delegate {
  private final PreparedStatementInformation preparedStatementInformation;

  public P6LogPreparedStatementAddBatchDelegate(final PreparedStatementInformation preparedStatementInformation) {
    this.preparedStatementInformation = preparedStatementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();

    try {
      return method.invoke(target, args);
    }
    finally {
      P6LogQuery.logElapsed(preparedStatementInformation.getConnectionId(), startTime, "batch",
          preparedStatementInformation.getStatementQuery(), preparedStatementInformation.getPreparedStatementQuery());
    }
  }
}
