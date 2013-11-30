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
 * Implementation of {@link java.sql.Wrapper#unwrap(Class)} for proxy classes.
 */
public class P6WrapperUnwrapDelegate implements Delegate {
  @Override
  public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
    Class<?> iface = (Class<?>) args[0];
    Object result = null;

    // if the proxy directly implements the interface or extends it, return the proxy
    if( iface.isAssignableFrom(proxy.getClass()) ) {
      result = proxy;
    }

    // if the proxied object directly implements the interface or extends it, return
    // the proxied object
    else if (iface.isAssignableFrom(underlying.getClass())) {
      result = underlying;
    }

    // if the proxied object implements the wrapper interface, then
    // return the result of it's unwrap method.
    else if (Wrapper.class.isAssignableFrom(underlying.getClass())) {
      result = ((Wrapper) underlying).unwrap(iface);
    }

    else {
      /*
         This line of code can only be reached when the underlying object does not implement the wrapper
         interface.  This would mean that either the JDBC driver or the wrapper of the underlying object
         does not implement the JDBC 4.0 API.
       */
      throw new SQLException("Can not unwrap to "+iface.getName());
    }

    return iface.cast(result);
  }
}
