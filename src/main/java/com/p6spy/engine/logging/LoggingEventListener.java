/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
 *
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
 */

package com.p6spy.engine.logging;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.Loggable;
import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.SimpleJdbcEventListener;

import java.sql.SQLException;

/**
 * This event listener is responsible for logging the SQL statements and the execution time
 * <p>
 * To use a custom implementation for logging, extend this class and add the fully qualified class name of your
 * implementation to <code>src/main/resources/META-INF/services/com.p6spy.engine.logging.LoggingEventListener</code>.
 */
public class LoggingEventListener extends SimpleJdbcEventListener {

  public static final LoggingEventListener INSTANCE = new LoggingEventListener();

  protected LoggingEventListener() {
  }

  @Override
  public void onAfterAnyExecute(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    logElapsed(statementInformation, timeElapsedNanos, Category.STATEMENT, e);
  }

  @Override
  public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, int[] updateCounts, SQLException e) {
    logElapsed(statementInformation, timeElapsedNanos, Category.BATCH, e);
  }

  @Override
  public void onAfterCommit(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
    logElapsed(connectionInformation, timeElapsedNanos, Category.COMMIT, e);
  }

  @Override
  public void onAfterRollback(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
    logElapsed(connectionInformation, timeElapsedNanos, Category.ROLLBACK, e);
  }

  @Override
  public void onAfterAnyAddBatch(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    logElapsed(statementInformation, timeElapsedNanos, Category.BATCH, e);
  }

  @Override
  public void onAfterGetResultSet(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    logElapsed(statementInformation, timeElapsedNanos, Category.RESULTSET, e);
  }

  @Override
  public void onAfterResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value, SQLException e) {
    resultSetInformation.setColumnValue(Integer.toString(columnIndex), value);
  }

  @Override
  public void onAfterResultSetGet(ResultSetInformation resultSetInformation, String columnLabel, Object value, SQLException e) {
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
  public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext, SQLException e) {
    if (hasNext) {
      logElapsed(resultSetInformation, timeElapsedNanos, Category.RESULT, e);
    }
  }

  @Override
  public void onAfterResultSetClose(ResultSetInformation resultSetInformation, SQLException e) {
    if (resultSetInformation.getCurrRow() > -1) {
      // If the result set has not been advanced to the first row, there is nothing to log.
      resultSetInformation.generateLogMessage();
    }
  }

  protected void logElapsed(Loggable loggable, long timeElapsedNanos, Category category, SQLException e) {
    P6LogQuery.logElapsed(loggable.getConnectionInformation().getConnectionId(), timeElapsedNanos, category, loggable);
  }
}
