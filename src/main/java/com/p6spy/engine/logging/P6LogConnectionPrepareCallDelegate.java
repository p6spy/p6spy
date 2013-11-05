package com.p6spy.engine.logging;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.CallableStatement;

/**
 */
public class P6LogConnectionPrepareCallDelegate extends P6LogConnectionCreateStatementDelegate {

  public P6LogConnectionPrepareCallDelegate(final ConnectionInformation connectionInformation) {
    super(connectionInformation);
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    CallableStatement statement = (CallableStatement) method.invoke(target, args);
    String query = (String) args[0];
    P6LogCallableStatementInvocationHandler invocationHandler = new P6LogCallableStatementInvocationHandler(statement,
        getConnectionInformation(), query, statement.getParameterMetaData());
    return ProxyFactory.createProxy(statement, CallableStatement.class, invocationHandler);
  }

}
