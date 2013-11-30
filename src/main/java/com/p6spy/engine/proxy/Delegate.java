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
   * @param proxy The proxy
   * @param underlying The proxied object
   * @param method The method that was invoked
   * @param args The arguments of the method (if any).  This argument will be null if there
   *             were no arguments.
   * @return The return value of the method
   * @throws Throwable
   */
  Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable;
}
