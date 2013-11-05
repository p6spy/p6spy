package com.p6spy.engine.logging;

import com.p6spy.engine.common.ConnectionInformation;
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

    P6LogConnectionCommitDelegate commitDelegate = new P6LogConnectionCommitDelegate(connectionInformation);
    P6LogConnectionRollbackDelegate rollbackDelegate = new P6LogConnectionRollbackDelegate(connectionInformation);
    P6LogConnectionPrepareStatementDelegate prepareStatementDelegate = new P6LogConnectionPrepareStatementDelegate(connectionInformation);
    P6LogConnectionCreateStatementDelegate createStatementDelegate = new P6LogConnectionCreateStatementDelegate(connectionInformation);
    P6LogConnectionPrepareCallDelegate prepareCallDelegate = new P6LogConnectionPrepareCallDelegate(connectionInformation);

    // add delegates to perform logging on connection methods
    addDelegate(
        new MethodNameMatcher("commit"),
        commitDelegate
    );
    addDelegate(
        new MethodNameMatcher("rollback"),
        rollbackDelegate
    );

    // add delegates to return proxies for other methods
    addDelegate(
        new MethodNameMatcher("prepareStatement"),
        prepareStatementDelegate
    );

    addDelegate(
        new MethodNameMatcher("createStatement"),
        createStatementDelegate
    );

    addDelegate(
        new MethodNameMatcher("prepareCall"),
        prepareCallDelegate
    );

    // TODO add proxy for getDatabaseMetaData - but not used for logging module?

  }

}
