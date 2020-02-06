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

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.spy.P6SpyOptions;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter G. Horvath
 * @since 08/2017
 */
public class CustomLineFormat implements MessageFormattingStrategy {

  private static final MessageFormattingStrategy FALLBACK_FORMATTING_STRATEGY = new SingleLineFormat();

  private static String lastProcessedFormat = null;
  private static final AtomicBoolean formatParsingInProgress = new AtomicBoolean(false);

  private static boolean isUsed_EFFECTIVE_SQL_SINGLELINE = false;
  private static boolean isUsed_SQL_SINGLE_LINE = false;
  private static boolean isUsed_ATTRIBUTES = false;

  private static List<String> usedAttributesPlaceholders = Collections.emptyList();

  private static final String TAG_PREFIX = "%(";
  private static final String TAG_SUFFIX = ")";

  private static final String TAG_CONNECTION_ID = "connectionId";
  private static final String TAG_CURRENT_TIME = "currentTime";
  private static final String TAG_EXECUTION_TIME = "executionTime";
  private static final String TAG_CATEGORY = "category";
  private static final String TAG_EFFECTIVE_SQL = "effectiveSql";
  private static final String TAG_EFFECTIVE_SQL_SINGLELINE = "effectiveSqlSingleLine";
  private static final String TAG_SQL = "sql";
  private static final String TAG_SQL_SINGLE_LINE = "sqlSingleLine";
  private static final String TAG_URL = "url";

  private static final String ATTRIBUTES_PREFIX = "attributes:";
  private static final String ATTRIBUTES_REGEX   = ATTRIBUTES_PREFIX + "[* .,a-zA-Z]+";
  private static final Pattern ATTRIBUTES_PATTERN = Pattern.compile(Pattern.quote(TAG_PREFIX) + ATTRIBUTES_REGEX + Pattern.quote(TAG_SUFFIX));

  public static final String CONNECTION_ID            = TAG_PREFIX + TAG_CONNECTION_ID + TAG_SUFFIX;
  public static final String CURRENT_TIME             = TAG_PREFIX + TAG_CURRENT_TIME + TAG_SUFFIX;
  public static final String EXECUTION_TIME           = TAG_PREFIX + TAG_EXECUTION_TIME + TAG_SUFFIX;
  public static final String CATEGORY                 = TAG_PREFIX + TAG_CATEGORY + TAG_SUFFIX;
  public static final String EFFECTIVE_SQL            = TAG_PREFIX + TAG_EFFECTIVE_SQL + TAG_SUFFIX;
  public static final String EFFECTIVE_SQL_SINGLELINE = TAG_PREFIX + TAG_EFFECTIVE_SQL_SINGLELINE + TAG_SUFFIX;
  public static final String SQL                      = TAG_PREFIX + TAG_SQL + TAG_SUFFIX;
  public static final String SQL_SINGLE_LINE          = TAG_PREFIX + TAG_SQL_SINGLE_LINE + TAG_SUFFIX;
  public static final String URL                      = TAG_PREFIX + TAG_URL + TAG_SUFFIX;

