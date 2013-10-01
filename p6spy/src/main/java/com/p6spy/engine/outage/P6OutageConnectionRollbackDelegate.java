/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.p6spy.engine.outage;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

/**
 */
class P6OutageConnectionRollbackDelegate implements Delegate {
  private final ConnectionInformation connectionInformation;

  public P6OutageConnectionRollbackDelegate(final ConnectionInformation connectionInformation) {
    this.connectionInformation = connectionInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();
    if (P6OutageOptions.getOutageDetection()) {
      P6OutageDetector.getInstance().registerInvocation(this, startTime, "rollback", "", "");
    }

    try {
      return method.invoke(target, args);
    } finally {
      if (P6OutageOptions.getOutageDetection()) {
          P6OutageDetector.getInstance().unregisterInvocation(this);
      }
    }
  }
}
