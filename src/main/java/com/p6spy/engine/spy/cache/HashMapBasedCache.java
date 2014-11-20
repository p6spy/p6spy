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
package com.p6spy.engine.spy.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache based on the underlying {@link HashMap}. <br/>
 * <br/>
 * Please note: there is a capacity that can limit size of cache. For simplicity, once capacity
 * limit is reached, cache is cleared (=> no fifo/...). It's there just to prevent related out-of
 * memory problems.
 * 
 * @author Peter Butkovic
 */
public class HashMapBasedCache<K, V> implements Cache<K, V> {

  /**
   * Cached entries holder.
   */
  private final Map<K, V> map;

  /**
   * Capacity of the cache. Cache is limited only if it's > 0.
   */
  private final int capacity;

  public HashMapBasedCache(int capacity) {
    super();
    this.capacity = capacity;
    this.map = new HashMap<K, V>();
  }

  @Override
  public V get(K key) {
    return map.get(key);
  }

  @Override
  public boolean contains(K key) {
    return map.containsKey(key);
  }

  @Override
  public void put(K key, V value) {
    map.put(key, value);

    // clear on capacity limit reached
    if (capacity > 0 && capacity < map.size()) {
      clear();

      // put once again, as it has been removed in the last clear
      map.put(key, value);
    }
  }

  @Override
  public void clear() {
    map.clear();
  }
}
