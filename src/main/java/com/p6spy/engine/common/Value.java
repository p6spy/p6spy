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

import com.p6spy.engine.logging.P6LogLoadableOptions;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.logging.format.BinaryFormat;
import com.p6spy.engine.spy.P6SpyOptions;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Value holder of the data passed to DB as well as of those retrieved capable
 * of binary data logging depending on the configuration property
 * {@code excludebinary}.
 *
 * @author Peter Butkovic
 *
 */
public class Value {

  /**
   * Value itself.
   */
  private Object value;

  public Value(Object valueToSet) {
    this();
    this.value = valueToSet;
  }

  public Value() {
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return convertToString(this.value);
  }

  /**
   * Returns the {@link String} representation of the given value depending on
   * the value type. Formats:
   * <ul>
   * <li>{@link java.util.Date} values it in a way configured via configuration
   * property: {@code dateformat},</li>
   * <li>{@code byte[]} values are converted to {@link String} representation using the configured
   * database dialect {@link BinaryFormat}, unless configuration property {@code exclidebinary=true} is
   * set.</li>
   * <li>for other types string representation is simply returned.</li>
   * </ul>
   *
   * @param value
   * @return
   */
  public String convertToString(Object value) {
    String result;
    
    if (value == null) {
      result = "NULL";
    } else {

      if (value instanceof byte[]) {
        // P6LogFactory may not be registered
        P6LogLoadableOptions logOptions = P6LogOptions.getActiveInstance();
        if (logOptions != null && logOptions.getExcludebinary()) {
          result = "[binary]";
        } else {
          BinaryFormat binaryFormat = P6SpyOptions.getActiveInstance().getDatabaseDialectBinaryFormatInstance();
          
          // return early because BinaryFormat#toString wraps the value in quotes if needed
          return binaryFormat.toString((byte[]) value);
        }
        
        // we should not do ((Blob) value).getBinaryStream(). ...
        // as inputstream might not be re-rea
//      } else  if (value instanceof Blob) {
//        P6LogLoadableOptions logOptions = P6LogOptions.getActiveInstance();
//        if (logOptions != null && logOptions.getExcludebinary()) {
//          result = "[binary]";
//        } else {
//          result = value.toString();
//        }
      } else if (value instanceof Timestamp) {
        result = new SimpleDateFormat(P6SpyOptions.getActiveInstance().getDatabaseDialectTimestampFormat()).format(value);
      } else if (value instanceof Date) {
        result = new SimpleDateFormat(P6SpyOptions.getActiveInstance().getDatabaseDialectDateFormat()).format(value);
      } else if (value instanceof Boolean) {
        if ("numeric".equals(P6SpyOptions.getActiveInstance().getDatabaseDialectBooleanFormat())) {
          result = Boolean.FALSE.equals(value) ? "0" : "1";
        } else {
          result = value.toString();
        }
      } else {
        result = value.toString();
      }

      result = quoteIfNeeded(result, value);
    }

    return result;
  }

  /**
   * Qoutes the passed {@code stringValue} if it's needed.
   * 
   * @param stringValue
   * @param obj
   * @return
   */
  private String quoteIfNeeded(String stringValue, Object obj) {
    if (stringValue == null) {
      return null;
    }

    /*
     * The following types do not get quoted: numeric, boolean.
     * Binary data is quoted only if the supplied binaryFormat requires that.
     * 
     * It is tempting to use ParameterMetaData.getParameterType() for this
     * purpose as it would be safer. However, this method will fail with some
     * JDBC drivers.
     * 
     * Oracle: Not supported until ojdbc7 which was released with Oracle 12c.
     * https://forums.oracle.com/thread/2584886
     * 
     * MySQL: The method call only works if service side prepared statements are
     * enabled. The URL parameter 'useServerPrepStmts=true' enables.
     */
    if (Number.class.isAssignableFrom(obj.getClass()) || Boolean.class.isAssignableFrom(obj.getClass())) {
      return stringValue;
    } else {
      return "'" + escape(stringValue) + "'";
    }
  }

  /**
   * Escapes special characters in SQL values. Currently is only {@code '}
   * escaped with {@code ''}.
   * 
   * @param stringValue
   *          value to escape
   * @return escaped value.
   */
  private String escape(String stringValue) {
    return stringValue.replaceAll("'", "''");
  }

}
