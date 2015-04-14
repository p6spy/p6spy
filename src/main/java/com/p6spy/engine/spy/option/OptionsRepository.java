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

import java.util.Set;

/**
 * Repository of the P6Spy options.
 * 
 * @author peterb
 */
public interface OptionsRepository {

  <T> T get(Class<T> type, String key);

  <T> T get(final OptionsRepository higherPrioRepository, Class<T> type, String key);

  <T> boolean set(Class<T> type, String key, Object value);

  <T> boolean setOrUnSet(Class<T> type, String key, Object value, Object defaultValue);

  <T> Set<T> getSet(Class<T> type, String key);
  
  <T> Set<T> getSet(final OptionsRepository higherPrioRepository, Class<T> type, String key);

  <T> boolean setSet(Class<T> type, String key, String csv);

  DelayedOptionChangePropagator getOptionChangePropagator();

  void setOptionChangePropagator(DelayedOptionChangePropagator optionChangePropagator);

}