package com.p6spy.engine.common;

import org.junit.Assert;
import org.junit.Test;

public class NanosecondsStrategyTest {

  @Test
  public void testConvert() {
    TimeUnitStrategy strategy = new NanosecondsStrategy();
    Assert.assertEquals(123L, strategy.convert(123L));
  }

}
