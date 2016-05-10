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

import java.lang.reflect.Method;

import com.p6spy.engine.common.Loggable;
import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.proxy.Delegate;
import com.p6spy.engine.spy.Clock;

class P6LogElapsedDelegate implements Delegate {

  private final Loggable loggable;
  private final Category category;

  public P6LogElapsedDelegate(final Loggable loggable, Category category) {
    this.loggable = loggable;
    this.category = category;
  }

  @Override
  public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
    long startTime = Clock.get().getTime();
    try {
      return method.invoke(underlying, args);
    } finally {
      P6LogQuery.logElapsed(loggable.getConnectionId(), startTime, category, loggable);
    }
  }
}
