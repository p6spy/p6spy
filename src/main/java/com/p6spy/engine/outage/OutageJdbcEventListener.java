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

package com.p6spy.engine.outage;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.SimpleJdbcEventListener;

import java.sql.SQLException;

/**
 * This event listener registers method invocations at {@link P6OutageDetector}
 */
public class OutageJdbcEventListener extends SimpleJdbcEventListener {

  public static final OutageJdbcEventListener INSTANCE = new OutageJdbcEventListener();

  private OutageJdbcEventListener() {
  }

  @Override
  public void onBeforeCommit(ConnectionInformation connectionInformation) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.INSTANCE.registerInvocation(this, System.nanoTime(), "commit", "", "");
    }
  }

  @Override
  public void onAfterCommit(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.INSTANCE.unregisterInvocation(this);
    }
  }

  @Override
  public void onBeforeRollback(ConnectionInformation connectionInformation) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.INSTANCE.registerInvocation(this, System.nanoTime(), "rollback", "", "");
    }
  }

  @Override
  public void onAfterRollback(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.INSTANCE.unregisterInvocation(this);
    }
  }

  @Override
  public void onBeforeAnyAddBatch(StatementInformation statementInformation) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.INSTANCE.registerInvocation(this, System.nanoTime(), "batch",
        statementInformation.getSqlWithValues(), statementInformation.getStatementQuery());
    }
  }

  @Override
  public void onAfterAnyAddBatch(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.INSTANCE.unregisterInvocation(this);
    }
  }

  @Override
  public void onBeforeAnyExecute(StatementInformation statementInformation) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.INSTANCE.registerInvocation(this, System.nanoTime(), "statement",
        statementInformation.getSqlWithValues(), statementInformation.getStatementQuery());
    }
  }

  @Override
  public void onAfterAnyExecute(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.INSTANCE.unregisterInvocation(this);
    }
  }
}
