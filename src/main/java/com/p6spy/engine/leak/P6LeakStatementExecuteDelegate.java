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
package com.p6spy.engine.leak;

import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.Delegate;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;

class P6LeakStatementExecuteDelegate implements Delegate {
  private final StatementInformation statementInformation;

  public P6LeakStatementExecuteDelegate(final StatementInformation statementInformation) {
    this.statementInformation = statementInformation;
  }

  @Override
  public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
    Object result = method.invoke(underlying, args);
    if (result != null && result instanceof ResultSet) {
      P6LeakResultSetInvocationHandler resultSetInvocationHandler = new P6LeakResultSetInvocationHandler((ResultSet) result, statementInformation);
      result = ProxyFactory.createProxy((ResultSet) result, ResultSet.class, resultSetInvocationHandler);
    }
    return result;
  }
}
