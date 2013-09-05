package com.p6spy.engine.logging;

import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameAndParameterMatcher;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Statement;


/**
 * Invocation handler for {@link Statement}
 */
class P6LogStatementInvocationHandler extends GenericInvocationHandler<Statement> {
  private final P6LogConnectionInvocationHandler connectionInvocationHandler;
  private String statementQuery;

  public P6LogStatementInvocationHandler(Statement underlying, P6LogConnectionInvocationHandler connectionInvocationHandler) {
    super(underlying);
    this.connectionInvocationHandler = connectionInvocationHandler;

    P6LogStatementExecuteDelegate executeDelegate = new P6LogStatementExecuteDelegate(this);
    P6LogStatementExecuteBatchDelegate executeBatchDelegate = new P6LogStatementExecuteBatchDelegate(this);
    P6LogStatementAddBatchDelegate addBatchDelegate = new P6LogStatementAddBatchDelegate(this);


    // These methods do not exist in the PreparedStatement interface.
    // It is safe to match by name only.
    addDelegate(
        new MethodNameMatcher("executeBatch"),
        executeBatchDelegate
    );

    // These methods do exist in the PreparedStatement interface.
    // Match should be performed by name and parameters!
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

  public String getStatementQuery() {
    return statementQuery;
  }

  public void setStatementQuery(String statementQuery) {
    this.statementQuery = statementQuery;
  }

  public int getConnectionId() {
    return connectionInvocationHandler.getConnectionId();
  }
}
