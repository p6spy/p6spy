package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
class P6LogResultSetNextDelegate implements Delegate {
  private final ResultSetInformation resultSetInformation;

  public P6LogResultSetNextDelegate(final ResultSetInformation resultSetInformation) {
    this.resultSetInformation = resultSetInformation;
  }

  /**
   * Called by the invocation handler instead of the target method.  Since this method is called
   * instead of the target method, it is up to implementations to invoke the target method (if applicable).
   *
   * @param target The object being proxied
   * @param method The method that was invoked
   * @param args   The arguments of the method (if any).  This argument will be null if there
   *               were no arguments.
   * @return The return value of the method
   * @throws Throwable
   */
  @Override
  public Object invoke(final Object target, final Method method, final Object[] args) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object result = null;
    try {
      if (resultSetInformation.getCurrRow() > -1) {
        // only dump the data on subsequent calls to next
        resultSetInformation.generateLogMessage();
      }
      resultSetInformation.setCurrRow(resultSetInformation.getCurrRow() + 1);
      result = method.invoke(target, args);
      return result;
    } finally {
      // the result of the proxied method call will be true or false since this is used to proxy the call to ResultSet.next()
      // we do not need to log the call if the result was false as it means that there were no more results.
      if( Boolean.TRUE.equals(result) ) {
        P6LogQuery.logElapsed(resultSetInformation.getConnectionId(), startTime, "result", resultSetInformation.getPreparedQuery(), resultSetInformation.getQuery());
      }
    }
  }
}
