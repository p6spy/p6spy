package com.p6spy.engine.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6SpyFactory;
import com.p6spy.engine.spy.P6SpyLoadableOptions;

public class P6ModuleManager {
    
  // recreated on each reload
  private final P6SpyLoadableOptions spyOptions = (P6SpyLoadableOptions) new P6SpyFactory().getOptions();
  private final Map<Class<? extends P6LoadableOptions>, P6LoadableOptions> allOptions = new HashMap<Class<? extends P6LoadableOptions>, P6LoadableOptions>();
  private final SpyDotProperties spyDotProperties = new SpyDotProperties();
  private final ScheduledExecutorService reloader;

  // to be kept across reloads
  private final SpyPropertiesJMX spyPropertiesJMX;
  
  private static P6ModuleManager instance;

  static {
    initMe(null);
  }
  
   /**
    *  
    * @param spyPropertiesJMX manually (via JMX) set properties to be kept across auto-reloads.
    */
  /* package protected for testability */ static void initMe(SpyPropertiesJMX spyPropertiesJMX) {
    try {
      instance = new P6ModuleManager(spyPropertiesJMX);
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
   * @throws IOException 
   */
  private P6ModuleManager(SpyPropertiesJMX spyPropertiesJMX) throws IOException {
    // keep manually (via JMX) set properties across auto-reloads
    {
      if (null == spyPropertiesJMX) {
        this.spyPropertiesJMX = new SpyPropertiesJMX();
      } else {
        this.spyPropertiesJMX = spyPropertiesJMX;
      }
    }
    
    // hard coded - core module init - as it holds initial config
    {
      spyOptions.load(spyDotProperties.getProperties());
      allOptions.put(spyOptions.getClass(),spyOptions);
    }
    
    // configured modules init
    {
      Set<P6Factory> factories = spyOptions.getModuleFactories();
      if (null != factories) {
        for (P6Factory factory : factories) {
          P6LoadableOptions options = factory.getOptions();
          options.load(spyDotProperties.getProperties());
          allOptions.put(options.getClass(), options);
        }
      }
    }
    
    // reloader init
    {
      if (spyOptions.isReloadProperties()) {
        long reloadInterval = spyOptions.getReloadInterval();
        reloader = Executors.newSingleThreadScheduledExecutor();
        final Runnable reader = new Runnable() {
          @Override
          public void run() {
            if (spyDotProperties.isModified()) {
              // correctly stop the old reloader first
              reloader.shutdownNow();
              
              // preserve manually (via JMX) set properties across auto-reloads 
              initMe(getSpyPropertiesJMX());  
            }
          }
        };
        
        reloader.scheduleAtFixedRate(reader, reloadInterval, reloadInterval, TimeUnit.SECONDS);
      } else {
        reloader = null;
      }
    }
  }

  // API methods
  public P6LoadableOptions getOptions(Class<? extends P6LoadableOptions> optionsClass) {
    return allOptions.get(optionsClass);
  }
  
  /**
   * Returns {@link SpyPropertiesJMX} instance that is to be used for setting manually set properties (via JMX).
   * @return
   */
  public SpyPropertiesJMX getSpyPropertiesJMX() {
    return spyPropertiesJMX;
  }
  
  /**
   * Reloads the {@link P6ModuleManager}.
   * <br/><br/>
   * The idea is that whoever initiates this one causes it to start with the clean table.
   * No previously set values are kept (even those set manually - via jmx will be forgotten). 
   */
  public void reload() {
    initMe(null); 
  }
}
