
package com.p6spy.engine.common;


/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class StatementInformation implements Loggable {

  private final ConnectionInformation connectionInformation;
  private String statementQuery;
  private long totalTimeElapsed;
  
  public StatementInformation(final ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  public String getStatementQuery() {
    return statementQuery;
  }

  public void setStatementQuery(final String statementQuery) {
    this.statementQuery = statementQuery;
  }

  @Override
  public int getConnectionId() {
    return connectionInformation.getConnectionId();
  }

  public ConnectionInformation getConnectionInformation() {
    return connectionInformation;
  }

  @Override
  public String getSqlWithValues() {
    return getSql();
  }

  @Override
  public String getSql() {
    return statementQuery;
  }

  public long getTotalTimeElapsed() {
    return totalTimeElapsed;
  }

  public void incrementTimeElapsed(long timeElapsedNanos) {
    totalTimeElapsed += timeElapsedNanos;
  }
}
