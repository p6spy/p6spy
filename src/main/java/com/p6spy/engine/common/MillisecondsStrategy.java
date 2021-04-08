package com.p6spy.engine.common;

import java.util.concurrent.TimeUnit;

/**
 * The {@code MillisecondsStrategy} class implements a strategy that converts a
 * duration in nanoseconds to milliseconds.
 */
public class MillisecondsStrategy implements TimeUnitStrategy {

  @Override
  public long convert(long durationInNanoseconds) {
    return TimeUnit.NANOSECONDS.toMillis(durationInNanoseconds);
  }

}
