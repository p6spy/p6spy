package com.p6spy.engine.spy.appender;

import com.google.gson.Gson;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.json.LogEvent;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JSONFormatTest extends BaseTestCase {

  private static final String SUFFIX_DEBUG = "DEBUG";

  private P6TestFramework framework;
  private Gson gson;

  @Before
  public void setup() {
    gson = new Gson();
  }

  @Test
  public void formatIncludesDefaultOptions() {
    String logMessage = new JSONFormat().formatMessage(124, "44", 1L, "statement",
      "select * from foo where bar=?",
      "select * from foo where bar=true", "jdbc:h2:mem:p6spyDSTest");

    final LogEvent logEvent = gson.fromJson(logMessage, LogEvent.class);

    Assert.assertEquals(124, logEvent.getConnectionId());
    Assert.assertEquals(44L, logEvent.getTimestamp());
    Assert.assertEquals(1L, logEvent.getExecutionTime());
    Assert.assertEquals("statement", logEvent.getCategory());
    Assert.assertNull(logEvent.getPreparedSql());
    Assert.assertEquals("select * from foo where bar=true", logEvent.getSql());
    Assert.assertEquals("jdbc:h2:mem:p6spyDSTest", logEvent.getConnectionUrl());
  }

  @Test
  public void formatIncludesStackTraceWhenEnabled() {

    P6SpyOptions.getActiveInstance().setJSONStackTrace(true);

    String logMessage = new JSONFormat().formatMessage(124, "44", 1L, "statement",
      "select * from foo where bar=?",
      "select * from foo where bar=true", "jdbc:h2:mem:p6spyDSTest");

    final LogEvent logEvent = gson.fromJson(logMessage, LogEvent.class);

    Assert.assertNotNull(logEvent.getStackTrace());
  }
}
