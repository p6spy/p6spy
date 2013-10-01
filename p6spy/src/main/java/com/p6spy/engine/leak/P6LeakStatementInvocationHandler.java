/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.p6spy.engine.leak;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Statement;


/**
 * Invocation handler for {@link java.sql.Statement}
 */
class P6LeakStatementInvocationHandler extends GenericInvocationHandler<Statement> {

  public P6LeakStatementInvocationHandler(Statement underlying, final ConnectionInformation connectionInformation) {
    super(underlying);
    StatementInformation statementInformation = new StatementInformation(connectionInformation);
    P6Objects.open(statementInformation);

    P6LeakStatementCloseDelegate closeDelegate = new P6LeakStatementCloseDelegate(statementInformation);
    P6LeakStatementExecuteDelegate executeDelegate = new P6LeakStatementExecuteDelegate(statementInformation);

    addDelegate(
        new MethodNameMatcher("close"),
        closeDelegate
    );
    addDelegate(
        new MethodNameMatcher("execute*"),
        executeDelegate
    );

  }

}
