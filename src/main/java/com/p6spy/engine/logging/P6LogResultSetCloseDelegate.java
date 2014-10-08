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
package com.p6spy.engine.logging;

import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
class P6LogResultSetCloseDelegate implements Delegate {
  private final ResultSetInformation resultSetInformation;

  public P6LogResultSetCloseDelegate(final ResultSetInformation resultSetInformation) {
    this.resultSetInformation = resultSetInformation;
  }

  /**
   * Called by the invocation handler instead of the target method.  Since this method is called
   * instead of the target method, it is up to implementations to invoke the target method (if applicable).
   *
   * @param proxy The object being proxied
   * @param method The method that was invoked
   * @param args   The arguments of the method (if any).  This argument will be null if there
   *               were no arguments.
   * @return The return value of the method
   * @throws Throwable
   */
  @Override
  public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
      
    if ( resultSetInformation.getCurrRow() > -1 ) {
      //  If the result set has not been advanced to the first row, there is nothing to log.
      resultSetInformation.generateLogMessage();
    }
    
    return method.invoke(underlying, args);
    
    // Note: No timing is logged on calls to ResultSet.close()
  }
}
