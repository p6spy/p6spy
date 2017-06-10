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

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Simple {@link DataSource} implementation that simply delegates to {@link DriverManager}.
 */
public class P6DriverManagerDataSource implements DataSource {
  protected DataSource rds;
  protected String url;
  protected String user;
  protected String password;

  public String getPassword() {
    return password;
  }

  public void setPassword(String inVar) {
    password = inVar;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String inVar) {
    user = inVar;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String inVar) {
    url = inVar;
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return DriverManager.getLoginTimeout();
  }

  @Override
  public void setLoginTimeout(int inVar) throws SQLException {
    DriverManager.setLoginTimeout(inVar);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return DriverManager.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter inVar) throws SQLException {
    DriverManager.setLogWriter(inVar);
  }

  @Override
  public Connection getConnection() throws SQLException {
    return getConnection(url, user, password);
  }

  @Override
  public Connection getConnection(String p0, String p1) throws SQLException {
    return getConnection(url, p0, p1);
  }

  private Connection getConnection(String p0, String p1, String p2) throws SQLException {
    return DriverManager.getConnection(p0, p1, p2);
  }

  /**
   * @param iface
   * @return
   * @throws SQLException
   * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return rds.isWrapperFor(iface);
  }

  /**
   * @param <T>
   * @param iface
   * @return
   * @throws SQLException
   * @see java.sql.Wrapper#unwrap(java.lang.Class)
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return rds.unwrap(iface);
  }

  // since 1.7
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return rds.getParentLogger();
  }
}
