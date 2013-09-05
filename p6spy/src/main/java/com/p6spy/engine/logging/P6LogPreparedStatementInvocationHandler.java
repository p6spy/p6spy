package com.p6spy.engine.logging;

import com.p6spy.engine.proxy.MethodNameAndParameterMatcher;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Invocation handler for {@link java.sql.PreparedStatement}
 */
class P6LogPreparedStatementInvocationHandler extends P6LogStatementInvocationHandler{
  private List<String> parameterValues;
  private int parameterCount;

  public P6LogPreparedStatementInvocationHandler(PreparedStatement underlying,
                                                 P6LogConnectionInvocationHandler connectionInvocationHandler,
                                                 String query,
                                                 final ParameterMetaData parameterMetaData)
      throws SQLException {

    super(underlying, connectionInvocationHandler);
    setStatementQuery(query);
    parameterValues = new ArrayList<String>(parameterMetaData.getParameterCount());
    parameterCount = parameterMetaData.getParameterCount();

    // pre-populate parameter values list with nulls to allow for the values to be set later by index
    for( int i = 0; i < parameterCount; i++) {
      parameterValues.add(null);
    }

    P6LogPreparedStatementExecuteDelegate executeDelegate = new P6LogPreparedStatementExecuteDelegate(this);
    P6LogPreparedStatementAddBatchDelegate addBatchDelegate = new P6LogPreparedStatementAddBatchDelegate(this);
    P6LogSetParameterValueDelegate setParameterValueDelegate = new P6LogSetParameterValueDelegate(this);

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

  /**
   * Generates the query for the prepared statement with all parameter placeholders
   * replaced with the actual parameter values
   *
   * @return the SQL
   * @throws SQLException
   */
  public String getPreparedStatementQuery() throws SQLException {
    StringBuilder sb = new StringBuilder();

    String statementQuery = getStatementQuery();
    ParameterMetaData metaData = ((PreparedStatement)getUnderlying()).getParameterMetaData();

    // iterate over the characters in the query replacing the parameter placeholders
    // with the actual values
    int currentParameter = 0;
    for( int pos = 0; pos < statementQuery.length(); pos ++) {
      char character = statementQuery.charAt(pos);
      if( statementQuery.charAt(pos) == '?' && currentParameter < parameterCount) {
        // replace with parameter value
        boolean shouldQuote = true;
        switch( metaData.getParameterType(currentParameter+1)) {
          case Types.BIT:
          case Types.TINYINT:
          case Types.SMALLINT:
          case Types.INTEGER:
          case Types.BIGINT:
          case Types.FLOAT:
          case Types.REAL:
          case Types.DOUBLE:
          case Types.NUMERIC:
          case Types.DECIMAL:
          case Types.BOOLEAN:
            shouldQuote = false;
        }
        if( parameterValues.get(currentParameter) == null) {
          sb.append("NULL");
        } else {
          if( shouldQuote ) {
            sb.append("'");
          }
          sb.append(parameterValues.get(currentParameter));
          if( shouldQuote ) {
            sb.append("'");
          }
        }
      } else {
        sb.append(character);
      }
    }

    return sb.toString();
  }

  /**
   * Records the value of a parameter.
   * @param position the position of the parameter (starts with 1 not 0)
   * @param value the value of the parameter
   */
  void setParameterValue(final int position, final String value) {
    parameterValues.set(position-1,value);
  }
}
