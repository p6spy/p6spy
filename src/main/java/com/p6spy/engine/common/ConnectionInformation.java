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

package com.p6spy.engine.common;

import java.sql.Connection;
import java.sql.Driver;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.CommonDataSource;
import javax.sql.PooledConnection;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class ConnectionInformation implements Loggable {

  private static final AtomicInteger counter = new AtomicInteger(0);
  private final int connectionId;
  private CommonDataSource dataSource;
  private Driver driver;
  private Connection connection;
  private PooledConnection pooledConnection;
  private long timeToGetConnectionNs;

  private ConnectionInformation() {
    this.connectionId = counter.getAndIncrement();
  }

  /**
   * Creates a new {@link ConnectionInformation} instance for a {@link Connection} which has been obtained via a
   * {@link Driver}
   *
   * @param driver                the {@link Driver} which created the {@link #connection}
   * @param connection            the {@link #connection} created by the {@link #driver}
   * @param timeToGetConnectionNs the time it took to obtain the connection in nanoseconds
   * @return a new {@link ConnectionInformation} instance
   */
  public static ConnectionInformation fromDriver(Driver driver, Connection connection, long timeToGetConnectionNs) {
    final ConnectionInformation connectionInformation = new ConnectionInformation();
    connectionInformation.driver = driver;
    connectionInformation.connection = connection;
    connectionInformation.timeToGetConnectionNs = timeToGetConnectionNs;
    return connectionInformation;
  }

  /**
   * Creates a new {@link ConnectionInformation} instance for a {@link Connection} which has been obtained via a
   * {@link CommonDataSource}
   *
   * @param dataSource            the {@link javax.sql.CommonDataSource} which created the {@link #connection}
   * @param connection            the {@link #connection} created by the {@link #dataSource}
   * @param timeToGetConnectionNs the time it took to obtain the connection in nanoseconds
   * @return a new {@link ConnectionInformation} instance
   */
  public static ConnectionInformation fromDataSource(CommonDataSource dataSource, Connection connection, long timeToGetConnectionNs) {
    final ConnectionInformation connectionInformation = new ConnectionInformation();
    connectionInformation.dataSource = dataSource;
    connectionInformation.connection = connection;
    connectionInformation.timeToGetConnectionNs = timeToGetConnectionNs;
    return connectionInformation;
  }

  /**
   * Creates a new {@link ConnectionInformation} instance for a {@link Connection} which has been obtained via a
   * {@link PooledConnection}
   *
   * @param pooledConnection      the {@link PooledConnection} which created the {@link #connection}
   * @param connection            the {@link #connection} created by the {@link #pooledConnection}
   * @param timeToGetConnectionNs the time it took to obtain the connection in nanoseconds
   * @return a new {@link ConnectionInformation} instance
   */
  public static ConnectionInformation fromPooledConnection(PooledConnection pooledConnection, Connection connection, long timeToGetConnectionNs) {
    final ConnectionInformation connectionInformation = new ConnectionInformation();
    connectionInformation.pooledConnection = pooledConnection;
    connectionInformation.connection = connection;
    connectionInformation.timeToGetConnectionNs = timeToGetConnectionNs;
    return connectionInformation;
  }

  /**
   * Creates a new {@link ConnectionInformation} instance for a {@link Connection} which will be obtained via a
   * {@link Driver}
   *
   * @param driver                the {@link Driver} which created the {@link #connection}
   * @return a new {@link ConnectionInformation} instance
   */
  public static ConnectionInformation fromDriver(Driver driver) {
    final ConnectionInformation connectionInformation = new ConnectionInformation();
    connectionInformation.driver = driver;
    return connectionInformation;
  }

  /**
   * Creates a new {@link ConnectionInformation} instance for a {@link Connection} which will be obtained via a
   * {@link CommonDataSource}
   *
   * @param dataSource            the {@link javax.sql.CommonDataSource} which created the {@link #connection}
   * @return a new {@link ConnectionInformation} instance
   */
  public static ConnectionInformation fromDataSource(CommonDataSource dataSource) {
    final ConnectionInformation connectionInformation = new ConnectionInformation();
    connectionInformation.dataSource = dataSource;
    return connectionInformation;
  }

  /**
   * Creates a new {@link ConnectionInformation} instance for a {@link Connection} which will be obtained via a
   * {@link PooledConnection}
   *
   * @param pooledConnection      the {@link PooledConnection} which created the {@link #connection}
   * @return a new {@link ConnectionInformation} instance
   */
  public static ConnectionInformation fromPooledConnection(PooledConnection pooledConnection) {
    final ConnectionInformation connectionInformation = new ConnectionInformation();
    connectionInformation.pooledConnection = pooledConnection;
    return connectionInformation;
  }

  /**
   * This method should only be used in test scenarios
   *
   * @param connection the underlying connection (possibly a mock)
   * @return a new {@link ConnectionInformation} instance
   */
  public static ConnectionInformation fromTestConnection(Connection connection) {
    final ConnectionInformation connectionInformation = new ConnectionInformation();
    connectionInformation.connection = connection;
    return connectionInformation;
  }

  public int getConnectionId() {
    return connectionId;
  }

  @Override
  public String getSql() {
    return "";
  }

  @Override
  public String getSqlWithValues() {
    return "";
  }

  /**
   * Returns the {@link #dataSource} which created the {@link #connection}
   * or <code>null</code> if it wasn't created via a {@link CommonDataSource}.
   *
   * @return the {@link #dataSource} which created the {@link #connection}
   */
  public CommonDataSource getDataSource() {
    return dataSource;
  }

  /**
   * Returns the {@link #driver} which created the {@link #connection}
   * or <code>null</code> if it wasn't created via a {@link Driver}.
   *
   * @return the {@link #driver} which created the {@link #connection}
   */
  public Driver getDriver() {
    return driver;
  }

  /**
   * Returns a reference to the {@link Connection}
   *
   * @return a reference to the {@link Connection}
   */
  public Connection getConnection() {
    return connection;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  /**
   * Returns the {@link #pooledConnection} which created the {@link #connection}
   * or <code>null</code> if it wasn't created via a {@link PooledConnection}.
   *
   * @return the {@link #pooledConnection} which created the {@link #connection}
   */
  public PooledConnection getPooledConnection() {
    return pooledConnection;
  }

  /**
   * Returns the time it took to obtain the connection in nanoseconds
   *
   * @return the time it took to obtain the connection in nanoseconds
   */
  public long getTimeToGetConnectionNs() {
    return timeToGetConnectionNs;
  }

  public void setTimeToGetConnectionNs(long timeToGetConnectionNs) {
    this.timeToGetConnectionNs = timeToGetConnectionNs;
  }

  /** {@inheritDoc} */
  @Override
  public ConnectionInformation getConnectionInformation() {
    return this;
  }
}
