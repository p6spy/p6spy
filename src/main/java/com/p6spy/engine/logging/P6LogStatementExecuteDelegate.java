package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.Delegate;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;

class P6LogStatementExecuteDelegate implements Delegate {
  private final StatementInformation statementInformation;

  public P6LogStatementExecuteDelegate(final StatementInformation statementInformation) {
    this.statementInformation = statementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    statementInformation.setStatementQuery((String) args[0]);
    long startTime = System.currentTimeMillis();

    try {
      Object result = method.invoke(target, args);
      if( result != null && result instanceof ResultSet) {
        P6LogResultSetInvocationHandler resultSetInvocationHandler = new P6LogResultSetInvocationHandler((ResultSet)result, statementInformation);
        result = ProxyFactory.createProxy((ResultSet)result, ResultSet.class, resultSetInvocationHandler);
      }
      return result;
    }
    finally {
      P6LogQuery.logElapsed(statementInformation.getConnectionId(), startTime, "statement", "",
          statementInformation.getStatementQuery());
    }
  }
}
