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
package com.p6spy.engine.spy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.p6spy.engine.common.P6ModuleManager;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.common.SpyDotPropertiesReloadChangedListener;
import com.p6spy.engine.logging.P6LogFactory;

public class P6SpyOptions implements P6SpyLoadableOptions {

    public static final String DEFAULT_DB_DATEFORMAT = "dd-MMM-yy";
    
    private String modulelist;
    private List<String> moduleNames;
    private Set<P6Factory> moduleFactories;
    
    private String driverlist;
    private List<String> driverNames;

    private boolean usePrefix;
    private boolean autoflush;
    private boolean reloadProperties;
    private long reloadPropertiesInterval;
    private String jndicontextfactory;
    private String jndicontextproviderurl;
    private String jndicontextcustom;
    private String realdatasource;
    private String realdatasourceclass;
    private String realdatasourceproperties;
    private String databaseDialectDateFormat;
    
    // reloadproperties propagation
    List<SpyDotPropertiesReloadChangedListener> reloadChangeListeners = new ArrayList<SpyDotPropertiesReloadChangedListener>();
    
    @Override
    public void load(Properties properties) {
      setModulelist(properties.getProperty("modulelist"));
      setDriverlist(properties.getProperty("driverlist"));
      setUsePrefix(properties.getProperty("usePrefix"));
      setAutoflush(properties.getProperty("autoflush"));
      setReloadProperties(properties.getProperty("reloadproperties"));
      setReloadPropertiesInterval(properties.getProperty("reloadpropertiesinterval"));
      setJNDIContextFactory(properties.getProperty("jndicontextfactory"));
      setJNDIContextProviderURL(properties.getProperty("jndicontextproviderurl"));
      setJNDIContextCustom(properties.getProperty("jndicontextcustom"));
      setRealDataSource(properties.getProperty("realdatasource"));
      setRealDataSourceClass(properties.getProperty("realdatasourceclass"));
      setRealDataSourceProperties(properties.getProperty("realdatasourceproperties"));
      setDatabaseDialectDateFormat(properties.getProperty("databaseDialectDateFormat"));
    }
    
    /**
     * Utility method, to make accessing options from app less verbose.
     * 
     * @return active instance of the {@link P6SpyLoadableOptions}
     */
    public static P6SpyLoadableOptions getActiveInstance() {
      return P6ModuleManager.getInstance().getOptions(P6SpyOptions.class);
    }

    // auto reload listeners
    
    @Override
    public void registerSpyDotPropertiesReloadChangedListener(SpyDotPropertiesReloadChangedListener reloadChangeListener) {
      if (null == reloadChangeListener) {
        return;
      }
      this.reloadChangeListeners.add(reloadChangeListener);
    }
    
    @Override
    public void unregisterSpyDotPropertiesReloadChangedListener(SpyDotPropertiesReloadChangedListener reloadChangeListener) {
      if (null == reloadChangeListener) {
        return;
      }
      this.reloadChangeListeners.remove(reloadChangeListener);
    }
    
    private void fireSpyDotPropertiesReloadChanged(boolean isEnabled) {
      for (SpyDotPropertiesReloadChangedListener reloadChangeListener : reloadChangeListeners) {
        reloadChangeListener.setAutoReload(isEnabled, this.getReloadPropertiesInterval());
      }
    }
    
    private void fireSpyDotPropertiesReloadInternalChanged(long secs) {
      for (SpyDotPropertiesReloadChangedListener reloadChangeListener : reloadChangeListeners) {
        reloadChangeListener.setAutoReload(this.getReloadProperties(), secs);
      }
    }
    
    // JMX exporsed API
    
    @Override
    public void reload() {
      P6ModuleManager.getInstance().reload();
    }
    
    @Override
    public Set<P6Factory> getModuleFactories() {
      return moduleFactories;
    }
    
    @Override
    public void setUsePrefix(String usePrefix) {
      setUsePrefix(P6Util.isTrue(usePrefix, false));
    }
    
    @Override
    public void setUsePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    @Override
    public boolean getUsePrefix() {
        return usePrefix;
    }

    @Override
    public void setAutoflush(String autoflush) {
      setAutoflush(P6Util.isTrue(autoflush, false));
    }
    
    @Override
    public void setAutoflush(boolean autoflush) {
      this.autoflush = autoflush;
    }

    @Override
    public boolean getAutoflush() {
        return autoflush;
    }

    @Override
    public String getDriverlist() {
      return driverlist;
    }

    @Override
    public void setDriverlist(final String driverlist) {
      this.driverlist = driverlist;
      
      driverNames = new ArrayList<String>();
      if( driverlist != null && !driverlist.trim().isEmpty() ) {
          driverNames = Arrays.asList(driverlist.split(","));
      }
    }

