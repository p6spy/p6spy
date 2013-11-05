package com.p6spy.engine.outage;

import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

class P6OutagePreparedStatementAddBatchDelegate implements Delegate {
  private final PreparedStatementInformation preparedStatementInformation;

  public P6OutagePreparedStatementAddBatchDelegate(final PreparedStatementInformation preparedStatementInformation) {
    this.preparedStatementInformation = preparedStatementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();

    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
        P6OutageDetector.getInstance().registerInvocation(this, startTime, "batch",
            preparedStatementInformation.getStatementQuery(), preparedStatementInformation.getPreparedStatementQuery());
    }

    try {
      return method.invoke(target, args);
    }
    finally {
      if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
          P6OutageDetector.getInstance().unregisterInvocation(this);
      }
    }
  }
}
