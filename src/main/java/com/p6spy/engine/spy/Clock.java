/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2016 P6Spy
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
package com.p6spy.engine.spy;

import java.util.concurrent.TimeUnit;

public enum Clock {

  MILLIS {
    @Override
    public long getTime() {
      return System.currentTimeMillis();
    }

    @Override
    public long fromMillisToClockGranularity(long millis) {
      return millis;
    }
  },
  NANOS {
    @Override
    public long getTime() {
      return System.nanoTime();
    }

    @Override
    public long fromMillisToClockGranularity(long millis) {
      return TimeUnit.MILLISECONDS.toNanos(millis);
    }
  };

  public abstract long getTime();

  public abstract long fromMillisToClockGranularity(long millis);

  public static Clock get() {
    return P6ModuleManager.getInstance().getOptions(P6SpyOptions.class).getUseNanoTime() ? NANOS : MILLIS;
  }
}
