package com.p6spy.engine.outage;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.CallableStatement;

/**
 */
public class P6OutageConnectionPrepareCallDelegate extends P6OutageConnectionCreateStatementDelegate {

  public P6OutageConnectionPrepareCallDelegate(final ConnectionInformation connectionInformation) {
    super(connectionInformation);
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    CallableStatement statement = (CallableStatement) method.invoke(target, args);
    String query = (String) args[0];
    P6OutageCallableStatementInvocationHandler invocationHandler = new P6OutageCallableStatementInvocationHandler(statement,
        getConnectionInformation(), query, statement.getParameterMetaData());
    return ProxyFactory.createProxy(statement, CallableStatement.class, invocationHandler);
  }

}
