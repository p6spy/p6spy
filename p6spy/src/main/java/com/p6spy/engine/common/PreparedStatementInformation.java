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

import com.p6spy.engine.spy.P6SpyOptions;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class PreparedStatementInformation extends StatementInformation {
  private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
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
        if( parameterValues.get(currentParameter) == null) {
          sb.append("NULL");
        } else {
          sb.append(parameterValues.get(currentParameter));
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
  public void setParameterValue(final int position, final Object value) {
    parameterValues.set(position-1,convertToString(value));
  }

  private String convertToString(Object o) {
    String result;
    if (o instanceof java.util.Date) {
      result = new SimpleDateFormat(P6SpyOptions.getActiveInstance().getDatabaseDialectDateFormat()).format(o);
    } else if (o instanceof byte[]) {
      result = toHexString((byte[]) o);
    } else {
      result =  (o == null) ? null : o.toString();
    }

    return quoteIfNeeded(result, o);
  }
  
  private String quoteIfNeeded(String stringValue, Object obj) {
    if(stringValue == null) {
      return null;
    }
    
    /*
        The following types do not get quoted: numeric, boolean
        
        It is tempting to use ParameterMetaData.getParameterType() for
        this purpose as it would be safer.  However, this method will fail
        with some JDBC drivers.
        
        Oracle: 
          Not supported until ojdbc7 which was released with Oracle 12c.
          https://forums.oracle.com/thread/2584886
                 
        MySQL:
          The method call only works if service side prepared statements
          are enabled.  The URL parameter 'useServerPrepStmts=true' enables.
     */

    boolean shouldQuote = true;
    if( Number.class.isAssignableFrom(obj.getClass()) ||
        Boolean.class.isAssignableFrom(obj.getClass()) ) {
      shouldQuote = false;
    } 
    
    if( shouldQuote ) {
      return "'" + stringValue + "'";
    } else {
      return stringValue;
    }
  }

  private String toHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      int temp = (int) b & 0xFF;
      sb.append(HEX_CHARS[temp / 16]);
      sb.append(HEX_CHARS[temp % 16]);
    }
    return sb.toString();
  }

}
