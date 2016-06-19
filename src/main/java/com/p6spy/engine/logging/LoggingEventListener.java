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
  public void onExecute(StatementInformation statementInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.STATEMENT, statementInformation);
  }

  @Override
  public void onExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.BATCH, statementInformation);
  }

  @Override
  public void onGetResultSet(StatementInformation statementInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(statementInformation.getConnectionId(), timeElapsedNanos, Category.RESULTSET, statementInformation);
  }

  @Override
  public void onCommit(ConnectionInformation connectionInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(connectionInformation.getConnectionId(), timeElapsedNanos, Category.COMMIT, connectionInformation);
  }

  @Override
  public void onRollback(ConnectionInformation connectionInformation, long timeElapsedNanos) {
    P6LogQuery.logElapsed(connectionInformation.getConnectionId(), timeElapsedNanos, Category.ROLLBACK, connectionInformation);
  }

  @Override
  public void onResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value) {
    resultSetInformation.setColumnValue(Integer.toString(columnIndex), value);
  }

  @Override
  public void onResultSetGet(ResultSetInformation resultSetInformation, String columnLabel, Object value) {
    resultSetInformation.setColumnValue(columnLabel, value);
  }

  @Override
  public void onResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext) {
    if (resultSetInformation.getCurrRow() > -1) {
      // Log the columns that were accessed except on the first call to ResultSet.next().  The first time it is
      // called it is advancing to the first row.
      resultSetInformation.generateLogMessage();
    }
    if (hasNext) {
      P6LogQuery.logElapsed(resultSetInformation.getConnectionId(), timeElapsedNanos, Category.RESULT, resultSetInformation);
    }
  }

  @Override
  public void onResultSetClose(ResultSetInformation resultSetInformation) {
    if (resultSetInformation.getCurrRow() > -1) {
      // If the result set has not been advanced to the first row, there is nothing to log.
      resultSetInformation.generateLogMessage();
    }
  }
}
