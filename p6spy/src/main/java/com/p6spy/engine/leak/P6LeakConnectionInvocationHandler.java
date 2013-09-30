package com.p6spy.engine.leak;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Connection;

/**
 * Invocation handler for {@link java.sql.Connection}
 */
public class P6LeakConnectionInvocationHandler extends GenericInvocationHandler<Connection> {

  public P6LeakConnectionInvocationHandler(Connection underlying) {
    super(underlying);
    ConnectionInformation connectionInformation = new ConnectionInformation();
    P6Objects.open(connectionInformation);

    P6LeakConnectionCloseDelegate closeDelegate = new P6LeakConnectionCloseDelegate(connectionInformation);
    P6LeakConnectionCreateStatementDelegate createStatementDelegate = new P6LeakConnectionCreateStatementDelegate(connectionInformation);

    addDelegate(
        new MethodNameMatcher("closeDelegate"),
        closeDelegate
    );

    addDelegate(
        new MethodNameMatcher("prepareStatement"),
        createStatementDelegate
    );

    addDelegate(
        new MethodNameMatcher("createStatement"),
        createStatementDelegate
    );

    addDelegate(
        new MethodNameMatcher("prepareCall"),
        createStatementDelegate
    );

  }

}
