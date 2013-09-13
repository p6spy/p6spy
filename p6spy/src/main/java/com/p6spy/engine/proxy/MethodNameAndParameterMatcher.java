package com.p6spy.engine.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class MethodNameAndParameterMatcher extends MethodNameMatcher {
  private final Class[] methodParameters;

  public MethodNameAndParameterMatcher(final String methodName, final Class... methodParameters) {
    super(methodName);
    this.methodParameters = methodParameters;
  }

  @Override
  public boolean matches(final Method method) {

    if (!super.matches(method)) {
      return false;
    }

    if (methodParameters.length != method.getParameterTypes().length) {
      return false;
    }

    for (int i = 0; i <methodParameters.length; i++) {
      if (!methodParameters[i].equals(method.getParameterTypes()[i])) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    MethodNameAndParameterMatcher that = (MethodNameAndParameterMatcher) o;

    return Arrays.equals(methodParameters, that.methodParameters);

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Arrays.hashCode(methodParameters);
    return result;
  }
}
