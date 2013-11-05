package com.p6spy.engine.proxy;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class MethodNameAndParameterLikeMatcherTest {

  @Test
  public void testParameterMatches() throws Exception {
    assertTrue("should have matched", new MethodNameAndParameterLikeMatcher("setA", String.class)
        .matches(TestInterface2.class.getMethod("setA", String.class, String.class)));

    assertTrue("should have matched", new MethodNameAndParameterLikeMatcher("setA", String.class)
        .matches(TestInterface2.class.getMethod("setA", String.class)));

    assertFalse("should not have matched", new MethodNameAndParameterLikeMatcher("setA", String.class, String.class)
        .matches(TestInterface2.class.getMethod("setA", String.class)));


  }

  interface TestInterface {
    void setA(String a);
    void setB(String b);
    void clear();
  }
  interface TestInterface2 extends TestInterface {
    void setA(String a, String x);
    void setB(String b, String x);
  }

}
