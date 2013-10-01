package com.p6spy.engine.spy;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public class P6SpyOptionsImpl implements P6SpyLoadableOptions {
  
  private boolean isReloadProperties;
  private int reloadpropertiesinterval;
  private Set<String> realdrivers;
  
  {
    defaults.put("reloadproperties", "false");
    defaults.put("reloadpropertiesinterval", "60");
  }

  @Override
  public void load(Properties props) {
  }

  @Override
  public Properties getDefaultPropeties() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<P6Factory> getModuleFactories() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isReloadproperties() {
    return isReloadProperties;
  }

  @Override
  public long getReloadInterval() {
    return reloadpropertiesinterval;
  }
  
  
}
