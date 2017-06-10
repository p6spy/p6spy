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

package com.p6spy.engine.spy.option;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.p6spy.engine.spy.P6ModuleManager;
import com.p6spy.engine.spy.P6SpyLoadableOptions;
import com.p6spy.engine.spy.P6SpyOptions;

public class SpyDotPropertiesReloader implements P6OptionChangedListener {

  private ScheduledExecutorService reloader;
  private final SpyDotProperties spyDotProperties;
  private boolean killed = false;

  public SpyDotPropertiesReloader(SpyDotProperties spyDotProperties,
                                  P6ModuleManager p6ModuleManager) {
    this.spyDotProperties = spyDotProperties;
    
    final P6SpyLoadableOptions spyOptions = p6ModuleManager.getOptions(P6SpyOptions.class);
    reschedule(spyOptions.getReloadProperties(), spyOptions.getReloadPropertiesInterval());
    
    p6ModuleManager.registerOptionChangedListener(this);
  }

  public synchronized void reschedule(final boolean enabled, final long reloadInterval) {
    shutdownNow();
    
    if (!enabled || killed) {
      return;
    } 

    reloader = Executors.newSingleThreadScheduledExecutor();
    final Runnable reader = new Runnable() {
      @Override
      public void run() {
        if (spyDotProperties.isModified()) {
          // correctly stop the old reloader first
          shutdownNow();
  
          P6ModuleManager.getInstance().reload();
        }
      }
    };

    reloader.scheduleAtFixedRate(reader, reloadInterval, reloadInterval, TimeUnit.SECONDS);
    
    // seems someone killed in the meantime
    if (killed) {
      shutdownNow();
    }
  }
  
  public void kill(P6ModuleManager p6ModuleManager) {
    p6ModuleManager.unregisterOptionChangedListener(this);
    killed = true;
    shutdownNow();
  }
  
  private void shutdownNow() {
    if (wasEnabled()) {
      reloader.shutdownNow();
      reloader = null;
    }  
  }
  
  private boolean wasEnabled() {
    return reloader != null;
  }

  @Override
  public void optionChanged(String key, Object oldValue, Object newValue) {
    if (key.equals(P6SpyOptions.RELOADPROPERTIES)) {
      reschedule(Boolean.valueOf(newValue.toString()), P6SpyOptions.getActiveInstance().getReloadPropertiesInterval());
    } else if (key.equals(P6SpyOptions.RELOADPROPERTIESINTERVAL)) {
      reschedule(P6SpyOptions.getActiveInstance().getReloadProperties(), (Long) newValue);
    }
  }

}
