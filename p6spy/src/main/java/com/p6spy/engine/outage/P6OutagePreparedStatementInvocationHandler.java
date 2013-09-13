package com.p6spy.engine.outage;

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
class P6OutagePreparedStatementInvocationHandler extends GenericInvocationHandler<PreparedStatement>{

  public P6OutagePreparedStatementInvocationHandler(PreparedStatement underlying,
                                                    ConnectionInformation connectionInformation,
                                                    String query,
                                                    final ParameterMetaData parameterMetaData)
      throws SQLException {

    super(underlying);
    PreparedStatementInformation preparedStatementInformation = new PreparedStatementInformation(connectionInformation, parameterMetaData);
    preparedStatementInformation.setStatementQuery(query);

    P6OutagePreparedStatementExecuteDelegate executeDelegate = new P6OutagePreparedStatementExecuteDelegate(preparedStatementInformation);
    P6OutagePreparedStatementAddBatchDelegate addBatchDelegate = new P6OutagePreparedStatementAddBatchDelegate(preparedStatementInformation);
    P6OutagePreparedStatementSetParameterValueDelegate setParameterValueDelegate = new P6OutagePreparedStatementSetParameterValueDelegate(preparedStatementInformation);

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
