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

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class PreparedStatementInformation extends StatementInformation {
  private final List<String> parameterValues;
  private final int parameterCount;
  private final ParameterMetaData parameterMetaData;

  public PreparedStatementInformation(final ConnectionInformation connectionInformation,ParameterMetaData parameterMetaData)
      throws SQLException {
    super(connectionInformation);
    this.parameterMetaData = parameterMetaData;
    this.parameterCount = parameterMetaData.getParameterCount();
    this.parameterValues = new ArrayList<String>(parameterMetaData.getParameterCount());

    // pre-populate parameter values list with nulls to allow for the values to be set later by index
    for( int i = 0; i < parameterCount; i++) {
      parameterValues.add(null);
    }

  }

  int getParameterCount() {
    return parameterCount;
  }

  /**
   * Generates the query for the prepared statement with all parameter placeholders
   * replaced with the actual parameter values
   *
   * @return the SQL
   * @throws java.sql.SQLException
   */
  @Override
  public String getPreparedStatementQuery() throws SQLException {
    StringBuilder sb = new StringBuilder();

    String statementQuery = getStatementQuery();

    // iterate over the characters in the query replacing the parameter placeholders
    // with the actual values
    int currentParameter = 0;
    for( int pos = 0; pos < statementQuery.length(); pos ++) {
      char character = statementQuery.charAt(pos);
      if( statementQuery.charAt(pos) == '?' && currentParameter < getParameterCount()) {
        // replace with parameter value
        boolean shouldQuote = true;
        switch( parameterMetaData.getParameterType(currentParameter+1)) {
          case Types.BIT:
          case Types.TINYINT:
          case Types.SMALLINT:
          case Types.INTEGER:
          case Types.BIGINT:
          case Types.FLOAT:
          case Types.REAL:
          case Types.DOUBLE:
          case Types.NUMERIC:
          case Types.DECIMAL:
          case Types.BOOLEAN:
            shouldQuote = false;
        }
        if( parameterValues.get(currentParameter) == null) {
          sb.append("NULL");
        } else {
          if( shouldQuote ) {
            sb.append("'");
          }
          sb.append(parameterValues.get(currentParameter));
          if( shouldQuote ) {
            sb.append("'");
          }
        }
        currentParameter++;
      } else {
        sb.append(character);
      }
    }

    return sb.toString();
  }

  /**
   * Records the value of a parameter.
   * @param position the position of the parameter (starts with 1 not 0)
   * @param value the value of the parameter
   */
  public void setParameterValue(final int position, final String value) {
    parameterValues.set(position-1,value);
  }

}
