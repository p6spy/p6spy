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

// log4j, stdout, and file logger all use the same format
// so we descend from this class.
public abstract class FormattedLogger {
  private MessageFormattingStrategy strategy;

  protected FormattedLogger() {
    strategy = new SingleLineFormat();
  }

  public void logSQL(int connectionId, String now, long elapsed, String category, String prepared, String sql) {
    logText(strategy.formatMessage(connectionId, now, elapsed, category, prepared, sql));
  }

  public abstract void logText(String text);

  /**
   * Sets the strategy implementation to use for formatting log message.  If not set, this will default to {@link SingleLineFormat}
   */
  public void setStrategy(final MessageFormattingStrategy strategy) {
    this.strategy = strategy;
  }
}
