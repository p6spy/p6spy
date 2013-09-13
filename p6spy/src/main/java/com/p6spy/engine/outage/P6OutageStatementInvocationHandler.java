package com.p6spy.engine.outage;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Statement;


/**
 * Invocation handler for {@link java.sql.Statement}
 */
class P6OutageStatementInvocationHandler extends GenericInvocationHandler<Statement> {

  public P6OutageStatementInvocationHandler(Statement underlying, final ConnectionInformation connectionInformation) {
    super(underlying);
    StatementInformation statementInformation = new StatementInformation(connectionInformation);

    P6OutageStatementExecuteDelegate executeDelegate = new P6OutageStatementExecuteDelegate(statementInformation);
    P6OutageStatementAddBatchDelegate addBatchDelegate = new P6OutageStatementAddBatchDelegate(statementInformation);


    addDelegate(
        new MethodNameMatcher("execute*"),
        executeDelegate
    );
    addDelegate(
        new MethodNameMatcher("addBatch"),
        addBatchDelegate
    );

  }

}
