
package com.p6spy.engine.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class P6UtilTest {
  @Test
  public void testSingleLine() {
      Assert.assertEquals("abc efg", P6Util.singleLine("abc\nefg"));
      Assert.assertEquals("abc efg", P6Util.singleLine("abc\n\nefg"));
      Assert.assertEquals("abc efg", P6Util.singleLine("abc\r\n\nefg"));
  }
  @Test 
  public void testJoinNullSafe() {
    Assert.assertEquals("", P6Util.joinNullSafe(null, null));
    Assert.assertEquals("", P6Util.joinNullSafe(null, ""));
    Assert.assertEquals("", P6Util.joinNullSafe(null, ","));
    Assert.assertEquals("", P6Util.joinNullSafe(Collections.<String>emptyList(), null));
    Assert.assertEquals("", P6Util.joinNullSafe(Collections.<String>emptyList(), ","));
    Assert.assertEquals("foo", P6Util.joinNullSafe(Arrays.asList("foo"), ","));
    Assert.assertEquals("foobar", P6Util.joinNullSafe(Arrays.asList("foo", "bar"), null));
    Assert.assertEquals("foo,bar", P6Util.joinNullSafe(Arrays.asList("foo", "bar"), ","));
    Assert.assertEquals("foo|bar|aaa", P6Util.joinNullSafe(Arrays.asList("foo", "bar", "aaa"), "|"));
  }
  
  @Test 
  public void testGetPropertiesMap() throws IOException {
	  final String string = "key1=\nkey2=val2";
	  final Properties properties = new Properties();
	  properties.load(new ByteArrayInputStream(string.getBytes()));
	  final Map<String, String> map = P6Util.getPropertiesMap(properties);
	  
	  Assert.assertTrue(map.containsKey("key1"));
	  Assert.assertEquals("", map.get("key1"));
	  Assert.assertTrue(map.containsKey("key2"));
	  Assert.assertEquals("val2", map.get("key2"));
	  Assert.assertFalse(map.containsKey("key3"));
	  Assert.assertNull(map.get("key3"));
  }
}
