package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
class P6LogResultSetGetColumnValueDelegate implements Delegate {


  private final ResultSetInformation resultSetInformation;

  public P6LogResultSetGetColumnValueDelegate(final ResultSetInformation resultSetInformation) {
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
    // the first argument will always be the column index or the column name
    String columnName = String.valueOf(args[0]);
    Object result;
    try {
      result = method.invoke(target, args);
      resultSetInformation.setColumnValue(columnName, result);
      return result;
    } finally {
      P6LogQuery.logElapsed(resultSetInformation.getConnectionId(), startTime, "result", resultSetInformation.getPreparedQuery(), resultSetInformation.getQuery());
    }
  }
}
