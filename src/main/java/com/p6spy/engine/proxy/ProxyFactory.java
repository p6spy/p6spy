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

import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.proxy.Enhancer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory for creating proxies
 * 
 * @author Quinton McCombs
 * @since 09/2013
 */
public class ProxyFactory {

  /**
   * Creates a proxy for the given object delegating all method calls to the invocation handler.  The proxy will
   * implement all interfaces implemented by the object to be proxied.
   *
   * @param underlying        the object to proxy
   * @param invocationHandler the invocation handler
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T createProxy(final T underlying, final GenericInvocationHandler<T> invocationHandler) {
    // TODO refactor - to get rid of endless try - catch
    final ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      final Enhancer enhancer = createProxy(underlying, invocationHandler, ProxyNamingPolicy.INSTANCE, currentThreadClassLoader);
      return (T) enhancer.create();
    } catch (CodeGenerationException e) {
      // fallback, as proxied class might implement non-public interfaces 
      // that have trouble with our ProxyNamingPolicy (for example for SQLite)
      try {
        final Enhancer enhancer = createProxy(underlying, invocationHandler, null, currentThreadClassLoader);
        return (T) enhancer.create();
      } catch (CodeGenerationException ex) {
        // 
        // well, wildfly (8.1.CR1) just doesn't like currentThreadClassLoader => needs to use the default one
        //
        try {
          final Enhancer enhancer = createProxy(underlying, invocationHandler, ProxyNamingPolicy.INSTANCE, null);
          return (T) enhancer.create();
        } catch (CodeGenerationException exc) {
          // fallback, as proxied class might implement non-public interfaces 
          // that have trouble with our ProxyNamingPolicy (for example for SQLite)
            final Enhancer enhancer = createProxy(underlying, invocationHandler, null, null);
            return (T) enhancer.create();
        }
      }
    }
  }

  /**
   * Creates a proxy for the given object delegating all method calls to the invocation handler.  The proxy will
   * implement all interfaces implemented by the object to be proxied.
   *
   * @param underlying        the object to proxy
   * @param invocationHandler the invocation handler
   * @param namingPolicy      the naming policy
   * @param classLoader       the class loader to be used
   * @return
   */
  private static <T> Enhancer createProxy(final T underlying,
                                           final GenericInvocationHandler<T> invocationHandler, 
                                           final NamingPolicy namingPolicy, final ClassLoader classLoader) {
    // noinspection unchecked
    final Enhancer enhancer = new Enhancer();
    enhancer.setCallback(invocationHandler);
    enhancer.setInterfaces(getInterfaces(underlying.getClass()));
    // fix for the https://github.com/p6spy/p6spy/issues/188
    if (null != classLoader) {
      enhancer.setClassLoader(classLoader);
    }
    if (null != namingPolicy) {
      enhancer.setNamingPolicy(namingPolicy);  
    }
    return enhancer;
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

    // loop through superclasses adding interfaces
    Class<?> examinedClass = clazz;
    while (examinedClass != null && !examinedClass.equals(Object.class)) {
      for( Class<?> intf : examinedClass.getInterfaces() ) {
        if( !intf.getName().startsWith("org.jboss") ) {
        /*
            When P6Spy is added to a connection wrapped by an application server there could be interfaces
            which can not be loaded by the current classloader.  This affects JBoss 7+ and likely other
            app servers as well.

            Note: The intent is to implement all of the interfaces supplied by the JDBC driver to provide
            easy access to vendor specific methods without having to unwrap the proxy.  This behavior will
            likely be removed in P6Spy 3 (where it will only implement the standard JDBC interfaces).
         */
          interfaces.add(intf);
        }
      }
      examinedClass = examinedClass.getSuperclass();
    }

    // add P6Proxy interface
    interfaces.add(P6Proxy.class);

    return interfaces.toArray(new Class<?>[interfaces.size()]);
  }

}
