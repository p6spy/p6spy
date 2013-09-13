package com.p6spy.engine.logging;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Statement;


/**
 * Invocation handler for {@link Statement}
 */
class P6LogStatementInvocationHandler extends GenericInvocationHandler<Statement> {

  public P6LogStatementInvocationHandler(Statement underlying, final ConnectionInformation connectionInformation) {
    super(underlying);
    StatementInformation statementInformation = new StatementInformation(connectionInformation);

    P6LogStatementExecuteDelegate executeDelegate = new P6LogStatementExecuteDelegate(statementInformation);
    P6LogStatementExecuteBatchDelegate executeBatchDelegate = new P6LogStatementExecuteBatchDelegate(statementInformation);
    P6LogStatementAddBatchDelegate addBatchDelegate = new P6LogStatementAddBatchDelegate(statementInformation);


    addDelegate(
        new MethodNameMatcher("executeBatch"),
        executeBatchDelegate
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

  }

}
