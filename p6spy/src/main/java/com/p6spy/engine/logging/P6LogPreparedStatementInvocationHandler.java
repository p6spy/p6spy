package com.p6spy.engine.logging;

import com.p6spy.engine.proxy.MethodNameAndParameterMatcher;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Invocation handler for {@link java.sql.PreparedStatement}
 */
class P6LogPreparedStatementInvocationHandler extends P6LogStatementInvocationHandler{

  public P6LogPreparedStatementInvocationHandler(PreparedStatement underlying,
                                                 ConnectionInformation connectionInformation,
                                                 String query,
                                                 final ParameterMetaData parameterMetaData)
      throws SQLException {

    super(underlying, connectionInformation);
    PreparedStatementInformation preparedStatementInformation = new PreparedStatementInformation(connectionInformation, parameterMetaData);
    preparedStatementInformation.setStatementQuery(query);

    P6LogPreparedStatementExecuteDelegate executeDelegate = new P6LogPreparedStatementExecuteDelegate(preparedStatementInformation);
    P6LogPreparedStatementAddBatchDelegate addBatchDelegate = new P6LogPreparedStatementAddBatchDelegate(preparedStatementInformation);
    P6LogSetParameterValueDelegate setParameterValueDelegate = new P6LogSetParameterValueDelegate(preparedStatementInformation);

    // add delegates for methods specific to a prepared statement.
    // Note: method name and parameters are being matched to prevent overlap with
    // similarly named method in Statement.
    addDelegate(
        new MethodNameAndParameterMatcher("execute"),
        executeDelegate
    );
    addDelegate(
        new MethodNameAndParameterMatcher("executeQuery"),
        executeDelegate
    );
    addDelegate(
        new MethodNameAndParameterMatcher("executeUpdate"),
        executeDelegate
    );
    addDelegate(
        new MethodNameAndParameterMatcher("addBatch"),
        addBatchDelegate
    );
    // intercept methods that set parameter values
    addDelegate(
        new MethodNameMatcher("set*"),
        setParameterValueDelegate
    );
    // replace the delegate added by superclass for the following methods
    addDelegate(
        new MethodNameMatcher("executeBatch"),
        executeDelegate
    );


  }

}
