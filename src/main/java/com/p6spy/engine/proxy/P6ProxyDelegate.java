package com.p6spy.engine.proxy;

import java.lang.reflect.Method;

public class P6ProxyDelegate implements Delegate {
  private final Object underlying;

  public P6ProxyDelegate(Object underlying) {
    this.underlying = underlying;
  }

  @Override
  public Object invoke(Object target, Method method, Object[] args) throws Throwable {
    return underlying;
  }
}
