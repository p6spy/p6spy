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

import com.p6spy.engine.common.StatementInformation;

import java.lang.reflect.Method;

class P6LogStatementAddBatchDelegate extends P6LogElapsedDelegate {

  private final StatementInformation statementInformation;

  public P6LogStatementAddBatchDelegate(final StatementInformation statementInformation) {
    super(statementInformation, Category.BATCH);
    this.statementInformation = statementInformation;
  }

  @Override
  public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
    if (args.length > 0) {
      statementInformation.setStatementQuery((String) args[0]);
    }
    return super.invoke(proxy, underlying, method, args);
  }
}
