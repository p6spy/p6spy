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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

public class P6TestOptionDefaults extends BaseTestCase {

  public static final File LOG_FILE = new File("spy.log");
  
  public static final Category[] DEFAULT_CATEGORIES = new Category[] { 
	  Category.INFO, Category.DEBUG, Category.RESULT, Category.RESULTSET, Category.BATCH };
  
  @SuppressWarnings("unchecked")
  private static final List<Class<? extends P6Factory>> DEFAULT_FACTORIES = Arrays.asList(
      P6SpyFactory.class, P6LogFactory.class);

  @BeforeAll
  public static void setUpAll() throws SQLException, IOException {
    // cleanup all
    LOG_FILE.delete();

    // make sure to reinit properly
    new P6TestFramework("blank") {
    };
  }

  @BeforeEach
  public void setUp() {
    // make sure to have no modules explicitly loaded by default
    {
      System.clearProperty(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST);
      P6SpyOptions.getActiveInstance().reload();
    }
  }
  
  @AfterEach
  public void tearDown() throws SQLException, IOException {
    System.getProperties().remove(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST);
  }
  
  @AfterAll
  public static void tearDownAll() {
    // post clean up
    LOG_FILE.delete();
  }

  @Test
  public void testDefaultOptions() {
    assertP6FactoryClassesEqual(DEFAULT_FACTORIES, P6ModuleManager.getInstance().getFactories());
    
    assertNotNull(P6SpyOptions.getActiveInstance());
    assertNotNull(P6LogOptions.getActiveInstance());
    assertNull(P6OutageOptions.getActiveInstance());
  }
  
  private void assertP6FactoryClassesEqual(List<Class<? extends P6Factory>> expected,
                                           Collection<P6Factory> factories) {
    final Set<Class<? extends P6Factory>> expectedSet = new HashSet<Class<? extends P6Factory>>(
        expected);

    for (P6Factory factory : factories) {
      expectedSet.remove(factory.getClass());
    }
    assertTrue(expectedSet.isEmpty());
  }
  
  @Test
  public void testP6SpyOptionDefaults() {
    P6SpyLoadableOptions opts = P6SpyOptions.getActiveInstance();
    assertNotNull(opts);

    assertEquals(SingleLineFormat.class.getName(), opts.getLogMessageFormat());
    assertEquals("spy.log", opts.getLogfile());
    assertTrue(opts.getAppend());
    assertNull(opts.getDateformat());
    assertEquals(FileLogger.class.getName(), opts.getAppender());
    assertEquals(P6SpyFactory.class.getName() + ","+ P6LogFactory.class.getName(), opts.getModulelist());
    assertEquals(2, opts.getModuleFactories().size());
    assertP6FactoryClassesEqual(DEFAULT_FACTORIES, opts.getModuleFactories());
    assertEquals(2, opts.getModuleNames().size());
    assertTrue(opts.getModuleNames().contains(P6SpyFactory.class.getName()));
    assertTrue(opts.getModuleNames().contains(P6LogFactory.class.getName()));
    assertEquals("", opts.getDriverlist());
    assertNull(opts.getDriverNames());
    assertFalse(opts.getStackTrace());
    assertNull(opts.getStackTraceClass());
    assertFalse(opts.getAutoflush());
    assertFalse(opts.getReloadProperties());
    assertEquals(60L, opts.getReloadPropertiesInterval());
    assertNull(opts.getJNDIContextFactory());
    assertNull(opts.getJNDIContextProviderURL());
    assertNull(opts.getJNDIContextCustom());
    assertNull(opts.getRealDataSource());
    assertNull(opts.getRealDataSourceClass());
    assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSZ", opts.getDatabaseDialectDateFormat());
    assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSZ", opts.getDatabaseDialectTimestampFormat());
    assertEquals("boolean", opts.getDatabaseDialectBooleanFormat());
    assertEquals(String.format("%s|%s|%s|connection%s|%s",
      CustomLineFormat.CURRENT_TIME, CustomLineFormat.EXECUTION_TIME, CustomLineFormat.CATEGORY,
      CustomLineFormat.CONNECTION_ID, CustomLineFormat.SQL_SINGLE_LINE),
      opts.getCustomLogMessageFormat());
    assertTrue(opts.getJmx());
    assertNull(opts.getJmxPrefix());
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
    assertNotNull(opts);

    assertNull(opts.getSQLExpression());
    assertEquals(0L, opts.getExecutionThreshold());
    assertEquals("info,debug,result,resultset,batch", opts.getExcludecategories());
    assertFalse(opts.getExcludebinary());
    assertTrue(opts.getExcludeCategoriesSet().containsAll(
        Arrays.asList(DEFAULT_CATEGORIES)));
    assertFalse(opts.getFilter());
    assertNull(opts.getIncludeList());
    assertNull(opts.getExcludeList());
    assertNull(opts.getIncludeExcludePattern());
    assertEquals("", opts.getInclude());
    assertEquals("", opts.getExclude());
    assertNull(opts.getSQLExpressionPattern());
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
    assertNotNull(opts);

    assertFalse(opts.getOutageDetection());
    assertEquals(30L, opts.getOutageDetectionInterval());
    assertEquals(30000L, opts.getOutageDetectionIntervalMS());
    
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
      fail();
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
      fail();
    }
  }

  private void assertDefaultDisabledLogCategories() throws IOException {
    {
      final String msg = "debug logged test msg";
      P6LogQuery.debug(msg);
      if( LOG_FILE.exists() ) {
        final String logged = FileUtils.readFileToString(LOG_FILE, "UTF-8");
        assertFalse(logged.contains(msg));
      }
    }
    
    {
      final String msg = "info logged test msg";
      P6LogQuery.info(msg);
      if( LOG_FILE.exists() ) {
        final String logged = FileUtils.readFileToString(LOG_FILE, "UTF-8");
        assertFalse(logged.contains(msg));
      }
    }
    
    {
      final String msg = "error logged test msg";
      P6LogQuery.error(msg);
      if( LOG_FILE.exists() ) {
        final String logged = FileUtils.readFileToString(LOG_FILE, "UTF-8");
        assertTrue(logged.contains(msg));
      } else {
        fail("log file not created");
      }
    }
  }
}
