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
package com.p6spy.engine.outage;

import com.p6spy.engine.common.P6Options;
import com.p6spy.engine.proxy.ProxyFactory;
import com.p6spy.engine.spy.P6CoreFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class P6OutageFactory extends P6CoreFactory {

  public Connection getConnection(Connection conn) throws SQLException {
    P6OutageConnectionInvocationHandler invocationHandler = new P6OutageConnectionInvocationHandler(conn);
    return ProxyFactory.createProxy(conn, Connection.class, invocationHandler);
  }

  public P6Options getOptions() throws SQLException {
    return (new P6OutageOptions());
  }

}
