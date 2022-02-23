package com.p6spy.engine.common;

import org.junit.Assert;
import org.junit.Test;

public class MillisecondsStrategyTest {

  @Test
  public void testConvert() {
    TimeUnitStrategy strategy = new MillisecondsStrategy();
    Assert.assertEquals(123L, strategy.convert(123456789L));
  }

}
