package com.p6spy.engine.spy.option;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.p6spy.engine.logging.P6LogFactory;
import com.p6spy.engine.logging.P6LogLoadableOptions;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.outage.P6OutageFactory;
import com.p6spy.engine.outage.P6OutageLoadableOptions;
import com.p6spy.engine.outage.P6OutageOptions;
import com.p6spy.engine.spy.P6SpyFactory;
import com.p6spy.engine.spy.P6SpyLoadableOptions;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.P6TestFramework;
import com.p6spy.engine.spy.appender.FileLogger;
import com.p6spy.engine.spy.appender.SingleLineFormat;

public class P6TestOptionDefaults {

  @BeforeClass
  public static void setUp() throws SQLException, IOException {
    // make sure to reinit properly
    new P6TestFramework("blank") {
    };
  }

  @After
  public void tearDown() throws SQLException, IOException {
    System.setProperty(SystemProperties.P6SPY_PREFIX + P6SpyOptions.MODULELIST, "");
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
    Assert.assertEquals(P6SpyFactory.class.getName(), opts.getModulelist());
    Assert.assertEquals(1, opts.getModuleFactories().size());
    Assert.assertTrue(opts.getModuleFactories().iterator().next() instanceof P6SpyFactory);
    Assert.assertEquals(1, opts.getModuleNames().size());
    Assert.assertTrue(opts.getModuleNames().contains(P6SpyFactory.class.getName()));
    Assert.assertNull(opts.getDriverlist());
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
    Assert.assertTrue(opts.getExcludeCategoriesSet().containsAll(
        Arrays.asList(new String[] { "info", "debug", "result", "resultset", "batch" })));
    Assert.assertFalse(opts.getFilter());
    Assert.assertNull(opts.getIncludeTables());
    Assert.assertNull(opts.getExcludeTables());
    Assert.assertNull(opts.getIncludeTablesPattern());
    Assert.assertNull(opts.getExcludeTablesPattern());
    Assert.assertNull(opts.getInclude());
    Assert.assertNull(opts.getExclude());
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
  }
}
