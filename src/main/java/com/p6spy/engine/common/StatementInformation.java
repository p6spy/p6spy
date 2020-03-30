/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2020 P6Spy
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

import java.sql.Statement;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class StatementInformation implements Loggable {

  private final ConnectionInformation connectionInformation;
  private Statement statement;
  private String statementQuery;
  private long totalTimeElapsed;

  public StatementInformation(final ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  public Statement getStatement() {
    return statement;
  }

  public void setStatement(Statement statement) {
    this.statement = statement;
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

}
