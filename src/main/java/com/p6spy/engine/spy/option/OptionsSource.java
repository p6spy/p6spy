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
package com.p6spy.engine.spy.option;

import java.util.Map;

import com.p6spy.engine.spy.P6ModuleManager;

/**
 * Source of the options usable for p6spy configuration.
 * 
 * @author peterb
 */
public interface OptionsSource {

  /**
   * @return the options loaded from source.
   */
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
