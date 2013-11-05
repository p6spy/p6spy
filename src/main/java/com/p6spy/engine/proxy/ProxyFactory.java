package com.p6spy.engine.proxy;

import net.sf.cglib.proxy.Proxy;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class ProxyFactory {

  public static <T> T createProxy(final T underlying, final Class<T> interfaceClass, final GenericInvocationHandler<T> invocationHandler) {
    //noinspection unchecked
    return (T) Proxy.newProxyInstance(
        underlying.getClass().getClassLoader(),
        new Class[]{interfaceClass},
        invocationHandler);
  }

}
