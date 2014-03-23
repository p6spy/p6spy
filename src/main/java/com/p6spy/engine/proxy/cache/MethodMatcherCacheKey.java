/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2014 P6Spy
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
package com.p6spy.engine.proxy.cache;

import java.lang.reflect.Method;

import com.p6spy.engine.proxy.GenericInvocationHandler;

/**
 * @author Peter Butkovic
 */
public class MethodMatcherCacheKey {
  
  private final Method method;

  @SuppressWarnings("rawtypes")
  private final Class<? extends GenericInvocationHandler> invHandlerClass;
  
  public MethodMatcherCacheKey(@SuppressWarnings("rawtypes")
  Class<? extends GenericInvocationHandler> invHandlerClass, Method method) {
    super();
    this.method = method;
    this.invHandlerClass = invHandlerClass;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((invHandlerClass == null) ? 0 : invHandlerClass.hashCode());
    result = prime * result + ((method == null) ? 0 : method.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MethodMatcherCacheKey other = (MethodMatcherCacheKey) obj;
    if (invHandlerClass == null) {
      if (other.invHandlerClass != null)
        return false;
    } else if (!invHandlerClass.equals(other.invHandlerClass))
      return false;
    if (method == null) {
      if (other.method != null)
        return false;
    } else if (!method.equals(other.method))
      return false;
    return true;
  }
}
