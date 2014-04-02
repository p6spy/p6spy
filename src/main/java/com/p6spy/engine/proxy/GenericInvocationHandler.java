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

import com.p6spy.engine.common.P6WrapperIsWrapperDelegate;
import com.p6spy.engine.common.P6WrapperUnwrapDelegate;
import com.p6spy.engine.proxy.cache.Cache;
import com.p6spy.engine.proxy.cache.CacheFactory;
import com.p6spy.engine.proxy.cache.MethodMatcherCacheKey;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for invocation handlers.  This class is designed to be a generic implementation
 * of the {@link InvocationHandler} interface which delegates the invocation of {@link Delegate} objects
 * based on pattern matching against the method.
 *
 * @param <T> The class of the object which will be proxied
 */
public class GenericInvocationHandler<T> implements InvocationHandler {
  private final Map<MethodMatcher, Delegate> delegateMap;
  
  private final T underlying;
  
  final static Cache<MethodMatcherCacheKey, MethodMatcher> cache = CacheFactory
      .<MethodMatcherCacheKey, MethodMatcher> newCache();

  /**
   * Creates a new invocation handler for the given object.
   *
   * @param underlying The object being proxied
   */
  public GenericInvocationHandler(T underlying) {
    this.underlying = underlying;
    this.delegateMap = new HashMap<MethodMatcher, Delegate>();
    addDelegatesForWrapperInterface();
  }

  private void addDelegatesForWrapperInterface() {
    // This covers the implementation of the java.sql.Wrapper interface
    delegateMap.put(new MethodNameMatcher("isWrapperFor"), new P6WrapperIsWrapperDelegate());
    delegateMap.put(new MethodNameMatcher("unwrap"), new P6WrapperUnwrapDelegate());
  }

  /**
   * Adds a delegate which will be used when a method is invoked that meets the following criteria:
   * <p>
   * <code>methodMatcher.match(method) == true</code>
   * </p>
   * Note: Adding a second delegate object with the same method matcher (according to {@link MethodMatcher#equals(Object)}
   * and {@link com.p6spy.engine.proxy.MethodMatcher#hashCode()} will replace the previous delegate!
   *
   * @param methodMatcher The method matcher
   * @param delegate      The delegate object
   */
  public void addDelegate(MethodMatcher methodMatcher, Delegate delegate) {
    delegateMap.put(methodMatcher, delegate);
  }

  Delegate getDelegate(MethodMatcher methodMatcher) {
    // This is package scope to allow test code to invoke the method
    return delegateMap.get(methodMatcher);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    MethodMatcher methodMatcher = cache.get(new MethodMatcherCacheKey(getClass(), method));
    
    if (null == methodMatcher) {
      for (MethodMatcher matcher : delegateMap.keySet()) {
        if (matcher.matches(method)) {
          methodMatcher = matcher;
          cache.put(new MethodMatcherCacheKey(this.getClass(), method), methodMatcher);
          break;
        }
      }
    }
      
    try {
      if (null != methodMatcher) {
        final Delegate delegate = delegateMap.get(methodMatcher);
        return delegate.invoke(proxy, underlying, method, args);
      }
          
      return method.invoke(underlying, args);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }

  protected T getUnderlying() {
    return underlying;
  }

  public static void clearCache() {
    cache.clear();
  }

}
