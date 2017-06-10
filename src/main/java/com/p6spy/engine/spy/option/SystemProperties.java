
package com.p6spy.engine.spy.option;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.p6spy.engine.spy.P6ModuleManager;

public class SystemProperties implements P6OptionsSource {

  public static final String P6SPY_PREFIX = "p6spy.config.";

  @Override
  public Map<String, String> getOptions() {
    final Map<String, String> result = new HashMap<String, String>();

    for (Entry<Object, Object> entry : new HashSet<Entry>(((Properties) System.getProperties().clone()).entrySet())) {
      final String key = entry.getKey().toString();
      if (key.startsWith(P6SPY_PREFIX)) {
        result.put(key.substring(P6SPY_PREFIX.length()), (String) entry.getValue());
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
