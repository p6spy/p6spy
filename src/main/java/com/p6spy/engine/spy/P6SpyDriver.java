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

import com.p6spy.engine.common.P6LogQuery;

/**
 * JDBC driver for P6Spy.
 */
public class P6SpyDriver implements Driver {
  private static Driver INSTANCE = new P6SpyDriver();

  static {
    try {
      DriverManager.registerDriver(INSTANCE);
    } catch (SQLException e) {
      throw new IllegalStateException("Could not register P6SpyDriver with DriverManager", e);
    }
  }

  @Override
  public boolean acceptsURL(final String url) throws SQLException {
    return P6JdbcUrlFactory.getP6JdbcUrl(url).isAccepted();
  }

  public P6SpyDriver() {
    P6Core.initialize();
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
    
    P6JdbcUrl p6JdbcUrl = P6JdbcUrlFactory.getP6JdbcUrl(url); 

    if( !p6JdbcUrl.isAccepted() ) {
      return null;
    }

    // find the real driver for the URL
    Driver passThru = findPassthru(p6JdbcUrl);

    P6LogQuery.debug("this is " + this + " and passthru is " + passThru);

    Connection conn = passThru.connect(p6JdbcUrl.getProxiedUrl(), properties);

    if (conn != null) {
      conn = P6Core.wrapConnection(conn, p6JdbcUrl.getOptionsRepository());
    }
    return conn;
  }

  protected Driver findPassthru(P6JdbcUrl p6JdbcUrl) throws SQLException {
    String realUrl = p6JdbcUrl.getProxiedUrl();
    Driver passthru = null;
    for (Driver driver: registeredDrivers() ) {
      try {
        if (driver.acceptsURL(p6JdbcUrl.getProxiedUrl())) {
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
    return findPassthru(P6JdbcUrlFactory.getP6JdbcUrl(url)).getPropertyInfo(url, properties);
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
}
