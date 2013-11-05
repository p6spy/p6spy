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
package com.p6spy.engine.logging;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Connection;

/**
 * Invocation handler for {@link java.sql.Connection}
 */
public class P6LogConnectionInvocationHandler extends GenericInvocationHandler<Connection> {

  public P6LogConnectionInvocationHandler(Connection underlying) {
    super(underlying);
    ConnectionInformation connectionInformation = new ConnectionInformation();

    P6LogConnectionCommitDelegate commitDelegate = new P6LogConnectionCommitDelegate(connectionInformation);
    P6LogConnectionRollbackDelegate rollbackDelegate = new P6LogConnectionRollbackDelegate(connectionInformation);
    P6LogConnectionPrepareStatementDelegate prepareStatementDelegate = new P6LogConnectionPrepareStatementDelegate(connectionInformation);
    P6LogConnectionCreateStatementDelegate createStatementDelegate = new P6LogConnectionCreateStatementDelegate(connectionInformation);
    P6LogConnectionPrepareCallDelegate prepareCallDelegate = new P6LogConnectionPrepareCallDelegate(connectionInformation);

    // add delegates to perform logging on connection methods
    addDelegate(
        new MethodNameMatcher("commit"),
        commitDelegate
    );
    addDelegate(
        new MethodNameMatcher("rollback"),
        rollbackDelegate
    );

    // add delegates to return proxies for other methods
    addDelegate(
        new MethodNameMatcher("prepareStatement"),
        prepareStatementDelegate
    );

    addDelegate(
        new MethodNameMatcher("createStatement"),
        createStatementDelegate
    );

    addDelegate(
        new MethodNameMatcher("prepareCall"),
        prepareCallDelegate
    );

    // TODO add proxy for getDatabaseMetaData - but not used for logging module?

  }

}
