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
package com.p6spy.engine.outage;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.JdbcEventListener;

import java.sql.SQLException;

/**
 * This event listener registers method invocations at {@link P6OutageDetector}
 */
public class OutageJdbcEventListener extends JdbcEventListener {

  public static final OutageJdbcEventListener INSTANCE = new OutageJdbcEventListener();

  private OutageJdbcEventListener() {
  }

  @Override
  public void onBeforeCommit(ConnectionInformation connectionInformation) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, System.nanoTime(), "commit", "", "");
    }
  }

  @Override
  public void onAfterCommit(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().unregisterInvocation(this);
    }
  }

  @Override
  public void onBeforeRollback(ConnectionInformation connectionInformation) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, System.nanoTime(), "rollback", "", "");
    }
  }

  @Override
  public void onAfterRollback(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().unregisterInvocation(this);
    }
  }

  @Override
  public void onBeforeAddBatch(PreparedStatementInformation statementInformation) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, System.nanoTime(), "batch",
        statementInformation.getStatementQuery(), statementInformation.getSqlWithValues());
    }
  }

  @Override
  public void onAfterAddBatch(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    onAfterAddBatch(statementInformation, timeElapsedNanos, null, e);
  }

  @Override
  public void onBeforeAddBatch(StatementInformation statementInformation, String sql) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, System.nanoTime(), "batch", "", statementInformation.getStatementQuery());
    }
  }

  @Override
  public void onAfterAddBatch(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().unregisterInvocation(this);
    }
  }

  @Override
  public void onBeforeExecute(PreparedStatementInformation statementInformation) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, System.nanoTime(), "statement",
        statementInformation.getStatementQuery(), statementInformation.getSqlWithValues());
    }
  }

  @Override
  public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().unregisterInvocation(this);
    }
  }

  @Override
  public void onBeforeExecute(StatementInformation statementInformation, String sql) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, System.nanoTime(), "statement", "", statementInformation.getStatementQuery());
    }
  }

  @Override
  public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().unregisterInvocation(this);
    }
  }

  @Override
  public void onBeforeExecuteBatch(StatementInformation statementInformation) {
    onBeforeExecute(statementInformation, null);
  }

  @Override
  public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    onAfterExecute(statementInformation, timeElapsedNanos, null, e);
  }

  @Override
  public void onBeforeExecuteUpdate(PreparedStatementInformation statementInformation) {
    onBeforeExecute(statementInformation);
  }

  @Override
  public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    onAfterExecute(statementInformation, timeElapsedNanos, e);
  }

  @Override
  public void onBeforeExecuteUpdate(StatementInformation statementInformation, String sql) {
    onBeforeExecute(statementInformation, sql);
  }

  @Override
  public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    onAfterExecute(statementInformation, timeElapsedNanos, sql, e);
  }

  @Override
  public void onBeforeExecuteQuery(PreparedStatementInformation statementInformation) {
    onBeforeExecute(statementInformation);
  }

  @Override
  public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    onAfterExecute(statementInformation, timeElapsedNanos, e);
  }

  @Override
  public void onBeforeExecuteQuery(StatementInformation statementInformation, String sql) {
    onBeforeExecute(statementInformation, sql);
  }

  @Override
  public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    onAfterExecute(statementInformation, timeElapsedNanos, sql, e);
  }
}
