package com.p6spy.engine.proxy;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class MethodNameMatcherTest {

  @Test
  public void testExactMatch() throws Exception {

    MethodNameMatcher matcher = new MethodNameMatcher("setA");
    assertTrue("should have matched", matcher.matches(TestInterface.class.getMethod("setA", String.class)));
    assertFalse("should not have matched", matcher.matches(TestInterface.class.getMethod("setB", String.class)));

  }

  @Test
  public void testWildcardMatch() throws Exception {

    MethodNameMatcher matcher = new MethodNameMatcher("set*");
    assertTrue("should have matched", matcher.matches(TestInterface.class.getMethod("setA", String.class)));
    assertTrue("should have matched", matcher.matches(TestInterface.class.getMethod("setB", String.class)));
    assertFalse("should not have matched", matcher.matches(TestInterface.class.getMethod("clear")));

  }

  @Test(expected = IllegalArgumentException.class)
  public void testWildcardNotAtEndOfMethodName() throws Exception {

    new MethodNameMatcher("s*A");

  }

  interface TestInterface {
    void setA(String a);
    void setB(String b);
    void clear();
  }
}
