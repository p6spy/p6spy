/*
 *
 * ====================================================================
 *
 * The P6Spy Software License, Version 1.1
 *
 * This license is derived and fully compatible with the Apache Software
 * license, see http://www.apache.org/LICENSE.txt
 *
 * Copyright (c) 2001-2002 Andy Martin, Ph.D. and Jeff Goke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement:
 * "The original concept and code base for P6Spy was conceived
 * and developed by Andy Martin, Ph.D. who generously contribued
 * the first complete release to the public under this license.
 * This product was due to the pioneering work of Andy
 * that began in December of 1995 developing applications that could
 * seamlessly be deployed with minimal effort but with dramatic results.
 * This code is maintained and extended by Jeff Goke and with the ideas
 * and contributions of other P6Spy contributors.
 * (http://www.p6spy.com)"
 * Alternately, this acknowlegement may appear in the software itself,
 * if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "P6Spy", "Jeff Goke", and "Andy Martin" must not be used
 * to endorse or promote products derived from this software without
 * prior written permission. For written permission, please contact
 * license@p6spy.com.
 *
 * 5. Products derived from this software may not be called "P6Spy"
 * nor may "P6Spy" appear in their names without prior written
 * permission of Jeff Goke and Andy Martin.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package com.p6spy.engine.spy.option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6Util;

public class P6OptionsRepository {

  public static final String COLLECTION_REMOVAL_PREFIX = "-";

  private final Map<String, Object> map = new HashMap<String, Object>();

  private Set<DelayedOptionChange> delayedOptionChanges = new HashSet<DelayedOptionChange>();

  private List<P6OptionChangedListener> listeners = new ArrayList<P6OptionChangedListener>(
      Arrays.asList(new P6LogQuery()));

  /**
   * Inidicator whether initialization has been completed. To prevent usage of not yet initialized
   * properties.
   */
  private boolean initCompleted = false;

  public void initCompleted() {
    this.initCompleted = true;
    fireDelayedOptionChanges();
  }

  public <T> boolean set(Class<T> type, String key, Object value) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("key can be neither null nor empty!");
    }

    if (value == null) {
      return false;
    }

    setInternal(key, parse(type, value));

    return true;
  }

  <T> Object parse(Class<T> type, Object value) {
    if (type.isAssignableFrom(Boolean.class)) {
      return P6Util.isTrue(value.toString(), true);
    } else if (type.isAssignableFrom(String.class)) {
      return value.toString();
    } else if (type.isAssignableFrom(Long.class)) {
      return Long.parseLong(value.toString());
    } else if (type.isAssignableFrom(Integer.class)) {
      return Integer.parseInt(value.toString());
    } else if (type.isAssignableFrom(Collection.class)) {
      throw new IllegalArgumentException("please call the setSet() method instead!");
    } else {
      Object instance;
      try {
        instance = P6Util.forName(value.toString()).newInstance();
      } catch (Exception ex) {
        // try one more hack to load the thing
        try {
          ClassLoader loader = ClassLoader.getSystemClassLoader();
          instance = loader.loadClass(value.toString()).newInstance();
        } catch (Exception e) {
          System.err.println("Cannot instantiate " + value + ", even on second attempt. ");
          e.printStackTrace(System.err);
          return null;
        }
      }

      try {
        T typedInstance = (T) instance;
        return typedInstance;
      } catch (ClassCastException e) {
        System.err.println("Value " + value + ", is not of expected type. Error: " + e);
        return null;
      }
    }
  }

  void setInternal(String key, Object value) {
    final Object oldValue = map.put(key, value);

    // propagate the changes
    fireOptionChanged(key, oldValue, value);
  }

  @SuppressWarnings("unchecked")
  public <T> boolean setSet(Class<T> type, String key, String csv) {
    if (csv == null) {
      return false;
    }

    final List<String> collection = P6Util.parseCSVList(csv);

    if (collection == null) {
      return false;
    }

    final Set<T> oldValue = getSet(type, key);

    Set<T> newValue;
    if (oldValue == null) {
      newValue = new HashSet<T>();
    } else {
      newValue = new HashSet<T>(oldValue);
    }

    for (String item : collection) {
      if (item.startsWith(COLLECTION_REMOVAL_PREFIX)) {
        newValue.remove((T) parse(type, item.substring(COLLECTION_REMOVAL_PREFIX.length())));
      } else {
        newValue.add((T) parse(type, item));
      }
    }
    // Set<T> newValue = new HashSet<T>();
    // for (String item : collection) {
    // newValue.add((T) parse(type, item));
    // }

    map.put(key, newValue);

    // propagate the changes
    fireOptionChanged(key, oldValue, newValue);

    return true;
  }

  void fireOptionChanged(final String key, final Object oldValue, final Object newValue) {
    if (initCompleted) {
      fireDelayedOptionChanges();
      for (P6OptionChangedListener listener : listeners) {
        listener.optionChanged(key, oldValue, newValue);
      }
    } else {
      delayedOptionChanges.add(new DelayedOptionChange(key, oldValue, newValue));
    }
  }

  private synchronized void fireDelayedOptionChanges() {
    if (null == delayedOptionChanges) {
      return;
    }

    for (DelayedOptionChange delayedOption : delayedOptionChanges) {
      for (P6OptionChangedListener listener : listeners) {
        listener.optionChanged(delayedOption.getKey(), delayedOption.getOldValue(),
            delayedOption.getNewValue());
      }
    }

    // make sure the delayed options get cleared to continue normal processing
    delayedOptionChanges = null;
  }

  public <T> T get(Class<T> type, String key) {
    if (!initCompleted) {
      throw new IllegalStateException("Options didn't load completely, yet!");
    }

    return (T) map.get(key);
  }

  public <T> Set<T> getSet(Class<T> type, String key) {
    // if (!initCompleted) {
    // throw new IllegalStateException("Options didn't load completely, yet!");
    // }

    return (Set<T>) map.get(key);
  }

  public void registerOptionChangedListener(P6OptionChangedListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("P6OptionChangedListener can't be null!");
    }

    this.listeners.add(listener);
  }

  public void unregisterOptionChangedListener(P6OptionChangedListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("P6OptionChangedListener can't be null!");
    }

    this.listeners.remove(listener);
  }

  class DelayedOptionChange {

    private final String key;
    private final Object oldValue;
    private final Object newValue;

    public DelayedOptionChange(String key, Object oldValue, Object newValue) {
      super();
      this.key = key;
      this.oldValue = oldValue;
      this.newValue = newValue;
    }

    @Override
    public int hashCode() {
      // this is important part!
      return key.hashCode();
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
  }
}
