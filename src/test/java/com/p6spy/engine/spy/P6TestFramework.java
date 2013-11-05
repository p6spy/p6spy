/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.p6spy.engine.spy;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.appender.P6TestLogger;
import com.p6spy.engine.spy.option.SpyDotProperties;
import com.p6spy.engine.test.P6TestOptions;
import org.apache.log4j.Logger;
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
import java.util.Enumeration;
import java.util.List;

public abstract class P6TestFramework {
  private static final Logger log = Logger.getLogger(P6TestFramework.class);

  /**
   * Environment variable enabling control over the DB to be used for testing. <br/>
   * <br/>
   * Comma separated values are allowed here, like:
   * <p>
   * MySQL,PostgresSQL,H2,HSQLDB,SQLite
   * </p>
   * However if none is specified, default is:
   * <p>
   * H2
   * </p>
   */
  private static final String ENV_DB = (System.getProperty("DB") == null ? "H2" : System
      .getProperty("DB"));

  public static final Collection<Object[]> DBS_IN_TEST;

  static {
    if (ENV_DB.contains(",")) {
      Object[] dbs = ENV_DB.split(",");
      Object[][] params = new Object[dbs.length][1];
      for (int i = 0; i < dbs.length; i++) {
        params[i] = new Object[]{dbs[i]};
      }
      DBS_IN_TEST = Arrays.asList(params);
    } else {
      DBS_IN_TEST = Arrays.asList(new Object[][]{{ENV_DB}});
    }
  }

  public static final String TEST_FILE_PATH = "target/test-classes/com/p6spy/engine/spy";
  
  protected final String db;

  protected Connection connection = null;

  public P6TestFramework(String db) throws SQLException, IOException {
    this.db = db;
    final File p6TestProperties = new File (TEST_FILE_PATH, "P6Test_" + db + ".properties");
    System.setProperty(SpyDotProperties.OPTIONS_FILE_PROPERTY, p6TestProperties.getAbsolutePath());
    log.info("Setting up test for "+db);
    
    // make sure to reinit for each Driver run as we run parametrized builds
    // and need to have fresh stuff for every specific driver
    P6Core.reinit();
  }

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> dbs() {
    return DBS_IN_TEST;
  }
    
  @Before
  public void setUpFramework() throws Exception {
      Collection<String> driverNames = P6SpyOptions.getActiveInstance().getDriverNames();
      String user = P6TestOptions.getActiveInstance().getUser();
      String password = P6TestOptions.getActiveInstance().getPassword();
      String url = P6TestOptions.getActiveInstance().getUrl();

      if( driverNames != null && !driverNames.isEmpty()) {
        for (String driverName : driverNames) {
          P6Util.forName(driverName);                
        }
      }

      Driver driver = DriverManager.getDriver(url);
      System.err.println("FRAMEWORK USING DRIVER == " + driver.getClass().getName() + " FOR URL " + url);
      connection = DriverManager.getConnection(url, user, password);

      printAllDrivers();
  }

  protected static String getStackTrace(Exception e) {
      CharArrayWriter c = new CharArrayWriter();
      e.printStackTrace(new PrintWriter(c));
      return c.toString();
  }

  protected static void printAllDrivers() {
    for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
      System.err.println("1 DRIVER FOUND == " + e.nextElement());
    }
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
  
  protected void clearLogEnties() {
    failOnNonP6TestLoggerUsage();
    ((P6TestLogger) P6LogQuery.getLogger()).clearLogEntries();
  }
  
  protected int getLogEntiesCount() {
    failOnNonP6TestLoggerUsage();
    return ((P6TestLogger) P6LogQuery.getLogger()).getLogs().size();
  }

  protected List<String> getLogEnties() {
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
