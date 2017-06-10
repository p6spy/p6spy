
package com.p6spy.engine.spy.appender;

import com.p6spy.engine.logging.Category;

import java.io.PrintStream;

public class StdoutLogger extends FormattedLogger {

  protected PrintStream getStream() {
    return System.out;
  }

  @Override
  public void logException(Exception e) {
    e.printStackTrace(getStream());
  }

  @Override
  public void logText(String text) {
    getStream().println(text);
  }

  @Override
  public boolean isCategoryEnabled(Category category) {
    // no restrictions on logger side
    return true;
  }
}

