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
 * {@link OptionChangePropagator} supporting delayed option changes propagation. <br/>
 * <br/>
 * Implementation should postpone all the option changed event propagation, until
 * {@link DelayedOptionChangePropagator#fireDelayedOptionChanges()} call happens. <br/>
 * <br/>
 * This prevents inconsistent values propagation. Imagine the case: {@code option.key=key1} with the
 * value transition: {@code option.value=value1 -> option.value=value2 -> option.value=value3}, so
 * the {@link DelayedOptionChangePropagator} should guarantee, that only:
 * {@code option.value=value1 ->
 * option.value=value3} gets propagated (until {@link #fireDelayedOptionChanges()} is triggered).
 * 
 * 
 * @author peterb
 */
public interface DelayedOptionChangePropagator extends OptionChangePropagator {

  /**
   * Fires options changed propagation of the delayed options.
   */
  void fireDelayedOptionChanges();

  /**
   * @return {@code true} if option changes are getting delayed. Otherwise returns {@code false}.
   */
  boolean isDelaying();
}
