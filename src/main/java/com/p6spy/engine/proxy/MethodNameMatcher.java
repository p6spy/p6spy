/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.p6spy.engine.proxy;

import java.lang.reflect.Method;

/**
 * Used to select methods based on the method name.  If the name ends with * then
 * it will match any method name that starts with that name.  The comparison is case sensitive. 
 * 
 * <p>
 * Example #1:<br/>
 * new MethodNameMatcher("testMethod") would match the following methods.<br/>
 * testMethod()<br/>
 * testMethod(int p1, String p2)<br/>
 * testMethod(int p1, String p2, String p3)<br/>
 *<br/> 
 * However, it would not match the following method.<br/>
 * TestMethod(int p1)<br/>
 * </p>
 *
 * <p>
 * Example #2:<br/>
 * new MethodNameMatcher("testM*") would match the following methods.<br/>
 * testMethod()<br/>
 * testMethodA(int p1, String p2)<br/>
 * testMethodB(int p1, String p2, String p3)<br/>
 *<br/> 
 * However, it would not match the following method.<br/>
 * TestMethod(int p1)<br/>
 * </p>
 * 
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
