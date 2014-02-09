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

import java.sql.SQLException;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

// Does it make sense to implement ConnectionPoolDataSource and XADataSource in one class?
// it makes our life simpler (with btm testing), and seems we're not the only ones, see: org.h2.jdbcx.JdbcDataSource
@SuppressWarnings("serial")
public class P6ConnectionPoolDataSource extends P6DataSource implements ConnectionPoolDataSource, XADataSource {

  public P6ConnectionPoolDataSource() {
    super();
  }

  public P6ConnectionPoolDataSource(DataSource ds) {
    super(ds);
  }

  @Override
  public PooledConnection getPooledConnection() throws SQLException {
    if (rds == null) {
      bindDataSource();
    }

    PooledConnection pc = ((ConnectionPoolDataSource) rds).getPooledConnection();
    return new P6PooledConnection(pc);
  }

  @Override
  public PooledConnection getPooledConnection(String user, String password) throws SQLException {
    if (rds == null) {
      bindDataSource();
    }

    PooledConnection pc = ((ConnectionPoolDataSource) rds).getPooledConnection(user, password);
    return new P6PooledConnection(pc);
  }

  @Override
  public XAConnection getXAConnection() throws SQLException {
    if (rds == null) {
      bindDataSource();
    }
    
    if (rds instanceof XADataSource) {
      return new P6XAConnection(((XADataSource) rds).getXAConnection());  
    }
    
    throw new IllegalStateException("realdatasource type not supported: " + rds);
  }

  @Override
  public XAConnection getXAConnection(String user, String password) throws SQLException {
    if (rds == null) {
      bindDataSource();
    }
    
    if (rds instanceof XADataSource) {
      return new P6XAConnection(((XADataSource) rds).getXAConnection(user, password));  
    }
    
    throw new IllegalStateException("realdatasource type not supported: " + rds);
  }

}
