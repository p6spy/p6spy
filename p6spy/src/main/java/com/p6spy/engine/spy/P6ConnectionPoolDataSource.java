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

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import java.sql.SQLException;

public class P6ConnectionPoolDataSource extends P6DataSource implements ConnectionPoolDataSource {

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
    P6PooledConnection pooledConnection = new P6PooledConnection(pc);
    return pooledConnection;
  }

  @Override
  public PooledConnection getPooledConnection(String user, String password) throws SQLException {
    if (rds == null) {
      bindDataSource();
    }

    PooledConnection pc = ((ConnectionPoolDataSource) rds).getPooledConnection(user, password);
    P6PooledConnection pooledConnection = new P6PooledConnection(pc);
    return pooledConnection;
  }

}
