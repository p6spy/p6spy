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
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * Invocation handler for {@link java.sql.PreparedStatement}
 */
class P6LogCallableStatementInvocationHandler extends GenericInvocationHandler<CallableStatement>{

  public P6LogCallableStatementInvocationHandler(CallableStatement underlying,
                                                 ConnectionInformation connectionInformation,
                                                 String query)
      throws SQLException {

    super(underlying);
    PreparedStatementInformation preparedStatementInformation = new PreparedStatementInformation(connectionInformation);
    preparedStatementInformation.setStatementQuery(query);

    P6LogPreparedStatementExecuteDelegate executeDelegate = new P6LogPreparedStatementExecuteDelegate(preparedStatementInformation);
    P6LogPreparedStatementAddBatchDelegate addBatchDelegate = new P6LogPreparedStatementAddBatchDelegate(preparedStatementInformation);
    P6LogPreparedStatementSetParameterValueDelegate setParameterValueDelegate = new P6LogPreparedStatementSetParameterValueDelegate(preparedStatementInformation);

    addDelegate(
        new MethodNameMatcher("executeBatch"),
        executeDelegate
    );
    addDelegate(
        new MethodNameMatcher("addBatch"),
        addBatchDelegate
    );
    addDelegate(
        new MethodNameMatcher("execute"),
        executeDelegate
    );
    addDelegate(
        new MethodNameMatcher("executeQuery"),
        executeDelegate
    );
    addDelegate(
        new MethodNameMatcher("executeUpdate"),
        executeDelegate
    );
    addDelegate(
        new MethodNameMatcher("set*"),
        setParameterValueDelegate
    );


  }

}
