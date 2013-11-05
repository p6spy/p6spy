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
