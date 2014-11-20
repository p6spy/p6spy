/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2014 P6Spy
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

/**
 * Option change propagator with support for delayed option changes propagation.
 * 
 * @author peterb
 */
public interface OptionChangePropagator {

  /**
   * Fires options changed propagation.
   * 
   * @param key
   *          the name of the option
   * @param oldValue
   *          the old value of the option
   * @param newValue
   *          the new value of the option
   */
  void fireOptionChanged(final String key, final Object oldValue, final Object newValue);

  /**
   * Registers {@link OptionChangeListener} for the option change notifications.
   * 
   * @param listener
   *          the listener to register
   */
  void registerOptionChangedListener(final OptionChangeListener listener);

  /**
   * Unregisters {@link OptionChangeListener} from the option change notifications.
   * 
   * @param listener
   *          the listener to unregister
   */
  void unregisterOptionChangedListener(final OptionChangeListener listener);

}
