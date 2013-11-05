package com.p6spy.engine.leak;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

/**
 */
class P6LeakConnectionCloseDelegate implements Delegate {
  private final ConnectionInformation connectionInformation;

  public P6LeakConnectionCloseDelegate(ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    P6Objects.close(connectionInformation);
    return method.invoke(target, args);
  }
}
