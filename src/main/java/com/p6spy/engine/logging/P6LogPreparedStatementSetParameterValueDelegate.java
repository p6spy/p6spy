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
package com.p6spy.engine.logging;

import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;
import java.sql.Statement;

class P6LogPreparedStatementSetParameterValueDelegate implements Delegate {
  private final PreparedStatementInformation preparedStatementInformation;

  public P6LogPreparedStatementSetParameterValueDelegate(PreparedStatementInformation preparedStatementInformation) {
    this.preparedStatementInformation = preparedStatementInformation;
  }

  @Override
  public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
    // ignore calls to any methods defined on the Statement interface!
    if( !Statement.class.equals(method.getDeclaringClass()) ) {
      
      // short term fix to prevent ClassCastException when setting named parameters
      // on a CallableStatement
      if( args[0] instanceof Integer ) {
        int position = (Integer) args[0];
        Object value = null;
        if (!method.getName().equals("setNull") && args.length > 1) {
          value = args[1];
        }
        preparedStatementInformation.setParameterValue(position, value);
      }
    }
    return method.invoke(underlying, args);
  }


}
