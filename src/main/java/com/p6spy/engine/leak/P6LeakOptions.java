package com.p6spy.engine.leak;

import java.util.Map;

import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6LeakOptions implements P6LeakLoadableOptions {

  public P6LeakOptions(final P6OptionsRepository optionsRepository) {
  }
  
  @Override
  public Map getOpenObjects() {
    return P6Objects.getOpenObjects();
  }

  @Override
  public void load(Map<String, String> options) {
    // no options in the module => nothing to reload here    
  }

  @Override
  public Map<String, String> getDefaults() {
    // no options in the module => no defaults
    return null;
  }
}
