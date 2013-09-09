package com.p6spy.engine.logging;

import java.sql.SQLException;

/**
 * @author Quinton McCombs (dt77102)
 * @since 09/2013
 */
public class StatementInformation {

  private final ConnectionInformation connectionInformation;
  private String statementQuery;

  public StatementInformation(final ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  public String getStatementQuery() {
    return statementQuery;
  }

  public void setStatementQuery(final String statementQuery) {
    this.statementQuery = statementQuery;
  }

  public int getConnectionId() {
    return connectionInformation.getConnectionId();
  }

  public String getPreparedStatementQuery() throws SQLException {
    return "";
  }
}
