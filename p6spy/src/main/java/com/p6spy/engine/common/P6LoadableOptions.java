package com.p6spy.engine.common;

import java.util.Properties;

public interface P6LoadableOptions {

  /**
   * Loads only those properties, that are relevant for the current implementation.
   * @param props
   */
  public void load(Properties props);
  
  /**
   * Returns default properties.
   * These are to be used, when no specific ones are provided for the run.
   * @return
   */
  public Properties getDefaultPropeties();
}
