package com.p6spy.engine.logging;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Invocation handler for {@link java.sql.PreparedStatement}
 */
class P6LogPreparedStatementInvocationHandler extends GenericInvocationHandler<PreparedStatement>{

  public P6LogPreparedStatementInvocationHandler(PreparedStatement underlying,
                                                 ConnectionInformation connectionInformation,
                                                 String query,
                                                 final ParameterMetaData parameterMetaData)
      throws SQLException {

    super(underlying);
    PreparedStatementInformation preparedStatementInformation = new PreparedStatementInformation(connectionInformation, parameterMetaData);
    preparedStatementInformation.setStatementQuery(query);

    P6LogPreparedStatementExecuteDelegate executeDelegate = new P6LogPreparedStatementExecuteDelegate(preparedStatementInformation);
    P6LogPreparedStatementAddBatchDelegate addBatchDelegate = new P6LogPreparedStatementAddBatchDelegate(preparedStatementInformation);
    P6LogPreparedStatementSetParameterValueDelegate setParameterValueDelegate = new P6LogPreparedStatementSetParameterValueDelegate(preparedStatementInformation);

    addDelegate(
        new MethodNameMatcher("executeBatch"),
        executeDelegate
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
    addDelegate(
        new MethodNameMatcher("set*"),
        setParameterValueDelegate
    );


  }

}
