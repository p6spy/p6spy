package com.p6spy.engine.outage;

import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

class P6OutageStatementExecuteDelegate implements Delegate {
  private final StatementInformation statementInformation;

  public P6OutageStatementExecuteDelegate(final StatementInformation statementInformation) {
    this.statementInformation = statementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();

    if (!method.getName().equals("executeBatch")) {
      // the execute batch method takes no parameters!
      statementInformation.setStatementQuery((String) args[0]);
    }

    if (P6OutageOptions.getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, startTime, "statement", "", statementInformation.getStatementQuery());
    }

    try {
      return method.invoke(target, args);
    } finally {
      if (P6OutageOptions.getOutageDetection()) {
        P6OutageDetector.getInstance().unregisterInvocation(this);
      }
    }
  }
}
