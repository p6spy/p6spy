package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.proxy.Delegate;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;

class P6LogPreparedStatementExecuteDelegate implements Delegate {
  private final PreparedStatementInformation preparedStatementInformation;

  public P6LogPreparedStatementExecuteDelegate(final PreparedStatementInformation preparedStatementInformation) {
    this.preparedStatementInformation = preparedStatementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();

    try {
      Object result = method.invoke(target, args);
      
      if( result != null && result instanceof ResultSet) {
        P6LogResultSetInvocationHandler resultSetInvocationHandler = new P6LogResultSetInvocationHandler((ResultSet)result, preparedStatementInformation);
        result = ProxyFactory.createProxy((ResultSet) result, ResultSet.class, resultSetInvocationHandler);
      }
      return result;
      
    } finally {
      P6LogQuery.logElapsed(preparedStatementInformation.getConnectionId(), startTime, "statement",
          preparedStatementInformation.getStatementQuery(), preparedStatementInformation.getPreparedStatementQuery());
    }
  }
}
