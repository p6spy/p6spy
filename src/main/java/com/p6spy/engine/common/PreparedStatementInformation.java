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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.p6spy.engine.spy.P6SpyOptions;

/**
 * Stores information about the prepared statement and bind variables.
 *
 * @author Quinton McCombs
 * @since 09/2013
 */
public class PreparedStatementInformation extends StatementInformation implements Loggable {
  private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
  private final Map<Integer, Object> parameterValues = new HashMap<Integer, Object>();

  public PreparedStatementInformation(final ConnectionInformation connectionInformation, String query) {
    super(connectionInformation);
    setStatementQuery(query);
  }

  /**
   * Generates the query for the prepared statement with all parameter placeholders
   * replaced with the actual parameter values
   *
   * @return the SQL
   */
  @Override
  public String getSqlWithValues() {
    final StringBuilder sb = new StringBuilder();
    final String statementQuery = getStatementQuery();

    // iterate over the characters in the query replacing the parameter placeholders
    // with the actual values
    int currentParameter = 0;
    for( int pos = 0; pos < statementQuery.length(); pos ++) {
      char character = statementQuery.charAt(pos);
      if( statementQuery.charAt(pos) == '?' && currentParameter <= parameterValues.size()) {
        // replace with parameter value
        sb.append(convertToString(parameterValues.get(currentParameter)));
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
    parameterValues.put(position - 1, value);
  }

  protected Map<Integer, Object> getParameterValues() {
    return parameterValues;
  }

  protected String convertToString(Object value) {
    String result;
    if( value == null ) {
      result = "NULL";
    } else {

      if (value instanceof java.util.Date) {
        result = new SimpleDateFormat(P6SpyOptions.getActiveInstance().getDatabaseDialectDateFormat()).format(value);
      } else if (value instanceof byte[]) {
        result = toHexString((byte[]) value);
      } else {
        result = value.toString();
      }

      result = quoteIfNeeded(result, value);
    }

    return result;
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
    if( Number.class.isAssignableFrom(obj.getClass()) ||
        Boolean.class.isAssignableFrom(obj.getClass()) ) {
      return stringValue;
    } else {
      return "'" + escape(stringValue) + "'";
    }
  }

  /**
   * Escapes special characters in SQL values. Currently is only {@code '} escaped with {@code ''}.
   * 
   * @param stringValue
   *          value to escape
   * @return escaped value.
   */
  private String escape(String stringValue) {
    return stringValue.replaceAll("'", "''");
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
