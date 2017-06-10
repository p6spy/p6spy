
package com.p6spy.engine.spy.option;

import java.util.Map;

import com.p6spy.engine.spy.P6ModuleManager;

public interface P6OptionsSource {

  public Map<String, String> getOptions();

  /**
   * Intended for the postInit jobs. Called in the {@link P6ModuleManager#P6ModuleManager}. After
   * initialization is done.
   * 
   * @param p6moduleManager
   *          module manager instance that has been initialized.
   */
  void postInit(P6ModuleManager p6moduleManager);

  /**
   * Intended for cleanup jobs. Called in the {@link P6ModuleManager#cleanUp}.
   * 
   * @param p6moduleManager
   *          module manager instance that is about to be destroyed.
   */
  void preDestroy(P6ModuleManager p6moduleManager);
}
