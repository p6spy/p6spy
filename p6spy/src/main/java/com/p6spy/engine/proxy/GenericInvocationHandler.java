package com.p6spy.engine.proxy;

import java.lang.reflect.InvocationHandler;
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

  /**
   * Creates a new invocation handler for the given object.
   *
   * @param underlying  The object being proxied
   */
  public GenericInvocationHandler(T underlying) {
    this.underlying = underlying;
    this.delegateMap = new HashMap<MethodMatcher, Delegate>();
  }

  /**
   * Adds a delegate which will be used when a method is invoked that meets the following criteria:
   * <p>
   *   <code>methodMatcher.match(method) == true</code>
   * </p>
   * Note: Adding a second delegate object with the same method matcher (according to {@link MethodMatcher#equals(Object)}
   * and {@link com.p6spy.engine.proxy.MethodMatcher#hashCode()} will replace the previous delegate!
   *
   * @param methodMatcher The method matcher
   * @param delegate The delegate object
   */
  public void addDelegate(MethodMatcher methodMatcher, Delegate delegate) {
    delegateMap.put(methodMatcher, delegate);
  }

  Delegate getDelegate(MethodMatcher methodMatcher) {
    // This is package scope to allow test code to invoke the method
    return delegateMap.get(methodMatcher);
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    for (MethodMatcher methodMatcher : delegateMap.keySet()) {
      if (methodMatcher.matches(method)) {
        return delegateMap.get(methodMatcher).invoke(underlying, method, args);
      }
    }
    
    return method.invoke(underlying, args);
  }

  protected T getUnderlying() {
    return underlying;
  }

}
