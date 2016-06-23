/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2016 P6Spy
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.p6spy.engine.logging;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.JdbcEventListener;

public class LoggingEventListener extends JdbcEventListener {

  public static final LoggingEventListener INSTANCE = new LoggingEventListener();

  private LoggingEventListener() {
  }

  @Override
  public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.BATCH, statementInformation);
  }

  @Override
  public void onAfterGetResultSet(StatementInformation statementInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.RESULTSET, statementInformation);
  }

  @Override
  public void onAfterCommit(ConnectionInformation connectionInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(connectionInformation.getConnectionId(), timeElapsedNanos, Category.COMMIT, connectionInformation);
  }

  @Override
  public void onAfterRollback(ConnectionInformation connectionInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(connectionInformation.getConnectionId(), timeElapsedNanos, Category.ROLLBACK, connectionInformation);
  }

  @Override
  public void onAfterResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value) {
    resultSetInformation.setColumnValue(Integer.toString(columnIndex), value);
  }

  @Override
  public void onAfterResultSetGet(ResultSetInformation resultSetInformation, String columnLabel, Object value) {
    resultSetInformation.setColumnValue(columnLabel, value);
  }

  @Override
  public void onBeforeResultSetNext(ResultSetInformation resultSetInformation) {
    if (resultSetInformation.getCurrRow() > -1) {
      // Log the columns that were accessed except on the first call to ResultSet.next().  The first time it is
      // called it is advancing to the first row.
      resultSetInformation.generateLogMessage();
    }
  }

  @Override
  public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext) {
    if (hasNext) {
      P6LogQuery.logElapsed(resultSetInformation.getConnectionId(), timeElapsedNanos, Category.RESULT, resultSetInformation);
    }
  }

  @Override
  public void onAfterResultSetClose(ResultSetInformation resultSetInformation) {
    if (resultSetInformation.getCurrRow() > -1) {
      // If the result set has not been advanced to the first row, there is nothing to log.
      resultSetInformation.generateLogMessage();
    }
  }

  @Override
  public void onAfterAddBatch(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
    onAfterAddBatch(statementInformation, timeElapsedNanos, null);
  }

  @Override
  public void onAfterAddBatch(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.BATCH, statementInformation);
  }
}
