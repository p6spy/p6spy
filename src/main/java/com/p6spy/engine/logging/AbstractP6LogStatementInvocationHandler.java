/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2016 P6Spy
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

import java.sql.Statement;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

public abstract class AbstractP6LogStatementInvocationHandler<S extends Statement, I extends StatementInformation> extends GenericInvocationHandler<S> {

  protected final I statementInformation;

  public AbstractP6LogStatementInvocationHandler(S underlying, final ConnectionInformation connectionInformation, String query) {
    super(underlying);
    this.statementInformation = createStatementInformation(connectionInformation);
    statementInformation.setStatementQuery(query);

    final boolean setStatementQueryInInvocationHandler = query == null;
    P6LogResultSetDelegate resultSetDelegate = new P6LogResultSetDelegate(statementInformation, setStatementQueryInInvocationHandler, Category.RESULTSET);
    P6LogResultSetDelegate executeDelegate = new P6LogResultSetDelegate(statementInformation, setStatementQueryInInvocationHandler, Category.STATEMENT);
    P6LogElapsedDelegate executeBatchDelegate = new P6LogElapsedDelegate(statementInformation, Category.BATCH);
    P6LogStatementAddBatchDelegate addBatchDelegate = new P6LogStatementAddBatchDelegate(statementInformation);

    addDelegate(new MethodNameMatcher("executeBatch"), executeBatchDelegate);
    addDelegate(new MethodNameMatcher("addBatch"), addBatchDelegate);
    addDelegate(new MethodNameMatcher("execute"), executeDelegate);
    addDelegate(new MethodNameMatcher("executeQuery"), executeDelegate);
    addDelegate(new MethodNameMatcher("executeUpdate"), executeDelegate);
    addDelegate(new MethodNameMatcher("getResultSet"), resultSetDelegate);
  }

  protected abstract I createStatementInformation(ConnectionInformation connectionInformation);
}
