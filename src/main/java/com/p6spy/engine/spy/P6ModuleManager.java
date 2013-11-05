package com.p6spy.engine.spy;

import java.io.IOException;
import java.util.Collection;
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

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.option.EnvironmentVariables;
import com.p6spy.engine.spy.option.P6OptionChangedListener;
import com.p6spy.engine.spy.option.P6OptionsRepository;
import com.p6spy.engine.spy.option.P6OptionsSource;
import com.p6spy.engine.spy.option.SpyDotProperties;
import com.p6spy.engine.spy.option.SystemProperties;

public class P6ModuleManager {

  // recreated on each reload
  private final P6OptionsSource[] optionsSources = new P6OptionsSource[] {
      new SpyDotProperties(), new EnvironmentVariables(), new SystemProperties() };
  private final Map<Class<? extends P6LoadableOptions>, P6LoadableOptions> allOptions = new HashMap<Class<? extends P6LoadableOptions>, P6LoadableOptions>();
  private final List<P6Factory> factories = new CopyOnWriteArrayList<P6Factory>();
  private final P6MBeansRegistry mBeansRegistry;

  private final P6OptionsRepository optionsRepository = new P6OptionsRepository();

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
      P6LogQuery.initialize();
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

    for (P6OptionsSource optionsSource : instance.optionsSources) {
      optionsSource.preDestroy(instance);
    }

    // unregister mbeans first (to prevent naming conflicts)
    if (instance.mBeansRegistry != null) {
      instance.mBeansRegistry.unregisterMBeans();
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
  private P6ModuleManager() throws IOException, InstanceAlreadyExistsException,
                           MBeanRegistrationException, NotCompliantMBeanException,
                           MalformedObjectNameException {
    debug(this.getClass().getName() + " re/initiating modules started");

    // hard coded - core module init - as it holds initial config
    final P6SpyLoadableOptions spyOptions;
    {
      P6SpyFactory p6SpyFactory = new P6SpyFactory();
      spyOptions = (P6SpyLoadableOptions) p6SpyFactory.getOptions(optionsRepository);

      loadOptions(spyOptions);
    }

    // configured modules init
    {
      Set<P6Factory> toProcessFactories = spyOptions.getModuleFactories();
      for (P6Factory factory : toProcessFactories) {
        P6LoadableOptions options = factory.getOptions(optionsRepository);

        // we initialized for core already => skip now
        if (options instanceof P6SpyLoadableOptions) {
          continue;
        }

        loadOptions(options);
      }
    }

    // make sure the proper listener registration happens
    optionsRepository.registerOptionChangedListener(new P6LogQuery());
    
    optionsRepository.initCompleted();

    // init MBeans
    mBeansRegistry = new P6MBeansRegistry(this.allOptions.values());

    // init factories
    initFactories(spyOptions);

    for (P6OptionsSource optionsSource : optionsSources) {
      optionsSource.postInit(this);
    }

    debug(this.getClass().getName() + " re/initiating modules done");
  }

  /**
   * Returns loaded options. These are loaded in the right order:
   * <ul>
   * <li></li>
   * </ul>
   * 
   * 
   * 
   * @param options
   * @return
   */
  private void loadOptions(final P6LoadableOptions options) {
    // make sure to load defaults first
    options.load(options.getDefaults());

    // load the rest in the right order then
    for (P6OptionsSource optionsSource : optionsSources) {
      Map<String, String> toLoad = optionsSource.getOptions();
      if (null != toLoad) {
        options.load(toLoad);
      }
    }

    // register to all the props then
    allOptions.put(options.getClass(), options);
  }

  public static P6ModuleManager getInstance() {
    return instance;
  }

  private static void handleInitEx(Exception e) {
    e.printStackTrace(System.err);
  }

  private void initFactories(P6SpyLoadableOptions spyOptions) {
    // register the core options file with the reloader
    String className = "no class";
    String classType = "driver";
    try {
      Collection<String> driverNames = spyOptions.getDriverNames();

      // register drivers and wrappers
      classType = "driver";
      if (null != driverNames) {
        for (String driverName : driverNames) {
          // you really only need to load the driver if it is not a type 4 driver!
          P6Util.forName(driverName).newInstance();
        }
      }

      // instantiate the factories, if nec.
      if (!spyOptions.getModuleNames().isEmpty()) {
        classType = "factory";

        for (Iterator<String> i = spyOptions.getModuleNames().iterator(); i.hasNext();) {
          className = i.next();
          P6Factory factory = (P6Factory) P6Util.forName(className).newInstance();
          factories.add(factory);

          debug("Registered factory: " + className + " with options: " + spyOptions);
        }
      }

    } catch (Exception e) {
      String err = "Error registering " + classType + "  [" + className + "]\nCaused By: "
          + e.toString();
      P6LogQuery.error(err);
      throw new P6DriverNotFoundError(err);
    }
  }

  private void debug(String msg) {
    // not initialized yet => nowhere to log yet
    if (instance == null || factories.isEmpty()) {
      return;
    }

    P6LogQuery.debug(msg);
  }

  //
  // API methods
  //

  /**
   * @param optionsClass
   *          the class to get the options for.
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

  public void registerOptionChangedListener(P6OptionChangedListener listener) {
    optionsRepository.registerOptionChangedListener(listener);
  }

  public void unregisterOptionChangedListener(P6OptionChangedListener listener) {
    optionsRepository.unregisterOptionChangedListener(listener);
  }
}
