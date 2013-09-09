package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Quinton McCombs (dt77102)
 * @since 09/2013
 */
public class ResultSetInformation {

  private final StatementInformation statementInformation;
  private final String query;
  private final String preparedQuery;
  private final Map<String, Object> resultMap = new TreeMap<String, Object>();
  private int currRow = -1;


  public ResultSetInformation(final StatementInformation statementInformation, final String query, final String preparedQuery) {
    this.statementInformation = statementInformation;
    this.query = query;
    this.preparedQuery = preparedQuery;
  }

  public String getPreparedQuery() {
    return preparedQuery;
  }

  public String getQuery() {
    return query;
  }

  public String generateResultSetLogMessage() {
    StringBuilder buffer = null;
    if (currRow > -1) {
        buffer = new StringBuilder();
        String comma = "";
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            buffer.append(comma);
            buffer.append(entry.getKey());
            buffer.append(" = ");
            buffer.append(entry.getValue());
            comma = ", ";
        }
        P6LogQuery.log("resultset", query, buffer.toString());
        resultMap.clear();
    }
    currRow++;
    return buffer == null ? null : buffer.toString();
  }
}
