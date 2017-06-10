
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
  public void onBeforeAnyAddBatch(StatementInformation statementInformation) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, System.nanoTime(), "batch",
        statementInformation.getSqlWithValues(), statementInformation.getStatementQuery());
    }
  }

  @Override
  public void onAfterAnyAddBatch(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().unregisterInvocation(this);
    }
  }

  @Override
  public void onBeforeAnyExecute(StatementInformation statementInformation) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, System.nanoTime(), "statement",
        statementInformation.getSqlWithValues(), statementInformation.getStatementQuery());
    }
  }

  @Override
  public void onAfterAnyExecute(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    if (P6OutageOptions.getActiveInstance().getOutageDetection()) {
      P6OutageDetector.getInstance().unregisterInvocation(this);
    }
  }
}
