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
package com.p6spy.engine.spy;

import com.p6spy.engine.common.P6LogQuery;

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

/**
 * JDBC driver for P6Spy
 */
public class P6SpyDriver implements Driver {
  private static Driver INSTANCE = new P6SpyDriver();

  /*

     TODO - Replace this class with proxies

     There is really no reason for this class to exist.  Proxies could be created for each driver registered with the
     driver manager.  The only issue is when this would happen...

     Using proxies would solve the problem exposed by getMajorVersion() and other driver methods which do not accept
     a URL as a parameter.
   */


  static {
    try {
      DriverManager.registerDriver(INSTANCE);
    } catch (SQLException e) {
      // TODO log this somewhere?
    }
  }


  /**
   * for some reason the passthru is null, go create one
   */
  @Override
  public boolean acceptsURL(final String url) throws SQLException {
    if (url != null && url.startsWith("jdbc:p6spy:")) {
      // yes, we accept this URL but only is there is another driver which accepts the real URL.

      return true;
    } else {
      return false;
    }
  }

  public P6SpyDriver() {
    P6Core.initialize();
  }

  /**
   * Parses out the real JDBC connection URL by removing "p6spy:".
   *
   * @param url the connection URL
   * @return the parsed URL
   */
  private String extractRealUrl(String url) {
    return url.startsWith("jdbc:p6spy:") ? url.replace("p6spy:", "") : url;
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

    // find the real driver for the URL
    Driver passThru = findPassthru(url);

    P6LogQuery.debug("this is " + this + " and passthru is " + passThru);

    Connection conn = passThru.connect(extractRealUrl(url), properties);

    if (conn != null) {
      conn = P6Core.wrapConnection(conn);
    }
    return conn;
  }

  protected Driver findPassthru(String url) throws SQLException {
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
}
