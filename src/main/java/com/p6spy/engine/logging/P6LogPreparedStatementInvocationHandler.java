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
import com.p6spy.engine.common.PreparedStatementInformation;

import java.sql.PreparedStatement;

/**
 * Invocation handler for {@link java.sql.PreparedStatement}
 */
class P6LogPreparedStatementInvocationHandler extends AbstractP6LogPreparedStatementInvocationHandler<PreparedStatement, PreparedStatementInformation> {

  public P6LogPreparedStatementInvocationHandler(PreparedStatement underlying,
                                                 ConnectionInformation connectionInformation,
                                                 String query) {
    super(underlying, connectionInformation, query);
  }

  @Override
  protected PreparedStatementInformation createStatementInformation(ConnectionInformation connectionInformation) {
    final PreparedStatementInformation preparedStatementInformation = new PreparedStatementInformation(connectionInformation);
    preparedStatementInformation.setStatementQuery(query);
    return preparedStatementInformation;
  }

  @Override
  protected P6LogPreparedStatementSetParameterValueDelegate getSetParameterValueDelegate() {
    return new P6LogPreparedStatementSetParameterValueDelegate(statementInformation);
  }

}
