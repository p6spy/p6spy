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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.p6spy.engine.spy.P6DriverNotFoundError;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6SpyFactory;
import com.p6spy.engine.spy.P6SpyLoadableOptions;

public class P6ModuleManager {

  // recreated on each reload
  private final P6SpyLoadableOptions spyOptions = (P6SpyLoadableOptions) new P6SpyFactory()
      .getOptions();
  private final Map<Class<? extends P6LoadableOptions>, P6LoadableOptions> allOptions = new HashMap<Class<? extends P6LoadableOptions>, P6LoadableOptions>();
  private final SpyDotProperties spyDotProperties = new SpyDotProperties();
  private final ScheduledExecutorService reloader;
  private final List<P6Factory> factories = new CopyOnWriteArrayList<P6Factory>();
  
  private static P6ModuleManager instance;

  static {
    initMe();
  }

  /**
   * 
   * @param spyPropertiesJMX
   *          manually (via JMX) set properties to be kept across auto-reloads.
   */
  /* package protected for testability */ synchronized static void initMe() {
    try {
      instance = new P6ModuleManager();
    } catch (IOException e) {
      handleInitEx(e);
    }
  }

  public static P6ModuleManager getInstance() {
    return instance;
  }

  private static void handleInitEx(IOException e) {
    // TODO report this problem the same as in the legacy impl
    // as we're in serious trouble here
    e.printStackTrace();
  }

  /**
   * Used on the class load only (only once!)
   * 
   * @throws IOException
   */
  private P6ModuleManager() throws IOException {
//    P6LogQuery.debug(this.getClass().getName() + " re/initiating modules started");
    
    List<P6Factory> factoriesToRegister = new ArrayList<P6Factory>();
    
    // hard coded - core module init - as it holds initial config
    {
      // JMX ones have higher prio, make sure to load them later => overwrite
      spyOptions.load(spyDotProperties.getProperties());
    }

    // configured modules init
    {
      Set<P6Factory> toBeProcessedFactories = spyOptions.getModuleFactories();
      if (null != toBeProcessedFactories) {
        for (P6Factory factory : toBeProcessedFactories) {
          P6LoadableOptions options = factory.getOptions();
          // we initialized for core already => skip now
          if (!(options instanceof P6SpyLoadableOptions)) {
            options.load(spyDotProperties.getProperties());
          }

          allOptions.put(options.getClass(), options);
          factoriesToRegister.add(factory);
        }
      }
    }

    // reloader init
    // TODO refactor to separate class?
    {
      if (spyOptions.getReloadProperties()) {
        long reloadInterval = spyOptions.getReloadPropertiesInterval();
        reloader = Executors.newSingleThreadScheduledExecutor();
        final Runnable reader = new Runnable() {
          @Override
          public void run() {
            if (spyDotProperties.isModified()) {
              // correctly stop the old reloader first
              reloader.shutdownNow();
              initMe();
            }
          }
        };

        reloader.scheduleAtFixedRate(reader, reloadInterval, reloadInterval, TimeUnit.SECONDS);
      } else {
        reloader = null;
      }
    }
    
    // factories init
    initFactories(factoriesToRegister);
    
//    P6LogQuery.debug(this.getClass().getName() + " re/initiating modules done");
  }

  private void initFactories(List<P6Factory> factoriesToRegister) {
    // register the core options file with the reloader
    String className = "no class";
    String classType = "driver";
    try {
      List<String> driverNames = spyOptions.getDriverNames();


      // register drivers and wrappers
      classType = "driver";
      for (String driverName : driverNames) {
        // you really only need to load the driver if it is not a type 4 driver!
        P6Util.forName(driverName).newInstance();

      }

      // instantiate the factories, if nec.
      if (!spyOptions.getModuleNames().isEmpty()) {
        classType = "factory";

      for (Iterator<String> i = spyOptions.getModuleNames().iterator(); i.hasNext(); ) {
          className = i.next();
          P6Factory factory = (P6Factory) P6Util.forName(className).newInstance();
          factories.add(factory);

//          P6LogQuery.debug("Registered factory: " + className + " with options: " + spyOptions);
        }
      }

    } catch (Exception e) {
      String err = "Error registering " + classType + "  [" + className + "]\nCaused By: " + e.toString();
      P6LogQuery.error(err);
      throw new P6DriverNotFoundError(err);
    }

  }

  
  // API methods
  
  /**
   * @param optionsClass the class to get the options for.
   * @return the options instance depending on it's class.
   */
  @SuppressWarnings("unchecked")
  public <T extends P6LoadableOptions> T getOptions(Class<T> optionsClass) {
    return (T) allOptions.get(optionsClass);
  }

  /**
   * Reloads the {@link P6ModuleManager}. <br/>
   * <br/>
   * The idea is that whoever initiates this one causes it to start with the clean table. No
   * previously set values are kept (even those set manually - via jmx will be forgotten).
   */
  public void reload() {
    initMe();
  }

  public List<P6Factory> getFactories() {
    return factories;
  }
}
