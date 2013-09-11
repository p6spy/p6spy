package com.p6spy.engine.logging;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

/**
 */
class P6LogConnectionCommitDelegate implements Delegate {
  private final ConnectionInformation connectionInformation;

  public P6LogConnectionCommitDelegate(ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();

    try {
      return method.invoke(target, args);
    } finally {
      P6LogQuery.logElapsed(connectionInformation.getConnectionId(), startTime, "commit", "", "");
    }
  }
}
