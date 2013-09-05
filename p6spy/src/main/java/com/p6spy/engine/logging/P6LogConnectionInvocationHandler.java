package com.p6spy.engine.logging;

import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Connection;

/**
 * Invocation handler for {@link java.sql.Connection}
 */
public class P6LogConnectionInvocationHandler extends GenericInvocationHandler<Connection> {

  private static int counter = 0;
  private final int connectionId = counter++;

  public P6LogConnectionInvocationHandler(Connection underlying) {
    super(underlying);
    // add delegates to perform logging on connection methods
    addDelegate(
        new MethodNameMatcher("commit"),
        new P6LogCommitDelegate(this)
    );
    addDelegate(
        new MethodNameMatcher("rollback"),
        new P6LogRollbackDelegate(this)
    );

    // add delegates to return proxies for other methods
    addDelegate(
        new MethodNameMatcher("prepareStatement"),
        new P6LogPrepareStatementDelegate(this)
    );

    addDelegate(
        new MethodNameMatcher("createStatement"),
        new P6LogCreateStatementDelegate(this)
    );

    addDelegate(
        new MethodNameMatcher("prepareCall"),
        new P6LogCreateStatementDelegate(this)
    );

  }

  public int getConnectionId() {
    return connectionId;
  }
}
