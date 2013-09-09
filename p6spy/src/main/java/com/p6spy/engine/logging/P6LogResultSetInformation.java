package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class P6LogResultSetInformation {

  private final StatementInformation statementInformation;
  private String query;
  private String preparedQuery;
  private Map<String, Object> resultMap= new TreeMap<String, Object>();
  private int currRow = -1;

  public P6LogResultSetInformation(final StatementInformation statementInformation) throws SQLException {
    this.statementInformation = statementInformation;
    this.preparedQuery = statementInformation.getPreparedStatementQuery();
    this.query = statementInformation.getStatementQuery();
  }

  public String generateLogMessage() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(entry.getKey());
      sb.append(" = ");
      sb.append(entry.getValue());
    }
    P6LogQuery.log("resultset", query, sb.toString());
    resultMap.clear();
    return sb.toString();
  }

  public String getPreparedQuery() {
    return preparedQuery;
  }

  public String getQuery() {
    return query;
  }

  public int getConnectionId() {
    return statementInformation.getConnectionId();
  }

  public int getCurrRow() {
    return currRow;
  }

  public void setCurrRow(final int currRow) {
    this.currRow = currRow;
  }

  public void setColumnValue(String columnName, Object value) {
    resultMap.put(columnName, value);
  }
}
