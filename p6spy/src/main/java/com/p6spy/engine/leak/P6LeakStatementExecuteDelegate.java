package com.p6spy.engine.leak;

import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.Delegate;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;

class P6LeakStatementExecuteDelegate implements Delegate {
  private final StatementInformation statementInformation;

  public P6LeakStatementExecuteDelegate(final StatementInformation statementInformation) {
    this.statementInformation = statementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    Object result = method.invoke(target, args);
    if (result != null && result instanceof ResultSet) {
      P6LeakResultSetInvocationHandler resultSetInvocationHandler = new P6LeakResultSetInvocationHandler((ResultSet) result, statementInformation);
      result = ProxyFactory.createProxy((ResultSet) result, ResultSet.class, resultSetInvocationHandler);
    }
    return result;
  }
}
