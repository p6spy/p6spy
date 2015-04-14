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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.p6spy.engine.test.BaseTestCase;

public class JdbcUrlOptionsSourceTest extends BaseTestCase {

  @Test
  public void testLoadOptionsAllValidSoAllRead() {
    final Map<String, String> expected = new HashMap<String, String>();

    expected.clear();
    expected.put("jmx", "");
    Assert.assertEquals(expected, new JdbcUrlOptionsSource("p6spy.config.jmx=").getOptions());
    
    expected.clear();
    expected.put("jmx", "true");
    Assert.assertEquals(expected, new JdbcUrlOptionsSource("p6spy.config.jmx=true").getOptions());
    
    expected.clear();
    expected.put("jmx", "true");
    Assert.assertEquals(expected, new JdbcUrlOptionsSource("p6spy.config.jmx=true;").getOptions());
    
    expected.clear();
    expected.put("jmx", "true");
    expected.put("jmxprefix", "foo");
    Assert.assertEquals(expected, new JdbcUrlOptionsSource("p6spy.config.jmx=true;p6spy.config.jmxprefix=foo").getOptions());
    
    expected.clear();
    expected.put("jmx", "true");
    expected.put("jmxprefix", "foo");
    Assert.assertEquals(expected, new JdbcUrlOptionsSource("p6spy.config.jmx=true;p6spy.config.jmxprefix=foo;").getOptions());
  }
  
  @Test
  public void testLoadOptionsSomeValidSoOnlyValidRead() {
    final Map<String, String> expected = new HashMap<String, String>();

    Assert.assertEquals(Collections.EMPTY_MAP, new JdbcUrlOptionsSource("jmx=true").getOptions());
    
    expected.clear();
    expected.put("jmx", "true");
    Assert.assertEquals(expected, new JdbcUrlOptionsSource("p6spy.config.jmx=true;foo=true").getOptions());
    
    expected.clear();
    expected.put("jmx", "true");
    expected.put("jmxprefix", "foo");
    Assert.assertEquals(expected, new JdbcUrlOptionsSource("p6spy.config.jmx=true;p6spy.config.jmxprefix=foo;foo=true").getOptions());
    
    expected.clear();
    expected.put("jmx", "true");
    Assert.assertEquals(expected, new JdbcUrlOptionsSource("foo=true;p6spy.config.jmx=true").getOptions());
  }
}
