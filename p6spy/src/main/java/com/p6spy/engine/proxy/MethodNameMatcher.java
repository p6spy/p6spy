package com.p6spy.engine.proxy;

import java.lang.reflect.Method;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class MethodNameMatcher implements MethodMatcher {
  private final String methodName;
  private final boolean startsWith;

  public MethodNameMatcher(final String methodName) {
    if( methodName.endsWith("*") ) {
      this.startsWith = true;
      this.methodName = methodName.substring(0, methodName.length() - 1);
    } else {
      this.startsWith = false;
      this.methodName = methodName;
    }

    // wildcard is only allowed at end of method name
    if( this.methodName.contains("*")) {
      throw new IllegalArgumentException("Wildcard is only allowed at end of method name");
    }
  }

  @Override
  public boolean matches(final Method method) {
    boolean result;
    if( startsWith ) {
      result = method.getName().startsWith(methodName);
    } else {
      result = method.getName().equals(methodName);
    }
    return result;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof MethodNameMatcher)) return false;

    MethodNameMatcher that = (MethodNameMatcher) o;

    if (startsWith != that.startsWith) return false;
    if (!methodName.equals(that.methodName)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = methodName.hashCode();
    result = 31 * result + (startsWith ? 1 : 0);
    return result;
  }
}
