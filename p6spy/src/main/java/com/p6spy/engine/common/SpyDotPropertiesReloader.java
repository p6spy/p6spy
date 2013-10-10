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
package com.p6spy.engine.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.p6spy.engine.spy.P6SpyLoadableOptions;

public class SpyDotPropertiesReloader implements SpyDotPropertiesReloadChangedListener {

  private ScheduledExecutorService reloader;
  private final SpyDotProperties spyDotProperties;
  private boolean isKilled = false;

  public SpyDotPropertiesReloader(SpyDotProperties spyDotProperties,
                                  P6SpyLoadableOptions spyOptions) {
    this.spyDotProperties = spyDotProperties;
    reschedule(spyOptions.getReloadProperties(), spyOptions.getReloadPropertiesInterval());
    spyOptions.registerSpyDotPropertiesReloadChangedListener(this);
  }

  public synchronized void reschedule(final boolean isEnabled, final long reloadInterval) {
    shutdownNow();
    
    if (!isEnabled || isKilled) {
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
    if (isKilled) {
      shutdownNow();
    }
  }
  
  public void kill(P6SpyLoadableOptions spyOptions) {
    isKilled = true;
    spyOptions.unregisterSpyDotPropertiesReloadChangedListener(this);
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
  public void setAutoReload(boolean isEnabled, long secs) {
    reschedule(isEnabled, secs);
  }

}
