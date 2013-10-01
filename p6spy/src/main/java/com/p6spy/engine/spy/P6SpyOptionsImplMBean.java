package com.p6spy.engine.spy;

import java.util.Set;

public interface P6SpyOptionsImplMBean {
  
  public Set<P6Factory> getModuleFactories();
  public boolean isReloadProperties();
  public long getReloadInterval();
  // non-jdbc4 drivers
  public Set<String> getDriversList();
  public boolean isDeregisterDrivers();
  public boolean isAutoflush();
  
// once clarified: add/remove
//realdatasource
//realdatasourceclass
//realdatasourceproperties
//jndicontextfactory
//jndicontextproviderurl
//jndicontextproviderurl
//jndicontextcustom
//jndicontextfactory
//jndicontextproviderurl

}
