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
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;

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
  public void onAddBatch(StatementInformation statementInformation, String sql) {
    statementInformation.setStatementQuery(sql);
  }

  @Override
  public void onExecute(StatementInformation statementInformation, long timeElapsedNanos) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    statementInformation.setStatementQuery(sql);
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    statementInformation.setStatementQuery(sql);
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    statementInformation.setStatementQuery(sql);
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onGetResultSet(StatementInformation statementInformation, long timeElapsedNanos) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext) {
    resultSetInformation.getStatementInformation().incrementTimeElapsed(timeElapsedNanos);
    if (hasNext) {
      resultSetInformation.incrementCurrRow();
    }
  }

  @Override
  public void onCallableStatementSet(CallableStatementInformation statementInformation, String parameterName, Object value) {
    statementInformation.setParameterValue(parameterName, value);
  }

  @Override
  public void onPreparedStatementSet(PreparedStatementInformation statementInformation, int parameterIndex, Object value) {
    statementInformation.setParameterValue(parameterIndex, value);
  }

}
