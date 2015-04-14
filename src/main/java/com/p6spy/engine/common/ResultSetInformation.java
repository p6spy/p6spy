/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
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
package com.p6spy.engine.common;

import com.p6spy.engine.logging.Category;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class ResultSetInformation implements Loggable {

  private final StatementInformation statementInformation;
  private String query;
  private final Map<String, Object> resultMap= new LinkedHashMap<String, Object>();
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
    if( lastRowLogged != currRow ) {
      P6LogQuery.log(Category.RESULTSET, this);
      resultMap.clear();
      lastRowLogged = currRow;
    }
  }

  public ConnectionInformation getConnectionInformation() {
    return statementInformation.getConnectionInformation();
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
  
}
