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
