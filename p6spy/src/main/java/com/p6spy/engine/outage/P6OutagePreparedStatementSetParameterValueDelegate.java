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

import com.p6spy.engine.common.P6SpyOptions;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

class P6OutagePreparedStatementSetParameterValueDelegate implements Delegate {
  private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
  private final PreparedStatementInformation preparedStatementInformation;

  public P6OutagePreparedStatementSetParameterValueDelegate(PreparedStatementInformation preparedStatementInformation) {
    this.preparedStatementInformation = preparedStatementInformation;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    int position = (Integer) args[0];
    Object value = null;
    if( !method.getName().equals("setNull")) {
      value = args[1];
    }
    preparedStatementInformation.setParameterValue(position, convertToString(value));
    return method.invoke(target, args);
  }

  private String convertToString(Object o) {
    if (o instanceof java.util.Date) {
      return new SimpleDateFormat(P6SpyOptions.getDatabaseDialectDateFormat()).format(o);
    } else if (o instanceof byte[]) {
      return toHexString((byte[]) o);
    } else {
      return (o == null) ? null : o.toString();
    }
  }

  private String toHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      int temp = (int) b & 0xFF;
      sb.append(HEX_CHARS[temp / 16]);
      sb.append(HEX_CHARS[temp % 16]);
    }
    return sb.toString();
  }

}
