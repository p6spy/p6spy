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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.logging.P6LogFactory;
import com.p6spy.engine.logging.P6LogLoadableOptions;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.outage.P6OutageFactory;
import com.p6spy.engine.outage.P6OutageLoadableOptions;
import com.p6spy.engine.outage.P6OutageOptions;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6ModuleManager;
import com.p6spy.engine.spy.P6SpyFactory;
import com.p6spy.engine.spy.P6SpyLoadableOptions;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.CustomLineFormat;
import com.p6spy.engine.spy.appender.FileLogger;
import com.p6spy.engine.spy.appender.SingleLineFormat;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class P6TestOptionDefaults extends BaseTestCase {

  public static final File LOG_FILE = new File("spy.log");
  
  public static final Category[] DEFAULT_CATEGORIES = new Category[] { 
	  Category.INFO, Category.DEBUG, Category.RESULT, Category.RESULTSET, Category.BATCH };
  
  @SuppressWarnings("unchecked")
  private static final List<Class<? extends P6Factory>> DEFAULT_FACTORIES = Arrays.asList(
      P6SpyFactory.class, P6LogFactory.class);

  @BeforeClass
  public static void setUpAll() throws SQLException, IOException {
    // cleanup all
    LOG_FILE.delete();

    // make sure to reinit properly
    new P6TestFramework("blank") {
    };
  }

  @Before
  public void setUp() {
    // make sure to have no modules explicitly loaded by default
    {
      System.clearProperty(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST);
      P6SpyOptions.getActiveInstance().reload();
    }
  }
  
  @After
  public void tearDown() throws SQLException, IOException {
    System.getProperties().remove(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST);
  }
  
  @AfterClass
  public static void tearDownAll() {
    // post clean up
    LOG_FILE.delete();
  }

  @Test
  public void testDefaultOptions() {
    assertP6FactoryClassesEqual(DEFAULT_FACTORIES, P6ModuleManager.getInstance().getFactories());
    
    Assert.assertNotNull(P6SpyOptions.getActiveInstance());
    Assert.assertNotNull(P6LogOptions.getActiveInstance());
    Assert.assertNull(P6OutageOptions.getActiveInstance());
  }
  
  private void assertP6FactoryClassesEqual(List<Class<? extends P6Factory>> expected,
                                           Collection<P6Factory> factories) {
    final Set<Class<? extends P6Factory>> expectedSet = new HashSet<Class<? extends P6Factory>>(
        expected);

    for (P6Factory factory : factories) {
      expectedSet.remove(factory.getClass());
    }
    Assert.assertTrue(expectedSet.isEmpty());
  }
  
  @Test
  public void testP6SpyOptionDefaults() {
    P6SpyLoadableOptions opts = P6SpyOptions.getActiveInstance();
    Assert.assertNotNull(opts);

    Assert.assertEquals(SingleLineFormat.class.getName(), opts.getLogMessageFormat());
    Assert.assertEquals("spy.log", opts.getLogfile());
    Assert.assertTrue(opts.getAppend());
    Assert.assertNull(opts.getDateformat());
    Assert.assertEquals(FileLogger.class.getName(), opts.getAppender());
    Assert.assertEquals(P6SpyFactory.class.getName() + ","+ P6LogFactory.class.getName(), opts.getModulelist());
    Assert.assertEquals(2, opts.getModuleFactories().size());
    assertP6FactoryClassesEqual(DEFAULT_FACTORIES, opts.getModuleFactories());
    Assert.assertEquals(2, opts.getModuleNames().size());
    Assert.assertTrue(opts.getModuleNames().contains(P6SpyFactory.class.getName()));
    Assert.assertTrue(opts.getModuleNames().contains(P6LogFactory.class.getName()));
    Assert.assertEquals("", opts.getDriverlist());
    Assert.assertNull(opts.getDriverNames());
    Assert.assertFalse(opts.getStackTrace());
    Assert.assertNull(opts.getStackTraceClass());
    Assert.assertFalse(opts.getAutoflush());
    Assert.assertFalse(opts.getReloadProperties());
    Assert.assertEquals(60L, opts.getReloadPropertiesInterval());
    Assert.assertNull(opts.getJNDIContextFactory());
    Assert.assertNull(opts.getJNDIContextProviderURL());
    Assert.assertNull(opts.getJNDIContextCustom());
    Assert.assertNull(opts.getRealDataSource());
    Assert.assertNull(opts.getRealDataSourceClass());
    Assert.assertEquals("dd-MMM-yy", opts.getDatabaseDialectDateFormat());
    Assert.assertEquals("boolean", opts.getDatabaseDialectBooleanFormat());
    Assert.assertEquals(String.format("%s|%s|%s|connection%s|%s",
      CustomLineFormat.CURRENT_TIME, CustomLineFormat.EXECUTION_TIME, CustomLineFormat.CATEGORY,
      CustomLineFormat.CONNECTION_ID, CustomLineFormat.SQL_SINGLE_LINE),
      opts.getCustomLogMessageFormat());
    Assert.assertTrue(opts.getJmx());
    Assert.assertNull(opts.getJmxPrefix());
  }

  @Test
  public void testP6LogOptionDefaults() {
    // make sure to have relevant module loaded
    {
      System.setProperty(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST,
          P6LogFactory.class.getName());
      P6SpyOptions.getActiveInstance().reload();
    }

    final P6LogLoadableOptions opts = P6LogOptions.getActiveInstance();
    Assert.assertNotNull(opts);

    Assert.assertNull(opts.getSQLExpression());
    Assert.assertEquals(0L, opts.getExecutionThreshold());
    Assert.assertEquals("info,debug,result,resultset,batch", opts.getExcludecategories());
    Assert.assertFalse(opts.getExcludebinary());
    Assert.assertTrue(opts.getExcludeCategoriesSet().containsAll(
        Arrays.asList(DEFAULT_CATEGORIES)));
    Assert.assertFalse(opts.getFilter());
    Assert.assertNull(opts.getIncludeList());
    Assert.assertNull(opts.getExcludeList());
    Assert.assertNull(opts.getIncludeExcludePattern());
    Assert.assertEquals("", opts.getInclude());
    Assert.assertEquals("", opts.getExclude());
    Assert.assertNull(opts.getSQLExpressionPattern());
  }

  @Test
  public void testP6OutageOptionDefaults() {
    // make sure to have relevant module loaded
    {
      System.setProperty(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST,
          P6OutageFactory.class.getName());
      P6SpyOptions.getActiveInstance().reload();
    }

    final P6OutageLoadableOptions opts = P6OutageOptions.getActiveInstance();
    Assert.assertNotNull(opts);

    Assert.assertFalse(opts.getOutageDetection());
    Assert.assertEquals(30L, opts.getOutageDetectionInterval());
    Assert.assertEquals(30000L, opts.getOutageDetectionIntervalMS());
    
    // cleanup - make sure go back to default modules
    {
      System.getProperties().remove(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST);
      P6SpyOptions.getActiveInstance().reload();
    }
  }

  @Test
  public void testImplicitlyDisabledLogCategories() {
    // let's explicitly remove P6LogFactory (by not including it to list)
    {
      System.setProperty(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST, "");
      P6SpyOptions.getActiveInstance().reload();
    }
    
    try {
      assertDefaultDisabledLogCategories();
    } catch(IOException e) {
      e.printStackTrace();
      Assert.fail();
    }
    
    // cleanup - make sure go back to default modules
    {
      System.getProperties().remove(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST);
      P6SpyOptions.getActiveInstance().reload();
    }
  }
  
  @Test
  public void testWithP6LogOptionDefaultsDefaultDisabledLogCategories() {
    // P6LogFactory is present by default

    try {
      assertDefaultDisabledLogCategories();
    } catch(IOException e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  private void assertDefaultDisabledLogCategories() throws IOException {
    {
      final String msg = "debug logged test msg";
      P6LogQuery.debug(msg);
      if( LOG_FILE.exists() ) {
        final String logged = FileUtils.readFileToString(LOG_FILE, "UTF-8");
        Assert.assertFalse(logged.contains(msg));
      }
    }
    
    {
      final String msg = "info logged test msg";
      P6LogQuery.info(msg);
      if( LOG_FILE.exists() ) {
        final String logged = FileUtils.readFileToString(LOG_FILE, "UTF-8");
        Assert.assertFalse(logged.contains(msg));
      }
    }
    
    {
      final String msg = "error logged test msg";
      P6LogQuery.error(msg);
      if( LOG_FILE.exists() ) {
        final String logged = FileUtils.readFileToString(LOG_FILE, "UTF-8");
        Assert.assertTrue(logged.contains(msg));
      } else {
        Assert.fail("log file not created");
      }
    }
  }
}
