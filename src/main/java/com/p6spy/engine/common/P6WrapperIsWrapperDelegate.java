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
package com.p6spy.engine.common;

import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * Implementation of {@link java.sql.Wrapper#isWrapperFor(Class)} for proxy classes.
 */
public class P6WrapperIsWrapperDelegate implements Delegate {
  @Override
  public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
    Class<?> iface = (Class<?>) args[0];
    boolean result = false;

    // if the proxy directly proxy the interface or extends it, return true
    if( iface.isAssignableFrom(proxy.getClass()) ) {
      result = true;
    }

    // if the proxied object directly implements the interface or extends it, return true
    else if (iface.isAssignableFrom(underlying.getClass())) {
      result = true;
    }

    // if the proxied object implements the wrapper interface, then
    // return the result of it's isWrapperFor method.
    else if (Wrapper.class.isAssignableFrom(underlying.getClass())) {
      result = ((Wrapper) underlying).isWrapperFor(iface);
    }

    return result;
  }
}
