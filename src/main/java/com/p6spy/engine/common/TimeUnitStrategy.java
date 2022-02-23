package com.p6spy.engine.common;

/**
 * The {@code TimeUnitStrategy} interface represents a strategy that decides
 * which time unit to use.
 */
public interface TimeUnitStrategy {

  /**
   * Converts duration in nanoseconds to another time unit according to this
   * strategy.
   * 
   * @param durationInNanoseconds duration in nanoseconds
   * @return the converted duration
   */
  long convert(long durationInNanoseconds);

}
