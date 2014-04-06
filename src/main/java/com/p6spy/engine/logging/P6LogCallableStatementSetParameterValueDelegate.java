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

import com.p6spy.engine.common.CallableStatementInformation;

import java.lang.reflect.Method;
import java.sql.Statement;

class P6LogCallableStatementSetParameterValueDelegate extends P6LogPreparedStatementSetParameterValueDelegate {

  public P6LogCallableStatementSetParameterValueDelegate(CallableStatementInformation callableStatementInformation) {
    super(callableStatementInformation);
  }

  @Override
  public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
    if( args[0] instanceof Integer ) {
      return super.invoke(proxy, underlying, method, args);
    } else {
      // ignore calls to any methods defined on the Statement interface!
      if( !Statement.class.equals(method.getDeclaringClass()) ) {
        String name = (String) args[0];
        Object value = null;
        if (!method.getName().equals("setNull") && args.length > 1) {
          value = args[1];
        }
        ((CallableStatementInformation)preparedStatementInformation).setParameterValue(name, value);
      }
    }
    return method.invoke(underlying, args);
  }


}
