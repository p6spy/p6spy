
package com.p6spy.engine.logging;

import com.p6spy.engine.common.Loggable;

import java.sql.SQLException;

/**
 * Extends {@link LoggingEventListener} and only logs if an exception has occurred.
 * <p>
 * To activate this class, add <code>com.p6spy.engine.logging.ErrorLoggingEventListener</code> to the file
 * <code>src/main/resources/META-INF/services/com.p6spy.engine.logging.LoggingEventListener</code>.
 */
public class ErrorLoggingEventListener extends LoggingEventListener {
  @Override
  protected void logElapsed(Loggable loggable, long timeElapsedNanos, Category category, SQLException e) {
    if (e != null) {
      super.logElapsed(loggable, timeElapsedNanos, category, e);
    }
  }
}