  private static final Pattern AllPlaceholdersPattern = Pattern.compile(Pattern.quote(TAG_PREFIX) +
    "(" +
      TAG_CONNECTION_ID + "|" +
      TAG_CURRENT_TIME + "|" +
      TAG_EXECUTION_TIME + "|" +
      TAG_CATEGORY + "|" +
      TAG_EFFECTIVE_SQL + "|" +
      TAG_EFFECTIVE_SQL_SINGLELINE + "|" +
      TAG_SQL + "|" +
      TAG_SQL_SINGLE_LINE + "|" +
      TAG_URL + "|" +
      ATTRIBUTES_REGEX +
    ")" +
    Pattern.quote(TAG_SUFFIX));

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
  public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql, final String url,
                              final Map<String, String> attributes) {

    String customLogMessageFormat = P6SpyOptions.getActiveInstance().getCustomLogMessageFormat();

    if (customLogMessageFormat == null) {
      // Someone forgot to configure customLogMessageFormat: fall back to built-in
      return FALLBACK_FORMATTING_STRATEGY.formatMessage(connectionId, now, elapsed, category, prepared, sql, url, attributes);
    }

    // If the log format has changed, make sure that only one thread parses the new style.
    // We can safely compare pointers to the format String, because it is cached in the active instance until it changes.
    if (lastProcessedFormat != customLogMessageFormat && formatParsingInProgress.compareAndSet(false, true)) {
      try {
        parseNewFormat(customLogMessageFormat);
      } finally {
        lastProcessedFormat = customLogMessageFormat;
        formatParsingInProgress.set(false);
      }
    }

    Map<String, String> replacements = new HashMap<String, String>() {{
      put(CONNECTION_ID, Integer.toString(connectionId));
      put(CURRENT_TIME, now);
      put(EXECUTION_TIME, Long.toString(elapsed));
      put(CATEGORY, category);
      put(EFFECTIVE_SQL, Matcher.quoteReplacement(prepared));
      put(SQL, Matcher.quoteReplacement(sql));
      put(URL, url);

      if (isUsed_EFFECTIVE_SQL_SINGLELINE) {
        put(EFFECTIVE_SQL_SINGLELINE, Matcher.quoteReplacement(P6Util.singleLine(prepared)));
      }

      if (isUsed_SQL_SINGLE_LINE) {
        put(SQL_SINGLE_LINE, Matcher.quoteReplacement(P6Util.singleLine(sql)));
      }

      if (isUsed_ATTRIBUTES && ! usedAttributesPlaceholders.isEmpty()) {
        // We're only expanding the first placeholder for the Attributes and the rest will be printed empty
        put(usedAttributesPlaceholders.get(0), attributes != null ? attributes.toString() : "");
      }
    }};

    StringBuffer output = new StringBuffer();
    try {
      Matcher m = AllPlaceholdersPattern.matcher(lastProcessedFormat);

      while (m.find()) {
        String replacement = replacements.get(m.group());
        m.appendReplacement(output, replacement != null ? replacement : "");
      }
      m.appendTail(output);
    } catch (Exception ex) {
      // Make sure that any exceptions from the Matcher are not breaking the main application
System.out.println("=== Got Exception : " + Arrays.toString(ex.getStackTrace()));
    }

    return output.toString();
  }

  private static void parseNewFormat(String customLogMessageFormat) {
    // Turn off any previously used Attribute Placeholders:
    usedAttributesPlaceholders = Collections.emptyList();

    // Set flags to indicate the presence of Attributes that require additional logic to get their values:
    isUsed_EFFECTIVE_SQL_SINGLELINE = customLogMessageFormat.contains(EFFECTIVE_SQL_SINGLELINE);
    isUsed_SQL_SINGLE_LINE          = customLogMessageFormat.contains(SQL_SINGLE_LINE);
    isUsed_ATTRIBUTES               = customLogMessageFormat.contains(TAG_PREFIX + ATTRIBUTES_PREFIX);

    if (isUsed_ATTRIBUTES) {
      List<String> customAttributes = new ArrayList<String>();
      List<String> customAttributePlaceholders = new ArrayList<String>();

      try {
        Matcher m = ATTRIBUTES_PATTERN.matcher(customLogMessageFormat);
        while (m.find()) {
          String requestedAttributes = m.group();
          customAttributePlaceholders.add(requestedAttributes);

          // Extract the individual attributes from the placeholder:
          requestedAttributes = requestedAttributes.substring(requestedAttributes.indexOf(":") + 1);
          requestedAttributes = requestedAttributes.substring(0, requestedAttributes.length() - 1).trim();
          customAttributes.addAll(Arrays.asList(requestedAttributes.split(" *,+ *")));
        }
      } catch (RuntimeException ignored) {
        // In case the Mather throws any RuntimeExceptions, we must ignore it and not interrupt the main application
System.out.println("=== Got Exception : " + Arrays.toString(ignored.getStackTrace()));
      }
System.out.println("=== parsed customAttributes : " + customAttributes.toString());

      ConnectionInformation.setAttributesToLog(customAttributes);
      StatementInformation.setAttributesToLog(customAttributes);
      ResultSetInformation.setAttributesToLog(customAttributes);

      usedAttributesPlaceholders = customAttributePlaceholders;
    }
  }
}
