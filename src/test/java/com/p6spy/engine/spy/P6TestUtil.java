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
package com.p6spy.engine.spy;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.test.P6TestOptions;


public class P6TestUtil {
  
  private static final Logger log = Logger.getLogger(P6TestUtil.class);

  public static void printAllDrivers() {
    for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
      log.info("DRIVER FOUND: " + e.nextElement());
    }
  }

  public static Connection loadDrivers(String drivername)
      throws SQLException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
    String user = P6TestOptions.getActiveInstance().getUser();
    String password = P6TestOptions.getActiveInstance().getPassword();
    String url = P6TestOptions.getActiveInstance().getUrl();

    if (drivername != null) {
      log.info("UTIL REGISTERING DRIVER == " + drivername);
      Class<Driver> driverClass = P6Util.forName(drivername);
      DriverManager.setLogWriter(new PrintWriter(System.out, true));
      DriverManager.registerDriver(driverClass.newInstance());
    }
    Driver driver = DriverManager.getDriver(url);
    log.info("UTIL USING DRIVER == " + driver.getClass().getName() + " FOR URL " + url);
    Connection connection = DriverManager.getConnection(url, user, password);
    printAllDrivers();
    return connection;
  }


}
