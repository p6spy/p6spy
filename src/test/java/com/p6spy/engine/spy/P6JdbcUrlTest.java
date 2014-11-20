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

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import com.p6spy.engine.test.BaseTestCase;

public class P6JdbcUrlTest extends BaseTestCase {

  @Test
  public void testIsAcceptedForNotAcceptedUrls() throws SQLException {
    Assert.assertFalse(new P6JdbcUrl(null).isAccepted());
    Assert.assertFalse(new P6JdbcUrl("").isAccepted());
    Assert.assertFalse(new P6JdbcUrl("jdbc:mysql:localhost:123").isAccepted());

    // also invalid/non-parsable would not be accepted!
    Assert.assertTrue(new P6JdbcUrl("jdbc:p6spy:jmx=true:mysql:localhost:123").isAccepted());
    Assert.assertTrue(new P6JdbcUrl("jdbc:p6spy:foo=true;p6spy.config.jmx=true:mysql:localhost:123").isAccepted());
  }
  
  @Test
  public void testIsAcceptedForAcceptedUrls() throws SQLException {
    Assert.assertTrue(new P6JdbcUrl("jdbc:p6spy:sqlite:target/p6spy.db").isAccepted());
    Assert.assertTrue(new P6JdbcUrl("jdbc:p6spy:mysql:localhost:123").isAccepted());
    Assert.assertTrue(new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true:mysql:localhost:123").isAccepted());
    Assert.assertTrue(new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true;:mysql:localhost:123").isAccepted());
    Assert.assertTrue(new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true;p6spy.config.jmxprefix=foo:mysql:localhost:123").isAccepted());
    Assert.assertTrue(new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true;p6spy.config.jmxprefix=foo;:mysql:localhost:123").isAccepted());
    Assert.assertTrue(new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true;foo=true:mysql:localhost:123").isAccepted());
    Assert.assertTrue(new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true;p6spy.config.jmxprefix=foo;foo=true:mysql:localhost:123").isAccepted());
  }
  
  @Test
  public void testGetProxiedUrl() throws SQLException {
    final String realURL = "jdbc:mysql:localhost:123";
    
    Assert.assertEquals(realURL, new P6JdbcUrl("jdbc:mysql:localhost:123").getProxiedUrl());
    Assert.assertEquals(realURL, new P6JdbcUrl("jdbc:p6spy:mysql:localhost:123").getProxiedUrl());
    Assert.assertEquals(realURL, new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true:mysql:localhost:123").getProxiedUrl());
    Assert.assertEquals(realURL, new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true;:mysql:localhost:123").getProxiedUrl());
    Assert.assertEquals(realURL, new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true;p6spy.config.jmxprefix=foo:mysql:localhost:123").getProxiedUrl());
    Assert.assertEquals(realURL, new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true;p6spy.config.jmxprefix=foo;:mysql:localhost:123").getProxiedUrl());
    Assert.assertEquals(realURL, new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true;foo=true:mysql:localhost:123").getProxiedUrl());
    Assert.assertEquals(realURL, new P6JdbcUrl("jdbc:p6spy:p6spy.config.jmx=true;p6spy.config.jmxprefix=foo;foo=true:mysql:localhost:123").getProxiedUrl());
    
    // invalid ones - no point of testing these, as they won't be accepted by driver at all!
    //Assert.assertEquals("???", new P6JdbcUrl("jdbc:p6spy:foo=true;p6spy.config.jmx=true:mysql:localhost:123"));
    //Assert.assertEquals("???", new P6JdbcUrl("jdbc:p6spy:jmx=true:mysql:localhost:123"));
  }
}
