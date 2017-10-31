/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.P6SpyOptions;

/**
 * @author Peter G. Horvath
 * @since 08/2017
 */
public class CustomLineFormat implements MessageFormattingStrategy {

  private static final MessageFormattingStrategy FALLBACK_FORMATTING_STRATEGY = new SingleLineFormat();

  public static final String CONNECTION_ID = "%(connectionId)";
  public static final String CURRENT_TIME = "%(currentTime)";
  public static final String EXECUTION_TIME = "%(executionTime)";
  public static final String CATEGORY = "%(category)";
  public static final String EFFECTIVE_SQL = "%(effectiveSql)";
  public static final String EFFECTIVE_SQL_SINGLELINE = "%(effectiveSqlSingleLine)";
  public static final String SQL = "%(sql)";
  public static final String SQL_SINGLE_LINE = "%(sqlSingleLine)";

  /**
   * Formats a log message for the logging module
   *
   * @param connectionId the id of the connection
   * @param now          the current ime expressing in milliseconds
   * @param elapsed      the time in milliseconds that the operation took to complete
   * @param category     the category of the operation
   * @param prepared     the SQL statement with all bind variables replaced with actual values
   * @param sql          the sql statement executed
   * @return the formatted log message
   */
  @Override
  public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql) {

    String customLogMessageFormat = P6SpyOptions.getActiveInstance().getCustomLogMessageFormat();

    if (customLogMessageFormat == null) {
      // Someone forgot to configure customLogMessageFormat: fall back to built-in
      return FALLBACK_FORMATTING_STRATEGY.formatMessage(connectionId, now, elapsed, category, prepared, sql);
    }

    return customLogMessageFormat
      .replaceAll(Pattern.quote(CONNECTION_ID), Integer.toString(connectionId))
      .replaceAll(Pattern.quote(CURRENT_TIME), now)
      .replaceAll(Pattern.quote(EXECUTION_TIME), Long.toString(elapsed))
      .replaceAll(Pattern.quote(CATEGORY), category)
      .replaceAll(Pattern.quote(EFFECTIVE_SQL), Matcher.quoteReplacement(prepared))
      .replaceAll(Pattern.quote(EFFECTIVE_SQL_SINGLELINE), Matcher.quoteReplacement(P6Util.singleLine(prepared)))
      .replaceAll(Pattern.quote(SQL), Matcher.quoteReplacement(sql))
      .replaceAll(Pattern.quote(SQL_SINGLE_LINE), Matcher.quoteReplacement(P6Util.singleLine(sql)));
  }
}
