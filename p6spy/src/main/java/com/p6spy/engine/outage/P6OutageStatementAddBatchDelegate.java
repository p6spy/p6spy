package com.p6spy.engine.outage;

import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

class P6OutageStatementAddBatchDelegate implements Delegate {

  private final StatementInformation statementInformation;

  public P6OutageStatementAddBatchDelegate(final StatementInformation statementInformation) {
    this.statementInformation = statementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();
    statementInformation.setStatementQuery((String) args[0]);

    if (P6OutageOptions.getOutageDetection()) {
        P6OutageDetector.getInstance().registerInvocation(this, startTime, "batch", "", statementInformation.getStatementQuery());
    }

    try {
      return method.invoke(target, args);
    }
    finally {
      if (P6OutageOptions.getOutageDetection()) {
          P6OutageDetector.getInstance().unregisterInvocation(this);
      }
    }
  }
}
