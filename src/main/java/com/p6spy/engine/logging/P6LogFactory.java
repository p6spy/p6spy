/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
 *
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
 */

package com.p6spy.engine.logging;

import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.P6OptionsRepository;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class P6LogFactory implements P6Factory {

  private static ServiceLoader<LoggingEventListener> customLoggingEventListener = ServiceLoader
    .load(LoggingEventListener.class, P6LogFactory.class.getClassLoader());

  @Override
  public JdbcEventListener getJdbcEventListener() {
    // return first custom implementaion
    for (Iterator<LoggingEventListener> iterator = customLoggingEventListener.iterator(); iterator.hasNext(); ) {
      try {
        return iterator.next();
      } catch (ServiceConfigurationError e) {
        e.printStackTrace();
      }
    }
    // if none found, return default impl
    return LoggingEventListener.INSTANCE;
  }

  @Override
  public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
    return new P6LogOptions(optionsRepository);
  }

}
