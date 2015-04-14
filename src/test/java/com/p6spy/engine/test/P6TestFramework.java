/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
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
package com.p6spy.engine.test;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.P6Core;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.P6TestUtil;
import com.p6spy.engine.spy.appender.P6TestLogger;
import com.p6spy.engine.spy.option.SpyDotPropertiesOptionsSource;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.runners.Parameterized.Parameters;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Base test case for tests which should execute against all databases defined for testing.
 * The list of database is defined by the system property 'DB'.  It should be set to a
 * comma separated list of thr database names.  If not set, it defaults to H2.
 * <p>
 * Example: MySQL,PostgresSQL,H2,HSQLDB,SQLite
 * </p>
 */
public abstract class P6TestFramework extends BaseTestCase {
  private static final Logger log = Logger.getLogger(P6TestFramework.class);

  public static final String TEST_FILE_PATH = "target/test-classes/com/p6spy/engine/spy";

  protected final String db;

  protected Connection connection = null;

  public P6TestFramework(String db) throws SQLException, IOException {
    this.db = db;
    final File p6TestProperties = new File(TEST_FILE_PATH, "P6Test_" + db + ".properties");
    System.setProperty(SpyDotPropertiesOptionsSource.OPTIONS_FILE_PROPERTY, p6TestProperties.getAbsolutePath());
    log.info("P6Spy will be configured using " + p6TestProperties.getName());

    // make sure to reinit for each Driver run as we run parametrized builds
    // and need to have fresh stuff for every specific driver
    P6Core.reinit();
  }

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> dbs() {
    Collection<Object[]> result;
    String dbList = (System.getProperty("DB") == null ? "H2" : System.getProperty("DB"));

    if (dbList.contains(",")) {
      Object[] dbs = dbList.split(",");
      Object[][] params = new Object[dbs.length][1];
      for (int i = 0; i < dbs.length; i++) {
        params[i] = new Object[]{dbs[i]};
      }
      result = Arrays.asList(params);
    } else {
      result = Arrays.asList(new Object[][]{{dbList}});
    }

    return result;
  }

  @Before
  public void setUpFramework() throws Exception {
    Collection<String> driverNames = P6SpyOptions.getActiveInstance().getDriverNames();
    String user = P6TestOptions.getActiveInstance().getUser();
    String password = P6TestOptions.getActiveInstance().getPassword();
    String url = P6TestOptions.getActiveInstance().getUrl();

    if (driverNames != null && !driverNames.isEmpty()) {
      for (String driverName : driverNames) {
        P6Util.forName(driverName);
      }
    }

    Driver driver = DriverManager.getDriver(url);
    if (log.isDebugEnabled()) {
      log.debug("FRAMEWORK USING DRIVER == " + driver.getClass().getName() + " FOR URL " + url);
    }
    connection = DriverManager.getConnection(url, user, password);

    P6TestUtil.printAllDrivers();
    P6TestUtil.setupTestData(url, user, password);


  }

  @After
  public void closeConnection() throws Exception {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  protected static String getStackTrace(Exception e) {
    CharArrayWriter c = new CharArrayWriter();
    e.printStackTrace(new PrintWriter(c));
    return c.toString();
  }

  //
  // log entries retrieval
  //

  private void failOnNonP6TestLoggerUsage() {
    if (!(P6LogQuery.getLogger() instanceof P6TestLogger)) {
      throw new IllegalStateException();
    }
  }

  protected String getLastLogEntry() {
    failOnNonP6TestLoggerUsage();
    return ((P6TestLogger) P6LogQuery.getLogger()).getLastEntry();
  }

  protected void clearLogEntries() {
    failOnNonP6TestLoggerUsage();
    ((P6TestLogger) P6LogQuery.getLogger()).clearLogEntries();
  }

  protected int getLogEntriesCount() {
    failOnNonP6TestLoggerUsage();
    return ((P6TestLogger) P6LogQuery.getLogger()).getLogs().size();
  }

  protected List<String> getLogEntries() {
    failOnNonP6TestLoggerUsage();
    return ((P6TestLogger) P6LogQuery.getLogger()).getLogs();
  }

  protected String getLastButOneLogEntry() {
    failOnNonP6TestLoggerUsage();
    return ((P6TestLogger) P6LogQuery.getLogger()).getLastButOneEntry();
  }

  protected String getLastLogStackTrace() {
    failOnNonP6TestLoggerUsage();
    return ((P6TestLogger) P6LogQuery.getLogger()).getLastStacktrace();
  }

  protected void clearLastLogStackTrace() {
    failOnNonP6TestLoggerUsage();
    ((P6TestLogger) P6LogQuery.getLogger()).clearLastStacktrace();
  }
}
