/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
