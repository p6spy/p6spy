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
package com.p6spy.engine.spy;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.p6spy.engine.spy.option.JdbcUrlOptionsSource;
import com.p6spy.engine.spy.option.OptionsRepository;
import com.p6spy.engine.spy.option.OptionsRepositoryFactory;

/**
 * Jdbc URL parsed chunks holder/accessor.
 * 
 * @author peterb
 */
public class P6JdbcUrl {

  /**
   * Pattern indivating whether particular jdbc url is to be accepted by {@link P6SpyDriver}.
   */
  public static final Pattern PATTERN_URL = Pattern
      .compile("^jdbc:p6spy(:p6spy[^:]*)?(:.*)$");
  
  private final boolean accepted;
  private final String proxiedUrl;
  private final OptionsRepository optionsRepository;
  
  
  public P6JdbcUrl(final String url) {
    Matcher matcher = null;
    accepted = null != url && (matcher = PATTERN_URL.matcher(url)).matches();
    
    if (accepted) {
      proxiedUrl = "jdbc" + matcher.group(2);
      optionsRepository = OptionsRepositoryFactory.getRepository(false);
      loadOptions(optionsRepository, matcher.group(1));
    } else {
      proxiedUrl = url;
      optionsRepository = null;
    }
  }
  
  private void loadOptions(OptionsRepository optionsRepository, String propertiesChunk) {
    // nothing to do for empty props
    if (null == propertiesChunk || propertiesChunk.isEmpty()) {
      return;
    }
    
    // configured modules init
    final List<P6Factory> moduleFactories = P6ModuleManager.getInstance().getFactories();
    
    // can that happen?
    if (null == moduleFactories) {
      return;
    }

    final Map<String, String> toLoad = new JdbcUrlOptionsSource(propertiesChunk).getOptions();
    for (P6Factory factory : moduleFactories) {
      final P6LoadableOptions options = factory.getOptions(optionsRepository);
      if (null != toLoad) {
        options.load(toLoad);
      }
    }
  }

  public boolean isAccepted() {
    return accepted;
  }
  
  public String getProxiedUrl() {
    return proxiedUrl;
  }

  public OptionsRepository getOptionsRepository() {
    return optionsRepository;
  }

}