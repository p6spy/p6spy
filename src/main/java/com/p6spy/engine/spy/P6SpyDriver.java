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

package com.p6spy.engine.spy;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.wrapper.ConnectionWrapper;

/**
 * JDBC driver for P6Spy
 */
public class P6SpyDriver implements Driver {
  private static Driver INSTANCE = new P6SpyDriver();
  private static JdbcEventListenerFactory jdbcEventListenerFactory;

  static {
    try {
      DriverManager.registerDriver(P6SpyDriver.INSTANCE);
    } catch (SQLException e) {
      throw new IllegalStateException("Could not register P6SpyDriver with DriverManager", e);
    }
  }

  @Override
  public boolean acceptsURL(final String url) {
    return url != null && url.startsWith("jdbc:p6spy:");
  }

  /**
   * Parses out the real JDBC connection URL by removing "p6spy:".
   *
   * @param url the connection URL
   * @return the parsed URL
   */
  private String extractRealUrl(String url) {
    return acceptsURL(url) ? url.replace("p6spy:", "") : url;
  }

  static List<Driver> registeredDrivers() {
    List<Driver> result = new ArrayList<Driver>();
    for (Enumeration<Driver> driverEnumeration = DriverManager.getDrivers(); driverEnumeration.hasMoreElements(); ) {
      result.add(driverEnumeration.nextElement());
    }
    return result;
  }

  @Override
  public Connection connect(String url, Properties properties) throws SQLException {
    // if there is no url, we have problems
    if (url == null) {
      throw new SQLException("url is required");
    }

    if( !acceptsURL(url) ) {
      return null;
    }

    // find the real driver for the URL
    Driver passThru = findPassthru(url);

    P6LogQuery.debug("this is " + this + " and passthru is " + passThru);

    final long start = System.nanoTime();

    if (P6SpyDriver.jdbcEventListenerFactory == null) {
      P6SpyDriver.jdbcEventListenerFactory = new DefaultJdbcEventListenerFactory();
    }
    final Connection conn;
    final JdbcEventListener jdbcEventListener = P6SpyDriver.jdbcEventListenerFactory.createJdbcEventListener();
    final ConnectionInformation connectionInformation = ConnectionInformation.fromDriver(passThru);
    jdbcEventListener.onBeforeGetConnection(connectionInformation);
    try {
      conn =  passThru.connect(extractRealUrl(url), properties);
      connectionInformation.setConnection(conn);
      connectionInformation.setTimeToGetConnectionNs(System.nanoTime() - start);
      jdbcEventListener.onAfterGetConnection(connectionInformation, null);
    } catch (SQLException e) {
      connectionInformation.setTimeToGetConnectionNs(System.nanoTime() - start);
      jdbcEventListener.onAfterGetConnection(connectionInformation, e);
      throw e;
    }

    return ConnectionWrapper.wrap(conn, jdbcEventListener, connectionInformation);
  }

  protected Driver findPassthru(String url) throws SQLException {
    // registers the passthru drivers, if configured s
    P6ModuleManager.getInstance();
    
    String realUrl = extractRealUrl(url);
    Driver passthru = null;
    for (Driver driver: registeredDrivers() ) {
      try {
        if (driver.acceptsURL(extractRealUrl(url))) {
          passthru = driver;
          break;
        }
      } catch (SQLException e) {
      }
    }
    if( passthru == null ) {
      throw new SQLException("Unable to find a driver that accepts " + realUrl);
    }
    return passthru;
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties properties) throws SQLException {
    return findPassthru(url).getPropertyInfo(url, properties);
  }

  @Override
  public int getMajorVersion() {
    // This is a bit of a problem since there is no URL to determine the passthru!
    return 2;
  }

  @Override
  public int getMinorVersion() {
    // This is a bit of a problem since there is no URL to determine the passthru!
    return 0;
  }

  @Override
  public boolean jdbcCompliant() {
    // This is a bit of a problem since there is no URL to determine the passthru!
    return true;
  }

  // Note: @Override annotation not added to allow compilation using Java 1.6
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public static void setJdbcEventListenerFactory(JdbcEventListenerFactory jdbcEventListenerFactory) {
    P6SpyDriver.jdbcEventListenerFactory = jdbcEventListenerFactory;
  }
}
