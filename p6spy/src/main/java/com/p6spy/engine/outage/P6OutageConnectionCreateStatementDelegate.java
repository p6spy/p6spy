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
package com.p6spy.engine.outage;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.Delegate;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.Statement;

class P6OutageConnectionCreateStatementDelegate implements Delegate {
  private final ConnectionInformation connectionInformation;

  public P6OutageConnectionCreateStatementDelegate(final ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    Statement statement = (Statement) method.invoke(target, args);
    P6OutageStatementInvocationHandler invocationHandler = new P6OutageStatementInvocationHandler(statement, connectionInformation);
    return ProxyFactory.createProxy(statement, Statement.class, invocationHandler);
  }

  ConnectionInformation getConnectionInformation() {
    return connectionInformation;
  }
}
