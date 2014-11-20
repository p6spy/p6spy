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

import com.p6spy.engine.spy.cache.Cache;
import com.p6spy.engine.spy.cache.CacheFactory;

/**
 * Factory for {@link P6JdbcUrlTest}s supporting caching.
 * 
 * @author peterb
 */
public class P6JdbcUrlFactory {

  public static int CACHE_CAPACITY = 100;
  
  static Cache<String, P6JdbcUrl> cache = CacheFactory.<String, P6JdbcUrl> newCache(CACHE_CAPACITY);
  
  public static P6JdbcUrl getP6JdbcUrl(String url) {
    P6JdbcUrl cached = cache.get(url);
    
    if (null == cached) {
      cached = new P6JdbcUrl(url);
      cache.put(url, cached);
    }
    
    return cached;
  }

  /**
   * Clears cache.
   */
  public static void clearCache() {
    cache.clear();
  }
}
