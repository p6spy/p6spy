package com.p6spy.engine.leak;

import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
class P6LeakResultSetInvocationHandler extends GenericInvocationHandler<ResultSet> {

  /**
   * Creates a new invocation handler for the given object.
   *
   * @param underlying The object being proxied
   */
  public P6LeakResultSetInvocationHandler(final ResultSet underlying, final StatementInformation statementInformation)
      throws SQLException {
    super(underlying);

    ResultSetInformation resultSetInformation = new ResultSetInformation(statementInformation);
    P6Objects.open(resultSetInformation);

    P6LeakResultSetCloseDelegate closeDelegate = new P6LeakResultSetCloseDelegate(resultSetInformation);

    addDelegate(
        new MethodNameMatcher("close"),
        closeDelegate
    );

  }
}
