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
package com.p6spy.engine.logging;

import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.common.P6LoadableOptions;
import com.p6spy.engine.proxy.ProxyFactory;
import com.p6spy.engine.spy.P6Factory;

public class P6LogFactory implements P6Factory {

  @Override
  public P6LoadableOptions getOptions() {
    return new P6LogOptions();
  }

  @Override
  public Connection getConnection(Connection conn) throws SQLException {
    P6LogConnectionInvocationHandler invocationHandler = new P6LogConnectionInvocationHandler(conn);
    return ProxyFactory.createProxy(conn, Connection.class, invocationHandler);
  }

}
