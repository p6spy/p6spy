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
package com.p6spy.engine.spy.appender;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public interface MessageFormattingStrategy {
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
  String formatMessage(final String instanceId, int connectionId, String now, long elapsed, String category, String prepared, String sql);

}
