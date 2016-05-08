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

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.StatementInformation;

import java.sql.Statement;

/**
 * Invocation handler for {@link Statement}
 */
class P6LogStatementInvocationHandler extends AbstractP6LogStatementInvocationHandler<Statement, StatementInformation> {

  public P6LogStatementInvocationHandler(Statement underlying, final ConnectionInformation connectionInformation) {
    super(underlying, connectionInformation, null);
  }

  @Override
  protected StatementInformation createStatementInformation(ConnectionInformation connectionInformation) {
    return new StatementInformation(connectionInformation);
  }

}
