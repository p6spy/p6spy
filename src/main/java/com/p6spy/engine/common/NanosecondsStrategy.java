package com.p6spy.engine.common;

/**
 * The {@code NanosecondsStrategy} class represents implements a strategy that
 * returns a duration in nanoseconds.
 */
public class NanosecondsStrategy implements TimeUnitStrategy {

  @Override
  public long convert(long durationInNanoseconds) {
    return durationInNanoseconds;
  }

}
