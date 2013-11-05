package com.p6spy.engine.leak;

import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

class P6LeakStatementCloseDelegate implements Delegate {
  private final StatementInformation statementInformation;

  public P6LeakStatementCloseDelegate(final StatementInformation statementInformation) {
    this.statementInformation = statementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    P6Objects.close(statementInformation);
    return method.invoke(target, args);
  }
}
