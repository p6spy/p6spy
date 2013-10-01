/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
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
