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
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.CallableStatement;

/**
 * Invocation handler for {@link java.sql.PreparedStatement}
 */
class P6OutageCallableStatementInvocationHandler extends GenericInvocationHandler<CallableStatement>{

  public P6OutageCallableStatementInvocationHandler(CallableStatement underlying,
                                                    ConnectionInformation connectionInformation,
                                                    String query) {

      super(underlying);
    PreparedStatementInformation preparedStatementInformation = new PreparedStatementInformation(connectionInformation);
    preparedStatementInformation.setStatementQuery(query);

    P6OutagePreparedStatementExecuteDelegate executeDelegate = new P6OutagePreparedStatementExecuteDelegate(preparedStatementInformation);
    P6OutagePreparedStatementAddBatchDelegate addBatchDelegate = new P6OutagePreparedStatementAddBatchDelegate(preparedStatementInformation);
    P6OutagePreparedStatementSetParameterValueDelegate setParameterValueDelegate = new P6OutagePreparedStatementSetParameterValueDelegate(preparedStatementInformation);

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
