package com.p6spy.engine.logging;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;

/**
 */
public class P6LogConnectionPrepareStatementDelegate extends P6LogConnectionCreateStatementDelegate {

  public P6LogConnectionPrepareStatementDelegate(final ConnectionInformation connectionInformation) {
    super(connectionInformation);
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    PreparedStatement statement = (PreparedStatement) method.invoke(target, args);
    String query = (String) args[0];
    P6LogPreparedStatementInvocationHandler invocationHandler = new P6LogPreparedStatementInvocationHandler(statement,
        getConnectionInformation(), query, statement.getParameterMetaData());
    return ProxyFactory.createProxy(statement, PreparedStatement.class, invocationHandler);
  }

}
