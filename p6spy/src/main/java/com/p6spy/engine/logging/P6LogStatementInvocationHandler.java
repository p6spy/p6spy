package com.p6spy.engine.logging;

import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameAndParameterMatcher;
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


    // These methods do not exist in the PreparedStatement interface.
    // It is safe to match by name only.
    addDelegate(
        new MethodNameMatcher("executeBatch"),
        executeBatchDelegate
    );

    // These methods exist in the PreparedStatement interface.
    // Match should be performed by name and parameters to avoid conflicts.
    addDelegate(
        new MethodNameAndParameterMatcher("addBatch", String.class),
        addBatchDelegate
    );
    addDelegate(
        new MethodNameAndParameterMatcher("execute", String.class),
        executeDelegate
    );
    addDelegate(
        new MethodNameAndParameterMatcher("executeQuery", String.class),
        executeDelegate
    );
    addDelegate(
        new MethodNameAndParameterMatcher("executeUpdate", String.class),
        executeDelegate
    );
    addDelegate(
        new MethodNameAndParameterMatcher("executeUpdate", String.class, int.class),
        executeDelegate
    );
    addDelegate(
        new MethodNameAndParameterMatcher("executeUpdate", String.class, int[].class),
        executeDelegate
    );
    addDelegate(
        new MethodNameAndParameterMatcher("executeUpdate", String.class, String[].class),
        executeDelegate
    );

  }

}
