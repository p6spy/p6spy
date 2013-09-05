package com.p6spy.engine.logging;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;

/**
 */
public class P6LogPrepareStatementDelegate extends P6LogCreateStatementDelegate {

  public P6LogPrepareStatementDelegate(P6LogConnectionInvocationHandler invocationHandler) {
    super(invocationHandler);
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    PreparedStatement statement = (PreparedStatement) method.invoke(target, args);
    String query = (String) args[0];
    P6LogPreparedStatementInvocationHandler statementProxy = new P6LogPreparedStatementInvocationHandler(statement,
        getInvocationHandler(), query, statement.getParameterMetaData());
    return Proxy.newProxyInstance(
        statement.getClass().getClassLoader(),
        new Class[]{PreparedStatement.class},
        statementProxy);


  }

}
