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
package com.p6spy.engine.spy.appender;

import com.p6spy.engine.common.P6Util;

import java.util.Map;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class SingleLineFormat implements MessageFormattingStrategy {

  /**
   * Formats a log message for the logging module
   *
   * @param connectionId the id of the connection
   * @param now          the current ime expressing in milliseconds
   * @param elapsed      the time in milliseconds that the operation took to complete
   * @param category     the category of the operation
   * @param prepared     the SQL statement with all bind variables replaced with actual values
   * @param sql          the sql statement executed
   * @param url          the database url where the sql statement executed
   * @param attributes   the additional attributes requested for logging
   * @return the formatted log message
   */
  @Override
  public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql, final String url, Map<String, String> attributes) {
    return new StringBuilder().append(now).append("|").append(elapsed).append("|").append(category).append("|connection ").append(connectionId).append("|url ").append(url).append("|").append(P6Util.singleLine(prepared)).append("|").append(P6Util.singleLine(sql)).toString();
  }
}
