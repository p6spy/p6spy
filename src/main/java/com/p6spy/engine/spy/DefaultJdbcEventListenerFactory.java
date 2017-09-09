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
package com.p6spy.engine.spy;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import com.p6spy.engine.event.CompoundJdbcEventListener;
import com.p6spy.engine.event.DefaultEventListener;
import com.p6spy.engine.event.JdbcEventListener;

/**
 * Default {@link JdbcEventListenerFactory} implementation providing all the
 * {@link JdbcEventListener}s supplied by the {@link P6Factory}ies as well as
 * those registered by {@link ServiceLoader}s.
 * 
 * @author Peter Butkovic
 * @since 3.3.0
 *
 */
public class DefaultJdbcEventListenerFactory implements JdbcEventListenerFactory {

  private static ServiceLoader<JdbcEventListener> jdbcEventListenerServiceLoader = //
      ServiceLoader.load(JdbcEventListener.class, DefaultJdbcEventListenerFactory.class.getClassLoader());
  
  private static JdbcEventListener jdbcEventListener;
  
  @Override
  public JdbcEventListener createJdbcEventListener() {
    if (jdbcEventListener == null) {
      synchronized (DefaultJdbcEventListenerFactory.class) {
        if (jdbcEventListener == null) {
          CompoundJdbcEventListener compoundEventListener = new CompoundJdbcEventListener();
          compoundEventListener.addListender(DefaultEventListener.INSTANCE);
          registerEventListenersFromFactories(compoundEventListener);
          registerEventListenersFromServiceLoader(compoundEventListener);
          jdbcEventListener = compoundEventListener;
        }
      }
    }
    
    return jdbcEventListener;
  }
  
  public void clearCache() {
    jdbcEventListener = null;
  }
  
  protected void registerEventListenersFromFactories(CompoundJdbcEventListener compoundEventListener) {
    List<P6Factory> factories = P6ModuleManager.getInstance().getFactories();
    if (factories != null) {
      for (P6Factory factory : factories) {
        final JdbcEventListener eventListener = factory.getJdbcEventListener();
        if (eventListener != null) {
          compoundEventListener.addListender(eventListener);
        }
      }
    }
  }

  protected void registerEventListenersFromServiceLoader(CompoundJdbcEventListener compoundEventListener) {
    for (Iterator<JdbcEventListener> iterator = jdbcEventListenerServiceLoader.iterator(); iterator.hasNext(); ) {
      try {
        compoundEventListener.addListender(iterator.next());
      } catch (ServiceConfigurationError e) {
        e.printStackTrace();
      }
    }
  }
  
}
