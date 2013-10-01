package com.p6spy.engine.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SpyPropertiesJMX {

  private final Map<String, String> properties = new HashMap<String, String>();
  
  public SpyPropertiesJMX() {
  }
  
  public void setProperty(String propName, String propValue) {
    properties.put(propName, propValue);
  }
  
  public Map<String, String> getProperties() {
    // prevent outside modifications 
    return Collections.unmodifiableMap(properties);
  }
  
}
