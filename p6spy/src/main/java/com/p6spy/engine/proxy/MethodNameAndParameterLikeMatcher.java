/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.p6spy.engine.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class MethodNameAndParameterLikeMatcher extends MethodNameMatcher {
  private final Class[] methodParameters;

  public MethodNameAndParameterLikeMatcher(final String methodName, final Class... methodParameters) {
    super(methodName);
    this.methodParameters = methodParameters;
  }

  @Override
  public boolean matches(final Method method) {

    if (!super.matches(method)) {
      return false;
    }

    if (methodParameters.length > method.getParameterTypes().length) {
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

    MethodNameAndParameterLikeMatcher that = (MethodNameAndParameterLikeMatcher) o;

    return Arrays.equals(methodParameters, that.methodParameters);

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Arrays.hashCode(methodParameters);
    return result;
  }
}
