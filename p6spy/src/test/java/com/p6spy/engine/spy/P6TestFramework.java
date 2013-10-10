/*
 *
 * ====================================================================
 *
 * The P6Spy Software License, Version 1.1
 *
 * This license is derived and fully compatible with the Apache Software
 * license, see http://www.apache.org/LICENSE.txt
 *
 * Copyright (c) 2001-2002 Andy Martin, Ph.D. and Jeff Goke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement:
 * "The original concept and code base for P6Spy was conceived
 * and developed by Andy Martin, Ph.D. who generously contribued
 * the first complete release to the public under this license.
 * This product was due to the pioneering work of Andy
 * that began in December of 1995 developing applications that could
 * seamlessly be deployed with minimal effort but with dramatic results.
 * This code is maintained and extended by Jeff Goke and with the ideas
 * and contributions of other P6Spy contributors.
 * (http://www.p6spy.com)"
 * Alternately, this acknowlegement may appear in the software itself,
 * if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "P6Spy", "Jeff Goke", and "Andy Martin" must not be used
 * to endorse or promote products derived from this software without
 * prior written permission. For written permission, please contact
 * license@p6spy.com.
 *
 * 5. Products derived from this software may not be called "P6Spy"
 * nor may "P6Spy" appear in their names without prior written
 * permission of Jeff Goke and Andy Martin.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

/**
 * Description: Test class for statements
 *
 * $Author: cheechq $
 * $Revision: 1.11 $
 * $Date: 2003/06/03 19:20:26 $
 *
 * $Id: P6TestFramework.java,v 1.11 2003/06/03 19:20:26 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestFramework.java,v $
 * $Log: P6TestFramework.java,v $
 * Revision 1.11  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.10  2003/04/09 16:44:00  jeffgoke
 * Added Jboss JMX support.  Updated documentation.  Added execution threshold property to only log queries taking longer than a specified time.
 *
 * Revision 1.9  2003/02/14 22:22:58  aarvesen
 * use a define for the property file
 *
 * Revision 1.8  2003/01/28 19:32:31  jeffgoke
 * fixed bug exposed by test framework where option reloading was having problems if options were manipulated before the driver was created.
 *
 * Revision 1.7  2003/01/28 17:59:12  jeffgoke
 * fixed test cases to use new options
 *
 * Revision 1.6  2003/01/23 00:43:37  aarvesen
 * Changed the module to be dot rather than underscore
 *
 * Revision 1.5  2003/01/03 21:19:24  aarvesen
 * use the new P6Util.forName
 *
 * Revision 1.4  2002/12/19 23:46:54  aarvesen
 * use factory rather than driver
 *
 * Revision 1.3  2002/12/18 01:03:03  aarvesen
 * Remove no-longer-used p6cache driver
 *
 * Revision 1.2  2002/10/06 18:24:04  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:30:46  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.4  2002/05/18 06:39:52  jeffgoke
 * Peter Laird added Outage detection.  Added junit tests for outage detection.
 * Fixed multi-driver tests.
 *
 * Revision 1.3  2002/05/16 04:58:40  jeffgoke
 * Viktor Szathmary added multi-driver support.
 * Rewrote P6SpyOptions to be easier to manage.
 * Fixed several bugs.
 *
 * Revision 1.2  2002/05/05 00:43:00  jeffgoke
 * Added Philip's reload code.
 *
 * Revision 1.1  2002/04/21 06:16:20  jeffgoke
 * added test cases, fixed batch bugs
 *
 *
 *
 */

package com.p6spy.engine.spy;

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

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.runners.Parameterized.Parameters;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.common.SpyDotProperties;
import com.p6spy.engine.logging.appender.P6TestLogger;
import com.p6spy.engine.test.P6TestOptions;

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
    File p6TestProperties = new File (TEST_FILE_PATH, "P6Test_" + db + ".properties");
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
      List<String> driverNames = P6SpyOptions.getActiveInstance().getDriverNames();
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
