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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Basic implementation of {@link DelayedOptionChangePropagator}.
 * 
 * @author peterb
 */
public class DelayedOptionChangePropagatorImpl implements DelayedOptionChangePropagator, OptionChangePropagator {

  private List<OptionChangeListener> listeners = new ArrayList<OptionChangeListener>();
  
  private Set<DelayedOptionChange> delayedOptionChanges = new HashSet<DelayedOptionChange>();
  
  /**
   * Inidicator whether initialization has been completed. To prevent usage of not yet initialized
   * properties.
   */
  private boolean initCompleted = false;
  
  @Override
  public boolean isDelaying() {
    return !initCompleted;
  }
  
  @Override
  public void fireOptionChanged(final String key, final Object oldValue, final Object newValue) {
    if (initCompleted) {
      fireDelayedOptionChanges();
      for (OptionChangeListener listener : listeners) {
        listener.optionChanged(key, oldValue, newValue);
      }
    } else {
      delayedOptionChanges.add(new DelayedOptionChange(key, oldValue, newValue));
    }
  }

  @Override
  public void registerOptionChangedListener(OptionChangeListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("P6OptionChangedListener can't be null!");
    }

    this.listeners.add(listener);
  }

  @Override
  public void unregisterOptionChangedListener(OptionChangeListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("P6OptionChangedListener can't be null!");
    }

    this.listeners.remove(listener);
  }

  @Override
  public synchronized void fireDelayedOptionChanges() {
    this.initCompleted = true;
    
    if (null == delayedOptionChanges) {
      return;
    }

    for (DelayedOptionChange delayedOption : delayedOptionChanges) {
      for (OptionChangeListener listener : listeners) {
        listener.optionChanged(delayedOption.getKey(), delayedOption.getOldValue(),
            delayedOption.getNewValue());
      }
    }

    // make sure the delayed options get cleared to continue normal processing
    delayedOptionChanges = null;
  }

  class DelayedOptionChange {

    private final String key;
    private final Object oldValue;
    private final Object newValue;

    public DelayedOptionChange(String key, Object oldValue, Object newValue) {
      super();

      if (null == key || key.isEmpty()) {
        throw new IllegalArgumentException("key can be neither null nor empty!");
      }

      this.key = key;
      this.oldValue = oldValue;
      this.newValue = newValue;
    }

    @Override
    public int hashCode() {
      // this is important part!
      return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      DelayedOptionChange other = (DelayedOptionChange) obj;
      if (!getOuterType().equals(other.getOuterType()))
        return false;
      if (key == null) {
        if (other.key != null)
          return false;
      } else if (!key.equals(other.key))
        return false;
      return true;
    }

    public String getKey() {
      return key;
    }

    public Object getOldValue() {
      return oldValue;
    }

    public Object getNewValue() {
      return newValue;
    }

    private DelayedOptionChangePropagatorImpl getOuterType() {
      return DelayedOptionChangePropagatorImpl.this;
    }
  }

}
