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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import com.p6spy.engine.spy.P6DriverNotFoundError;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6SpyFactory;
import com.p6spy.engine.spy.P6SpyLoadableOptions;

public class P6ModuleManager {

  // recreated on each reload
  private final SpyDotProperties spyDotProperties = new SpyDotProperties();
  private final SpyDotPropertiesReloader reloader;
  private final P6SpyLoadableOptions spyOptions;
  private final Map<Class<? extends P6LoadableOptions>, P6LoadableOptions> allOptions = new HashMap<Class<? extends P6LoadableOptions>, P6LoadableOptions>();
  private final List<P6Factory> factories = new CopyOnWriteArrayList<P6Factory>();
  private final P6MBeansRegistry mBeansRegistry;
  
  // singleton
  private static P6ModuleManager instance;

  static {
    initMe();
  }

  /**
   * 
   * @param spyPropertiesJMX
   *          manually (via JMX) set properties to be kept across auto-reloads.
   */
  private synchronized static void initMe() {
    try {
      cleanUp();
      
      instance = new P6ModuleManager();
    } catch (IOException e) {
      handleInitEx(e);
    } catch (MBeanRegistrationException e) {
      handleInitEx(e);
    } catch (InstanceNotFoundException e) {
      handleInitEx(e);
    } catch (MalformedObjectNameException e) {
      handleInitEx(e);
    } catch (InstanceAlreadyExistsException e) {
      handleInitEx(e);
    } catch (NotCompliantMBeanException e) {
      handleInitEx(e);
    }
  }

  private static void cleanUp() throws MBeanRegistrationException, InstanceNotFoundException,
      MalformedObjectNameException {
    if (instance == null) {
      return;
    }

    // unregister mbeans first (to prevent naming conflicts)
    if (instance.mBeansRegistry != null) {
      instance.mBeansRegistry.unregisterMBeans();      
    }

    // kill reloader
    if (instance.reloader != null) {
      instance.reloader.kill(instance.spyOptions);
    }
  }

  /**
   * Used on the class load only (only once!)
   * 
   * @throws IOException
   * @throws NotCompliantMBeanException 
   * @throws MBeanRegistrationException 
   * @throws InstanceAlreadyExistsException 
   * @throws MalformedObjectNameException 
   */
  private P6ModuleManager() throws IOException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
    debug(this.getClass().getName() + " re/initiating modules started");

    // no spy,properties config file found
    if (null == spyDotProperties.getProperties()) {
      // TODO refactor once spy.properties won't be mandatory
      spyOptions = null;
      reloader = null;
      mBeansRegistry = null;
      return;
    }
    
    // hard coded - core module init - as it holds initial config
    {
      P6SpyFactory p6SpyFactory = new P6SpyFactory();
      spyOptions = (P6SpyLoadableOptions) p6SpyFactory.getOptions();
      
      // JMX ones have higher prio, make sure to load them later => overwrite
      spyOptions.load(spyDotProperties.getProperties());
      allOptions.put(spyOptions.getClass(), spyOptions);
    }

    // configured modules init
    {
      Set<P6Factory> toProcessFactories = spyOptions.getModuleFactories();
      for (P6Factory factory : toProcessFactories) {
        P6LoadableOptions options = factory.getOptions();

        // we initialized for core already => skip now
        if (options instanceof P6SpyLoadableOptions) {
          continue;
        }

        options.load(spyDotProperties.getProperties());
        allOptions.put(options.getClass(), options);
      }
    }

    // init MBeans
    mBeansRegistry = new P6MBeansRegistry(this.allOptions.values());
    
    // reloader init
    reloader = new SpyDotPropertiesReloader(spyDotProperties, spyOptions);
    
    // init factories
    initFactories();
    
    debug(this.getClass().getName() + " re/initiating modules done");
  }

  public static P6ModuleManager getInstance() {
    return instance;
  }

  private static void handleInitEx(Exception e) {
      e.printStackTrace(System.err);
  }

  private void initFactories() {
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

          debug("Registered factory: " + className + " with options: " + spyOptions);
        }
      }

    } catch (Exception e) {
      String err = "Error registering " + classType + "  [" + className + "]\nCaused By: " + e.toString();
      P6LogQuery.error(err);
      throw new P6DriverNotFoundError(err);
    }
  }


  private void debug(String msg) {
    // not initialized yet => nowhere to log yet
    if (instance == null || instance.spyOptions == null) {
      return;
    }

    P6LogQuery.debug(msg);
  }

  //
  // API methods
  //
  
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
