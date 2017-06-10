
package com.p6spy.engine.common;

import java.util.LinkedHashMap;
import java.util.Map;

import com.p6spy.engine.logging.Category;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class ResultSetInformation implements Loggable {

  private final StatementInformation statementInformation;
  private String query;
  private final Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
  private int currRow = -1;
  private int lastRowLogged = -1;

  public ResultSetInformation(final StatementInformation statementInformation) {
    this.statementInformation = statementInformation;
    this.query = statementInformation.getStatementQuery();
  }

  /**
   * Generates log message with column values accessed if the row's column values have not already been logged.
   */
  public void generateLogMessage() {
    if (lastRowLogged != currRow) {
      P6LogQuery.log(Category.RESULTSET, this);
      resultMap.clear();
      lastRowLogged = currRow;
    }
  }

  @Override
  public int getConnectionId() {
    return statementInformation.getConnectionId();
  }

  public int getCurrRow() {
    return currRow;
  }

  public void incrementCurrRow() {
    this.currRow++;
  }

  public void setColumnValue(String columnName, Object value) {
    resultMap.put(columnName, value);
  }

  @Override
  public String getSql() {
    return query;
  }

  @Override
  public String getSqlWithValues() {
    final StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(entry.getKey());
      sb.append(" = ");
      sb.append(entry.getValue());
    }

    return sb.toString();
  }

  public StatementInformation getStatementInformation() {
    return statementInformation;
  }

}