    @Override
    public boolean getReloadProperties() {
        return reloadProperties;
    }
    @Override
    public void setReloadProperties(String reloadproperties) {
      setReloadProperties(P6Util.isTrue(reloadproperties, false));
    }
    
    @Override
    public void setReloadProperties(boolean reloadproperties) {
      this.reloadProperties = reloadproperties;
      fireSpyDotPropertiesReloadChanged(reloadproperties);
    }
    
    @Override
    public long getReloadPropertiesInterval() {
        return reloadPropertiesInterval;
    }
    @Override
    public void setReloadPropertiesInterval(String reloadpropertiesinterval) {
      setReloadPropertiesInterval(P6Util.parseLong(reloadpropertiesinterval, -1l));
    }
    
    @Override
    public void setReloadPropertiesInterval(long reloadpropertiesinterval) {
      this.reloadPropertiesInterval = reloadpropertiesinterval;
//      this.reloadMs = reloadPropertiesInterval * 1000l;
      fireSpyDotPropertiesReloadInternalChanged(reloadpropertiesinterval);
    }
    
    @Override
    public void setJNDIContextFactory(String jndicontextfactory) {
      this.jndicontextfactory = jndicontextfactory;
    }
    @Override
    public String getJNDIContextFactory() {
        return jndicontextfactory;
    }
    @Override
    public void setJNDIContextProviderURL(String jndicontextproviderurl) {
      this.jndicontextproviderurl = jndicontextproviderurl;
    }
    @Override
    public String getJNDIContextProviderURL() {
        return jndicontextproviderurl;
    }
    @Override
    public void setJNDIContextCustom(String jndicontextcustom) {
      this.jndicontextcustom = jndicontextcustom;
    }
    @Override
    public String getJNDIContextCustom() {
        return jndicontextcustom;
    }
    @Override
    public void setRealDataSource(String realdatasource) {
      this.realdatasource = realdatasource;
    }
    @Override
    public String getRealDataSource() {
        return realdatasource;
    }
    @Override
    public void setRealDataSourceClass(String realdatasourceclass) {
      this.realdatasourceclass = realdatasourceclass;
    }
    @Override
    public String getRealDataSourceClass() {
        return realdatasourceclass;
    }
    @Override
    public void setRealDataSourceProperties(String realdatasourceproperties) {
      this.realdatasourceproperties = realdatasourceproperties;
    }
    @Override
    public String getRealDataSourceProperties() {
        return realdatasourceproperties;
    }

    @Override
    public List<String> getDriverNames() {
        return driverNames;
    }

    /**
     * Returns the databaseDialectDateFormat.
     *
     * @return String
     */
    @Override
    public String getDatabaseDialectDateFormat() {
        return databaseDialectDateFormat;
    }

    /**
     * Sets the databaseDialectDateFormat.
     *
     * @param databaseDialectDateFormat The databaseDialectDateFormat to set
     */
    @Override
    public void setDatabaseDialectDateFormat(String databaseDialectDateFormat) {
      this.databaseDialectDateFormat = databaseDialectDateFormat;
        if (databaseDialectDateFormat == null || databaseDialectDateFormat.length() == 0) {
          this.databaseDialectDateFormat = DEFAULT_DB_DATEFORMAT;
        }
    }


    @Override
    public String getModulelist() {
      return this.modulelist;
    }

    @Override
    public void setModulelist(String modulelist) {
      this.modulelist = modulelist;
      
      // set moduleNames
      {
        moduleNames = new ArrayList<String>();
        if( modulelist != null && !modulelist.trim().isEmpty() ) {
          moduleNames = new ArrayList<String>(Arrays.asList(modulelist.split(",")));
        } 

        // core module is a must
        if (!moduleNames.contains(P6SpyFactory.class.getName())) {
          moduleNames.add(P6SpyFactory.class.getName());
        }
        
        // log module is a must as well
        if (!moduleNames.contains(P6LogFactory.class.getName())) {
          moduleNames.add(P6LogFactory.class.getName());
        }
      }
      
      // set moduleFactories
      {
        moduleFactories = new HashSet<P6Factory>();
        for (String moduleName : moduleNames) {
          try {
            if (null == moduleName || moduleName.trim().isEmpty()) {
              continue;
            }
            
            moduleFactories.add((P6Factory) Class.forName(moduleName).newInstance());
          } catch (InstantiationException e) {
            System.err.println("Cannot instantiate module factory: " + moduleName + ". " + e);
          } catch (IllegalAccessException e) {
            System.err.println("Cannot instantiate module factory: " + moduleName + ". " + e);
          } catch (ClassNotFoundException e) {
            System.err.println("Cannot instantiate module factory: " + moduleName + ". " + e);
          }
        }
      }
    }

    @Override
    public List<String> getModuleNames() {
      return moduleNames;
    }

}
