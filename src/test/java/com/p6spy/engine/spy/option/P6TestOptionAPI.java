/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
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

package com.p6spy.engine.spy.option;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.p6spy.engine.logging.P6LogLoadableOptions;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.P6SpyLoadableOptions;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;

public class P6TestOptionAPI extends BaseTestCase {

  @BeforeClass
  public static void setUpAll() throws SQLException, IOException {
    // make sure to reinit properly
    new P6TestFramework("blank") {
    };
  }
  
  @Test
  public void testUnSetSpyAPI() {
    final P6SpyLoadableOptions opts = P6SpyOptions.getActiveInstance();
    
    opts.setJNDIContextCustom("foo");
    Assert.assertEquals("foo", opts.getJNDIContextCustom());
    opts.unSetJNDIContextCustom();
    Assert.assertNull(opts.getJNDIContextCustom());
    
    opts.setJNDIContextFactory("fooFactory");
    Assert.assertEquals("fooFactory", opts.getJNDIContextFactory());
    opts.unSetJNDIContextFactory();
    Assert.assertNull(opts.getJNDIContextFactory());
    
    opts.setJNDIContextProviderURL("http://fooUrl");
    Assert.assertEquals("http://fooUrl", opts.getJNDIContextProviderURL());
    opts.unSetJNDIContextProviderURL();
    Assert.assertNull(opts.getJNDIContextProviderURL());
    
    opts.setRealDataSource("fooDS");
    Assert.assertEquals("fooDS", opts.getRealDataSource());
    opts.unSetRealDataSource();
    Assert.assertNull(opts.getRealDataSource());
    
    opts.setRealDataSourceClass("fooDSClass");
    Assert.assertEquals("fooDSClass", opts.getRealDataSourceClass());
    opts.unSetRealDataSourceClass();
    Assert.assertNull(opts.getRealDataSourceClass());
    
    opts.setRealDataSourceProperties("fooDSProps");
    Assert.assertEquals("fooDSProps", opts.getRealDataSourceProperties());
    opts.unSetRealDataSourceProperties();
    Assert.assertNull(opts.getRealDataSourceProperties());
  }
  
  @Test
  public void testUnSetLogAPI() {
    final P6LogLoadableOptions opts = P6LogOptions.getActiveInstance();
    
    opts.setSQLExpression("foo");
    Assert.assertEquals("foo", opts.getSQLExpression());
    Assert.assertNotNull(opts.getSQLExpressionPattern());
    opts.unSetSQLExpression();
    Assert.assertNull(opts.getSQLExpression());
    Assert.assertNull(opts.getSQLExpressionPattern());
  }
  
}
