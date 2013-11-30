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

import net.sf.cglib.proxy.Enhancer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class ProxyFactory {

  /**
   * @deprecated use {@link #createProxy(Object, GenericInvocationHandler)} instead
   */
  @Deprecated
  public static <T> T createProxy(final T underlying, final Class<T> notUsed, final GenericInvocationHandler<T> invocationHandler) {
    return createProxy(underlying, invocationHandler);
  }

  /**
   * Creates a proxy for the given object delegating all method calls to the invocation handler.  The proxy will
   * implement all interfaces implemented by the object to be proxied.
   *
   * @param underlying        the object to proxy
   * @param invocationHandler the invocation handler
   * @return
   */
  public static <T> T createProxy(final T underlying, final GenericInvocationHandler<T> invocationHandler) {
    //noinspection unchecked
    Enhancer enhancer = new Enhancer();
    enhancer.setCallback(invocationHandler);
    enhancer.setInterfaces(getInterfaces(underlying.getClass()));
    return (T) enhancer.create();
  }

  /**
   * Used to determine is a given object is a Proxy created by this proxy factory.
   *
   * @param obj the object in question
   * @return true if it is a proxy - false otherwise
   */
  public static boolean isProxy(final Object obj) {
    return (obj != null && isProxy(obj.getClass()));
  }

  /**
   * Used to determine if the given class is a proxy class.
   *
   * @param clazz the class in question
   * @return true if proxy - false otherwise
   */
  public static boolean isProxy(final Class<?> clazz) {
    return (clazz != null && P6Proxy.class.isAssignableFrom(clazz));
  }

  private static Class<?>[] getInterfaces(final Class<?> clazz) {
    Set<Class<?>> interfaces = new HashSet<Class<?>>();

    // add all interfaces directly implemented by the given class.
    interfaces.addAll(Arrays.asList(clazz.getInterfaces()));

    // loop through superclasses adding interfaces
    Class<?> superclass = clazz.getSuperclass();
    while (superclass != null && !superclass.equals(Object.class)) {
      interfaces.addAll(Arrays.asList(superclass.getInterfaces()));
      superclass = superclass.getSuperclass();
    }

    // add P6Proxy interface
    interfaces.add(P6Proxy.class);

    return interfaces.toArray(new Class<?>[interfaces.size()]);
  }

}
