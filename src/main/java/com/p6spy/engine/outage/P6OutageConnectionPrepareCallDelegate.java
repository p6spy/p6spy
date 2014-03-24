/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
  public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
    CallableStatement statement = (CallableStatement) method.invoke(underlying, args);
    String query = (String) args[0];
    P6OutageCallableStatementInvocationHandler invocationHandler = new P6OutageCallableStatementInvocationHandler(statement,
        getConnectionInformation(), query);
    return ProxyFactory.createProxy(statement, CallableStatement.class, invocationHandler);
  }

}
