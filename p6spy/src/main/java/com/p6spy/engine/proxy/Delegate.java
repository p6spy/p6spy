package com.p6spy.engine.proxy;

import java.lang.reflect.Method;

/**
 * Interface to be implemented by all classes which can be used as a delegate by an
 * invocation handler.  This is equivalent to AOP style 'around advise'.
 *
 * @see GenericInvocationHandler
 */
public interface Delegate {

  /**
   * Called by the invocation handler instead of the target method.  Since this method is called
   * instead of the target method, it is up to implementations to invoke the target method (if applicable).
   *
   *
   * @param target The object being proxied
   * @param method The method that was invoked
   * @param args The arguments of the method (if any).  This argument will be null if there
   *             were no arguments.
   * @return The return value of the method
   * @throws Throwable
   */
  Object invoke(Object target, Method method, Object[] args) throws Throwable;
}
