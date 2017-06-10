
package com.p6spy.engine.spy.appender;

import com.p6spy.engine.logging.Category;

/**
 * {@link P6Logger} implementation providing support for pluggable {@link MessageFormattingStrategy}.
 */
public abstract class FormattedLogger implements P6Logger {

  protected MessageFormattingStrategy strategy;

  protected FormattedLogger() {
    strategy = new SingleLineFormat();
  }

  @Override
  public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql) {
    logText(strategy.formatMessage(connectionId, now, elapsed, category.toString(), prepared, sql));
  }

  /**
   * Sets the strategy implementation to use for formatting log message.  If not set, this will default to {@link SingleLineFormat}
   */
  public void setStrategy(final MessageFormattingStrategy strategy) {
    this.strategy = strategy;
  }
  
}
