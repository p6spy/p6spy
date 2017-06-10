
package com.p6spy.engine.event;

import com.p6spy.engine.common.CallableStatementInformation;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;

import java.sql.SQLException;

/**
 * This implementation of {@link JdbcEventListener} must always be applied as the first listener.
 * It populates the information objects {@link StatementInformation}, {@link PreparedStatementInformation},
 * {@link com.p6spy.engine.common.CallableStatementInformation} and {@link ResultSetInformation}
 */
public class DefaultEventListener extends JdbcEventListener {

  public final static DefaultEventListener INSTANCE = new DefaultEventListener();

  private DefaultEventListener() {
  }

  @Override
  public void onAfterAddBatch(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    statementInformation.setStatementQuery(sql);
  }

  @Override
  public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    statementInformation.setStatementQuery(sql);
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, int[] updateCounts, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos, int rowCount, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql, int rowCount, SQLException e) {
    statementInformation.setStatementQuery(sql);
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    statementInformation.setStatementQuery(sql);
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterGetResultSet(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext, SQLException e) {
    resultSetInformation.getStatementInformation().incrementTimeElapsed(timeElapsedNanos);
    if (hasNext) {
      resultSetInformation.incrementCurrRow();
    }
  }

  @Override
  public void onAfterCallableStatementSet(CallableStatementInformation statementInformation, String parameterName, Object value, SQLException e) {
    statementInformation.setParameterValue(parameterName, value);
  }

  @Override
  public void onAfterPreparedStatementSet(PreparedStatementInformation statementInformation, int parameterIndex, Object value, SQLException e) {
    statementInformation.setParameterValue(parameterIndex, value);
  }

}
