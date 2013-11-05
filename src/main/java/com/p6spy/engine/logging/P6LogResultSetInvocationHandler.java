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

import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameAndParameterLikeMatcher;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
class P6LogResultSetInvocationHandler extends GenericInvocationHandler<ResultSet> {

  /**
   * Creates a new invocation handler for the given object.
   *
   * @param underlying The object being proxied
   */
  public P6LogResultSetInvocationHandler(final ResultSet underlying, final StatementInformation statementInformation)
      throws SQLException {
    super(underlying);

    ResultSetInformation resultSetInformation = new ResultSetInformation(statementInformation);
    P6LogResultSetNextDelegate nextDelegate = new P6LogResultSetNextDelegate(resultSetInformation);
    P6LogResultSetGetColumnValueDelegate getColumnValueDelegate = new P6LogResultSetGetColumnValueDelegate(resultSetInformation);

    addDelegate(
        new MethodNameMatcher("next"),
        nextDelegate
    );

    // TODO: create proxy for Array object returned from getArray()?

    // add delegates for the basic getXXXX(int) and getXXXX(String) methods
    addDelegate(
        new MethodNameAndParameterLikeMatcher("get*", int.class),
        getColumnValueDelegate
    );
    addDelegate(
        new MethodNameAndParameterLikeMatcher("get*", String.class),
        getColumnValueDelegate
    );
  }
}
