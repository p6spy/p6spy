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

  public void onBeforeAddBatch(PreparedStatementInformation statementInformation) {
  }

  public void onAfterAddBatch(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
  }

  public void onBeforeAddBatch(StatementInformation statementInformation, String sql) {
  }

  public void onAfterAddBatch(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
  }


  public void onBeforeExecute(PreparedStatementInformation statementInformation) {
  }

  public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
  }

  public void onBeforeExecute(StatementInformation statementInformation, String sql) {
  }

  public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
  }


  public void onBeforeExecuteBatch(StatementInformation statementInformation) {
  }
  public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos) {
  }


  public void onBeforeExecuteUpdate(PreparedStatementInformation statementInformation) {
  }

  public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
  }

  public void onBeforeExecuteUpdate(StatementInformation statementInformation, String sql) {
  }

  public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
  }


  public void onBeforeExecuteQuery(PreparedStatementInformation statementInformation) {
  }

  public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
  }

  public void onBeforeExecuteQuery(StatementInformation statementInformation, String sql) {
  }

  public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
  }


  public void onAfterPreparedStatementSet(PreparedStatementInformation statementInformation, int parameterIndex, Object value) {
  }

  public void onAfterCallableStatementSet(CallableStatementInformation statementInformation, String parameterName, Object value) {
  }

  public void onAfterGetResultSet(StatementInformation statementInformation, long timeElapsedNanos) {
  }

  public void onBeforeResultSetNext(ResultSetInformation resultSetInformation) {
  }

  public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext) {
  }

  public void onAfterResultSetClose(ResultSetInformation resultSetInformation) {
  }

  public void onAfterResultSetGet(ResultSetInformation resultSetInformation, String columnLabel, Object value) {
  }

  public void onAfterResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value) {
  }

  public void onBeforeCommit(ConnectionInformation connectionInformation) {
  }

  public void onAfterCommit(ConnectionInformation connectionInformation, long timeElapsedNanos) {
  }

  public void onAfterConnectionClose(ConnectionInformation connectionInformation) {
  }

  public void onBeforeRollback(ConnectionInformation connectionInformation) {
  }

  public void onAfterRollback(ConnectionInformation connectionInformation, long timeElapsedNanos) {
  }
}
