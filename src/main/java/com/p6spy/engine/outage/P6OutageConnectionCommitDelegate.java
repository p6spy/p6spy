package com.p6spy.engine.outage;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

/**
 */
class P6OutageConnectionCommitDelegate implements Delegate {
  private final ConnectionInformation connectionInformation;

  public P6OutageConnectionCommitDelegate(ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();
    
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, startTime, "commit", "", "");
    }

    try {
      return method.invoke(target, args);
    } finally {
      if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
          P6OutageDetector.getInstance().unregisterInvocation(this);
      }
    }
  }
}
