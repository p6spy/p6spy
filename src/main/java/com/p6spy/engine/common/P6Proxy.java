package com.p6spy.engine.common;

public interface P6Proxy {

  /**
   * Returns the underlying object which is being proxied.
   *
   * @return the underlying object
   */
  Object getUnderlying();
}
