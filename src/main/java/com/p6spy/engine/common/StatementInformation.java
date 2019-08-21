/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2019 P6Spy
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
package com.p6spy.engine.common;


import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class StatementInformation implements Loggable {

  private final ConnectionInformation connectionInformation;
  private String statementQuery;
  private long totalTimeElapsed;
  private int fetchSize;
  private int queryTimeout;

  private static final String ATTRIBUTE_PREFIX = "Statement.";

  /**
   * The states that this context can be in at any given instance.
   */
  private enum LogAttribute {
    getQueryTimeout, getFetchSize, isAutoCommitted
  }

  private static List<LogAttribute> enabledAttributes = null;

  public StatementInformation(final ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  public String getStatementQuery() {
    return statementQuery;
  }

  public void setStatementQuery(final String statementQuery) {
    this.statementQuery = statementQuery;
  }

  /** {@inheritDoc} */
  @Override
  public ConnectionInformation getConnectionInformation() {
    return this.connectionInformation;
  }

  @Override
  public String getSqlWithValues() {
    return getSql();
  }

  @Override
  public String getSql() {
    return getStatementQuery();
  }

  public long getTotalTimeElapsed() {
    return totalTimeElapsed;
  }

  public void incrementTimeElapsed(long timeElapsedNanos) {
    totalTimeElapsed += timeElapsedNanos;
  }

  public void setQueryTimeout(int seconds) {
    queryTimeout = seconds;
  }

  public void setFetchSize(int rows) {
    fetchSize = rows;
  }

  public void captureAttributeValues(Statement statement) {
    try {
      queryTimeout = statement.getQueryTimeout();
    } catch (SQLException ignored) {}

    try {
      fetchSize = statement.getFetchSize();
    } catch (SQLException ignored) {}
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, String> getAttributeValues() {
    Map<String, String> values = null;

    if (enabledAttributes != null) {
      values = new HashMap<String, String>(enabledAttributes.size());

      for (LogAttribute attr : enabledAttributes) {
        switch (attr) {
          case getQueryTimeout:
            values.put(attr.name(), String.valueOf(queryTimeout));
            break;

          case getFetchSize:
            values.put(attr.name(), String.valueOf(fetchSize));
            break;

          case isAutoCommitted:
            try {
              values.put(attr.name(), Boolean.toString(connectionInformation.getConnection().getAutoCommit()));
            } catch (SQLException ignored) {}
            break;
        }
      }
    }

    return values;
  }

  public static void setAttributesToLog(List<String> attributeNames) {
    enabledAttributes = P6Util.findEnumMatches(LogAttribute.class, ATTRIBUTE_PREFIX, attributeNames);
  }

}
