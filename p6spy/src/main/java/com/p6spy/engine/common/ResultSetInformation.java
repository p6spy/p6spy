/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.p6spy.engine.common;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class ResultSetInformation {

  private final StatementInformation statementInformation;
  private String query;
  private String preparedQuery;
  private final Map<String, Object> resultMap= new TreeMap<String, Object>();
  private int currRow = -1;

  public ResultSetInformation(final StatementInformation statementInformation) throws SQLException {
    this.statementInformation = statementInformation;
    this.preparedQuery = statementInformation.getPreparedStatementQuery();
    this.query = statementInformation.getStatementQuery();
  }

  public void generateLogMessage() {
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
