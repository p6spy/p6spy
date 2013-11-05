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
package com.p6spy.engine.leak;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Connection;

/**
 * Invocation handler for {@link java.sql.Connection}
 */
public class P6LeakConnectionInvocationHandler extends GenericInvocationHandler<Connection> {

  public P6LeakConnectionInvocationHandler(Connection underlying) {
    super(underlying);
    ConnectionInformation connectionInformation = new ConnectionInformation();
    P6Objects.open(connectionInformation);

    P6LeakConnectionCloseDelegate closeDelegate = new P6LeakConnectionCloseDelegate(connectionInformation);
    P6LeakConnectionCreateStatementDelegate createStatementDelegate = new P6LeakConnectionCreateStatementDelegate(connectionInformation);

    addDelegate(
        new MethodNameMatcher("closeDelegate"),
        closeDelegate
    );

    addDelegate(
        new MethodNameMatcher("prepareStatement"),
        createStatementDelegate
    );

    addDelegate(
        new MethodNameMatcher("createStatement"),
        createStatementDelegate
    );

    addDelegate(
        new MethodNameMatcher("prepareCall"),
        createStatementDelegate
    );

  }

}
