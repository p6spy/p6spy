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

import org.junit.Assert;
import org.junit.Test;

import com.p6spy.engine.spy.cache.Cache;
import com.p6spy.engine.spy.cache.CacheFactory;

/**
 * @author Peter Butkovic
 */
public class CacheTest {

  @Test
  public void testNewCacheInstance() {
    Assert.assertNotNull(CacheFactory.newCache());
    Assert.assertNotEquals(CacheFactory.newCache(), CacheFactory.newCache());
  }
  
  @Test
  public void testPutAndGet() {
    final Cache<String, String> cache = CacheFactory.<String, String>newCache();
    Assert.assertNull(cache.get("key1"));
    cache.put("key1", "value1");
    Assert.assertNotNull(cache.get("key1"));
    Assert.assertEquals("value1", cache.get("key1"));
    Assert.assertNull(cache.get("key2"));
  }
  
  @Test
  public void testPutAndContains() {
    final Cache<String, String> cache = CacheFactory.<String, String>newCache();
    Assert.assertFalse(cache.contains("key1"));
    cache.put("key1", "value1");
    Assert.assertTrue(cache.contains("key1"));
    Assert.assertFalse(cache.contains("key2"));
  }
  
  @Test
  public void testClear() {
    final Cache<String, String> cache = CacheFactory.<String, String>newCache();
    cache.put("key1", "value1");
    Assert.assertTrue(cache.contains("key1"));
    cache.clear();
    Assert.assertFalse(cache.contains("key1"));
  }

  @Test
  public void testCapacityNotExceeded() {
    Cache<String, String> cache = CacheFactory.<String, String> newCache(2);

    cache.put("1", "1");
    cache.put("2", "2");
    Assert.assertTrue(cache.contains("1"));
    Assert.assertTrue(cache.contains("2"));

    // capacity reached => clear
    
    cache.put("3", "3");
    Assert.assertFalse(cache.contains("1"));
    Assert.assertFalse(cache.contains("2"));
    Assert.assertTrue(cache.contains("3"));
  }
}
