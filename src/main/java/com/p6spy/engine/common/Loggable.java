
package com.p6spy.engine.common;

/**
 * Assures capability of the class to be logged by {@link P6LogQuery}.
 *
 * @author Peter Butkovic
 */
public interface Loggable {

  /**
   * @return Original {@code SQL}.
   */
  String getSql();

  /**
   * @return The {@code SQL} having '?' replaced with real values used.
   */
  String getSqlWithValues();

  /**
   * @return A connection id which is unique for each {@link java.sql.Connection}
   */
  int getConnectionId();

}
