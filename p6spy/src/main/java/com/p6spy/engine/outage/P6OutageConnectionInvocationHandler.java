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

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Connection;

/**
 * Invocation handler for {@link java.sql.Connection}
 */
public class P6OutageConnectionInvocationHandler extends GenericInvocationHandler<Connection> {

  public P6OutageConnectionInvocationHandler(Connection underlying) {
    super(underlying);
    ConnectionInformation connectionInformation = new ConnectionInformation();

    P6OutageConnectionCommitDelegate commitDelegate = new P6OutageConnectionCommitDelegate(connectionInformation);
    P6OutageConnectionRollbackDelegate rollbackDelegate = new P6OutageConnectionRollbackDelegate(connectionInformation);
    P6OutageConnectionCreateStatementDelegate createStatementDelegate = new P6OutageConnectionCreateStatementDelegate(connectionInformation);
    P6OutageConnectionPrepareStatementDelegate prepareStatementDelegate = new P6OutageConnectionPrepareStatementDelegate(connectionInformation);
    P6OutageConnectionPrepareCallDelegate prepareCallDelegate = new P6OutageConnectionPrepareCallDelegate(connectionInformation);

    addDelegate(
        new MethodNameMatcher("commit"),
        commitDelegate
    );
    addDelegate(
        new MethodNameMatcher("rollback"),
        rollbackDelegate
    );

    addDelegate(
        new MethodNameMatcher("createStatement"),
        createStatementDelegate
    );

    addDelegate(
        new MethodNameMatcher("prepareStatement"),
        prepareStatementDelegate
    );

    addDelegate(
        new MethodNameMatcher("prepareCall"),
        prepareCallDelegate
    );

  }

}
