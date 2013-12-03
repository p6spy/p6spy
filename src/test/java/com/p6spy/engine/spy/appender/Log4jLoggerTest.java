package com.p6spy.engine.spy.appender;

import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.P6TestUtil;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;
import liquibase.exception.LiquibaseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Log4jLoggerTest extends BaseTestCase {

  P6TestFramework framework;

  @Before
  public void setup() throws Exception {
    // reset log4j
    LogManager.resetConfiguration();
  }

  @After
  public void cleanup() throws Exception {
    // reset log4j
    LogManager.resetConfiguration();

    // load default configuration
    configureLog4J();
  }

  private void configureLog4J() {
    DOMConfigurator.configure("target/test-classes/log4j.xml");
  }

  @Test
  public void testLoggingWithUnconfiguredLog4J() throws Exception {
    // initialize framework
    framework = new P6TestFramework("log4j") {
    };
    framework.setUpFramework();

    Connection con = DriverManager.getConnection("jdbc:p6spy:h2:mem:p6spy", "sa", null);

    Log4JTestApppender.clearCapturedMessages();
    P6TestUtil.queryForInt(con, "select count(*) from customers");

    con.close();

    assertEquals(1, Log4JTestApppender.getCapturedMessages().size());

    framework.closeConnection();
  }

  @Test
  public void testExternallyConfiguredLog4J() throws Exception {
    // configure log4j externally
    configureLog4J();

    // initialize framework
    framework = new P6TestFramework("log4j") {
    };
    framework.setUpFramework();

    Connection con = DriverManager.getConnection("jdbc:p6spy:h2:mem:p6spy", "sa", null);

    Log4JTestApppender.clearCapturedMessages();
    P6TestUtil.queryForInt(con, "select count(*) from customers");

    con.close();

    assertEquals(1, Log4JTestApppender.getCapturedMessages().size());

    framework.closeConnection();
  }

  public static class Log4JTestApppender extends ConsoleAppender {
    static List<String> messages = new ArrayList<String>();

    public static void clearCapturedMessages() {
      messages.clear();
    }

    public static List<String> getCapturedMessages() {
      return messages;
    }

    @Override
    protected void subAppend(LoggingEvent event) {
      messages.add(event.getMessage().toString());
      super.subAppend(event);
    }
  }
}
