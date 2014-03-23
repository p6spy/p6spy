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
package com.p6spy.engine.proxy.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Peter Butkovic
 */
public class HashMapBasedCache<K, V> implements Cache<K, V> {

  private Map<K, V> map;

  public HashMapBasedCache() {
    super();
    this.map = new HashMap<K,V>();
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
  }

  @Override
  public void clear() {
    map.clear();
  }
}
