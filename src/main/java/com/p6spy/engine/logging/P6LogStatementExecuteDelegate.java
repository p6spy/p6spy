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
