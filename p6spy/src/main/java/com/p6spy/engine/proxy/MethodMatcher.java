package com.p6spy.engine.proxy;

import java.lang.reflect.Method;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public interface MethodMatcher {

  boolean matches(final Method method);

}
