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

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;


public class P6TestUtil {
  private static final Logger log = Logger.getLogger(P6TestUtil.class);

  
  public static int queryForInt(Connection con, String sql) throws SQLException {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = con.createStatement();
      rs = stmt.executeQuery(sql);
      rs.next();
      return rs.getInt(1);
    } finally {
      if( rs != null ) try {rs.close();} catch(Exception e) {}
      if( stmt != null) try {stmt.close();} catch(Exception e) {}
    }
  }

  public static void execute(Connection con, String sql) throws SQLException {
    Statement stmt = null;
    try {
      stmt = con.createStatement();
      stmt.execute(sql);
    } finally {
      if( stmt != null) try {stmt.close();} catch(Exception e) {}
    }
  }
  
  public static void setupTestData(final String url, final String username, final String password)
      throws LiquibaseException {

    // setup database for testing
    String nativeUrl = url;
    if( url.startsWith("jdbc:p6spy:") ) {
      nativeUrl = url.replace("jdbc:p6spy:", "jdbc:");
    }

    setupTestData(new DriverManagerDataSource(nativeUrl, username, password));
  }

  public static void setupTestData(final DataSource dataSource) throws LiquibaseException {
    log.info("Setting up database for testing");
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setChangeLog("classpath:liquibase.xml");
    liquibase.setDataSource(dataSource);
    liquibase.setResourceLoader(new DefaultResourceLoader());
    liquibase.setDropFirst(true);
    liquibase.afterPropertiesSet();
  }


  public static void printAllDrivers() {
    if (log.isDebugEnabled()) {
      for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
        log.debug("1 DRIVER FOUND == " + e.nextElement());
      }
    }
  }

  public static int executeUpdate(Connection connection, String query) throws SQLException {
    Statement stmt = null;
    try {
      stmt = connection.createStatement();
      return stmt.executeUpdate(query);
    } finally {
      if( stmt != null) try {stmt.close();} catch(Exception e) {}
    }
  }
}
