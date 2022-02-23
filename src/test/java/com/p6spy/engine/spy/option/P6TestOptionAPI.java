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
package com.p6spy.engine.spy.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.p6spy.engine.logging.P6LogLoadableOptions;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.P6SpyLoadableOptions;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;

public class P6TestOptionAPI extends BaseTestCase {

  @BeforeAll
  public static void setUpAll() throws SQLException, IOException {
    // make sure to reinit properly
    new P6TestFramework("blank") {
    };
  }
  
  @Test
  public void testUnSetSpyAPI() {
    final P6SpyLoadableOptions opts = P6SpyOptions.getActiveInstance();
    
    opts.setJNDIContextCustom("foo");
    assertEquals("foo", opts.getJNDIContextCustom());
    opts.unSetJNDIContextCustom();
    assertNull(opts.getJNDIContextCustom());
    
    opts.setJNDIContextFactory("fooFactory");
    assertEquals("fooFactory", opts.getJNDIContextFactory());
    opts.unSetJNDIContextFactory();
    assertNull(opts.getJNDIContextFactory());
    
    opts.setJNDIContextProviderURL("http://fooUrl");
    assertEquals("http://fooUrl", opts.getJNDIContextProviderURL());
    opts.unSetJNDIContextProviderURL();
    assertNull(opts.getJNDIContextProviderURL());
    
    opts.setRealDataSource("fooDS");
    assertEquals("fooDS", opts.getRealDataSource());
    opts.unSetRealDataSource();
    assertNull(opts.getRealDataSource());
    
    opts.setRealDataSourceClass("fooDSClass");
    assertEquals("fooDSClass", opts.getRealDataSourceClass());
    opts.unSetRealDataSourceClass();
    assertNull(opts.getRealDataSourceClass());
    
    opts.setRealDataSourceProperties("fooDSProps");
    assertEquals("fooDSProps", opts.getRealDataSourceProperties());
    opts.unSetRealDataSourceProperties();
    assertNull(opts.getRealDataSourceProperties());
  }
  
  @Test
  public void testUnSetLogAPI() {
    final P6LogLoadableOptions opts = P6LogOptions.getActiveInstance();
    
    opts.setSQLExpression("foo");
    assertEquals("foo", opts.getSQLExpression());
    assertNotNull(opts.getSQLExpressionPattern());
    opts.unSetSQLExpression();
    assertNull(opts.getSQLExpression());
    assertNull(opts.getSQLExpressionPattern());
  }
  
}
