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
package com.p6spy.engine.event;

import com.p6spy.engine.common.CallableStatementInformation;
import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;

/**
 * Implementations of this class receive notifications for interesting JDBC events.
 * <p>
 * This class intentionally is not an interface so that methods can be added without breaking existing implementations.
 */
public abstract class JdbcEventListener {

  public void onAddBatch(PreparedStatementInformation statementInformation) {
  }

  public void onAddBatch(StatementInformation statementInformation, String sql) {
  }

  public void onExecute(StatementInformation statementInformation, long timeElapsedNanos) {
  }

  public void onExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
  }

  public void onExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos) {
  }

  public void onExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
  }

  public void onExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
  }

  public void onExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos) {
  }

  public void onExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
  }

  public void onPreparedStatementSet(PreparedStatementInformation statementInformation, int parameterIndex, Object value) {
  }

  public void onCallableStatementSet(CallableStatementInformation statementInformation, String parameterName, Object value) {
  }

  public void onGetResultSet(StatementInformation statementInformation, long timeElapsedNanos) {
  }

  public void onResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext) {
  }

  public void onResultSetClose(ResultSetInformation resultSetInformation) {
  }

  public void onResultSetGet(ResultSetInformation resultSetInformation, String columnLabel, Object value) {
  }

  public void onResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value) {
  }

  public void onCommit(ConnectionInformation connectionInformation, long timeElapsedNanos) {
  }

  public void onConnectionClose(ConnectionInformation connectionInformation) {
  }

  public void onRollback(ConnectionInformation connectionInformation, long timeElapsedNanos) {
  }
}
