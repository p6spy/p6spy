/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2014 P6Spy
 * %%
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
 * #L%
 */
package com.p6spy.engine.spy.option;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.p6spy.engine.spy.P6ModuleManager;

/**
 * {@link OptionsSource} implementation providing options from the jdbc URL. <br/>
 * <br/>
 * Only those properties are considered, which have specific prefix, namely:
 * {@link SystemPropertiesOptionsSource#P6SPY_PREFIX}. <br/>
 * <br/>
 * As a properties separator is expected to be used character: {@code ;}
 * 
 * @see {@link JdbcUrlOptionsSourceTest#testLoadOptionsAllValidSoAllRead()} and
 *      {@link JdbcUrlOptionsSourceTest#testLoadOptionsSomeValidSoOnlyValidRead()()} for examples.
 * @author peterb
 */
public class JdbcUrlOptionsSource implements OptionsSource {

  private final String unparsed;

  private final Map<String, String> map;

  public static final String OPTIONS = "p6spy[.]config[.]([^=]+)=([^;]*);?";

  private static final Pattern PATTERN_OPTIONS = Pattern.compile(OPTIONS);

  public JdbcUrlOptionsSource(final String jdbcUrlPropertiesChunk) {
    this.unparsed = jdbcUrlPropertiesChunk;
    map = new HashMap<String, String>();
    loadOptions();
  }

  /* package protected */void loadOptions() {
    Matcher matcher = PATTERN_OPTIONS.matcher(this.unparsed);
    while (matcher.find()) {
      String key = matcher.group(1);
      String value = matcher.group(2);
      map.put(key, value);
    }
  }

  @Override
  public Map<String, String> getOptions() {
    return map;
  }

  @Override
  public void postInit(P6ModuleManager p6moduleManager) {
  }

  @Override
  public void preDestroy(P6ModuleManager p6moduleManager) {
  }

}
