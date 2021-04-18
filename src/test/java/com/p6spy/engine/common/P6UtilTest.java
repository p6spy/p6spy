/**
 * P6Spy
 *
 * Copyright (C) 2002 P6Spy
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
package com.p6spy.engine.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class P6UtilTest {
  @Test
  public void testSingleLine() {
      assertEquals("abc efg", P6Util.singleLine("abc\nefg"));
      assertEquals("abc efg", P6Util.singleLine("abc\n\nefg"));
      assertEquals("abc efg", P6Util.singleLine("abc\r\n\nefg"));
  }
  @Test 
  public void testJoinNullSafe() {
    assertEquals("", P6Util.joinNullSafe(null, null));
    assertEquals("", P6Util.joinNullSafe(null, ""));
    assertEquals("", P6Util.joinNullSafe(null, ","));
    assertEquals("", P6Util.joinNullSafe(Collections.<String>emptyList(), null));
    assertEquals("", P6Util.joinNullSafe(Collections.<String>emptyList(), ","));
    assertEquals("foo", P6Util.joinNullSafe(Arrays.asList("foo"), ","));
    assertEquals("foobar", P6Util.joinNullSafe(Arrays.asList("foo", "bar"), null));
    assertEquals("foo,bar", P6Util.joinNullSafe(Arrays.asList("foo", "bar"), ","));
    assertEquals("foo|bar|aaa", P6Util.joinNullSafe(Arrays.asList("foo", "bar", "aaa"), "|"));
  }
  
  @Test 
  public void testGetPropertiesMap() throws IOException {
	  final String string = "key1=\nkey2=val2";
	  final Properties properties = new Properties();
	  properties.load(new ByteArrayInputStream(string.getBytes()));
	  final Map<String, String> map = P6Util.getPropertiesMap(properties);
	  
	  assertTrue(map.containsKey("key1"));
	  assertEquals("", map.get("key1"));
	  assertTrue(map.containsKey("key2"));
	  assertEquals("val2", map.get("key2"));
	  assertFalse(map.containsKey("key3"));
	  assertNull(map.get("key3"));
  }
}
