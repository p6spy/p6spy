package com.p6spy.engine.spy.option;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.p6spy.engine.spy.P6ModuleManager;

public class EnvironmentVariables implements P6OptionsSource {

  @Override
  public Map<String, String> getOptions() {
    final Map<String, String> result = new HashMap<String, String>();

    for (Entry<String, String> entry : System.getenv().entrySet()) {
      final String key = entry.getKey();
      if (key.startsWith(SystemProperties.P6SPY_PREFIX)) {
        result.put(key.substring(SystemProperties.P6SPY_PREFIX.length()), (String) entry.getValue());
      }
    }

    return result;
  }

  @Override
  public void postInit(P6ModuleManager p6moduleManager) {
  }

  @Override
  public void preDestroy(P6ModuleManager p6moduleManager) {
  }
}
