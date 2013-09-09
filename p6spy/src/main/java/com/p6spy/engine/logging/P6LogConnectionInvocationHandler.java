package com.p6spy.engine.logging;

import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Connection;

/**
 * Invocation handler for {@link java.sql.Connection}
 */
public class P6LogConnectionInvocationHandler extends GenericInvocationHandler<Connection> {

  public P6LogConnectionInvocationHandler(Connection underlying) {
    super(underlying);
    ConnectionInformation connectionInformation = new ConnectionInformation();

    // add delegates to perform logging on connection methods
    addDelegate(
        new MethodNameMatcher("commit"),
        new P6LogCommitDelegate(connectionInformation)
    );
    addDelegate(
        new MethodNameMatcher("rollback"),
        new P6LogRollbackDelegate(connectionInformation)
    );

    // add delegates to return proxies for other methods
    addDelegate(
        new MethodNameMatcher("prepareStatement"),
        new P6LogPrepareStatementDelegate(connectionInformation)
    );

    addDelegate(
        new MethodNameMatcher("createStatement"),
        new P6LogCreateStatementDelegate(connectionInformation)
    );

    // TODO should be a callable statement
    addDelegate(
        new MethodNameMatcher("prepareCall"),
        new P6LogCreateStatementDelegate(connectionInformation)
    );

    // TODO add proxy for getDatabaseMetaData - but not used for logging module?

  }

}
