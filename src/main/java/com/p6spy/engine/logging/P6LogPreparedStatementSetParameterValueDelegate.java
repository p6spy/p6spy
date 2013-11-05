package com.p6spy.engine.logging;

import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

class P6LogPreparedStatementSetParameterValueDelegate implements Delegate {
  private final PreparedStatementInformation preparedStatementInformation;

  public P6LogPreparedStatementSetParameterValueDelegate(PreparedStatementInformation preparedStatementInformation) {
    this.preparedStatementInformation = preparedStatementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    int position = (Integer) args[0];
    Object value = null;
    if( !method.getName().equals("setNull")) {
      value = args[1];
    }
    preparedStatementInformation.setParameterValue(position, value);
    return method.invoke(target, args);
  }


}
