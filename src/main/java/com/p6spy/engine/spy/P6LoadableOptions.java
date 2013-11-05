package com.p6spy.engine.spy;

import java.util.Map;

public interface P6LoadableOptions {

  /**
   * Loads only those options, that are relevant for the current implementation.
   * 
   * @param options
   *          options to be loaded.
   */
  void load(Map<String, String> options);

  Map<String, String> getDefaults();
}
