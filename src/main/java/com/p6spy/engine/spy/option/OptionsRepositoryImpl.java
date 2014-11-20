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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.p6spy.engine.common.ClassHasher;
import com.p6spy.engine.common.CustomHashedHashSet;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6Factory;

public class OptionsRepositoryImpl implements OptionsRepository {

  protected final Map<String, Object> map = new HashMap<String, Object>();

  private DelayedOptionChangePropagator optionChangePropagator;

  @Override
  public <T> boolean set(Class<T> type, String key, Object value) {
    if (value == null) {
      return false;
    }

    return setOrUnSet(type, key, value, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> type, String key) {
    if (((DelayedOptionChangePropagator) getOptionChangePropagator()).isDelaying()) {
      throw new IllegalStateException("Options didn't load completely, yet!");
    }

    return (T) map.get(key);
  }

  @Override
  public <T> T get(final OptionsRepository higherPrioRepository, Class<T> type, String key) {
    if (((DelayedOptionChangePropagator) getOptionChangePropagator()).isDelaying()) {
      throw new IllegalStateException("Options didn't load completely, yet!");
    }
    
    if (null != higherPrioRepository) {
      T value = higherPrioRepository.get(type, key);
      if (null != value) {
        return value;
      }
    }

    return this.get(type, key);
  }

  @Override
  public <T> boolean setOrUnSet(Class<T> type, String key, Object value, Object defaultValue) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("key can be neither null nor empty!");
    }

    if (value == null) {
      value = defaultValue;
    }

    if (value == null) {
      setInternal(key, value);
    } else {
      setInternal(key, parse(type, value));
    }

    return true;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Set<T> getSet(Class<T> type, String key) {
    return (Set<T>) map.get(key);
  }

  @Override
  public <T> Set<T> getSet(final OptionsRepository higherPrioRepository, Class<T> type, String key) {
    Set<T> higherPrioValue = higherPrioRepository.getSet(type, key);
    return null != higherPrioValue ? higherPrioValue : this.getSet(type, key);
  }

  @Override
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
    Set<T> newValue = null;

    if (collection.isEmpty()) {
      map.remove(key);
    } else {
      if (type.equals(P6Factory.class)) {
        // for P6Factories the hashcode is computed based on class
        newValue = new CustomHashedHashSet<T>(new ClassHasher());
      } else {
        newValue = new HashSet<T>();
      }

      for (String item : collection) {

        if (item.toString().startsWith("-")) {
          throw new IllegalArgumentException(
              "- prefix has been deprecated for list-like properties! Full overriding happens (see: http://p6spy.github.io/p6spy/2.0/configandusage.html)");
        }

        newValue.add((T) parse(type, item));
      }
      map.put(key, newValue);
    }

    // propagate the changes
    getOptionChangePropagator().fireOptionChanged(key, oldValue, newValue);

    return true;
  }

  @Override
  public DelayedOptionChangePropagator getOptionChangePropagator() {
    return optionChangePropagator;
  }

  @Override
  public void setOptionChangePropagator(DelayedOptionChangePropagator optionChangePropagator) {
    this.optionChangePropagator = optionChangePropagator;
  }

  //
  // internals
  //

  @SuppressWarnings("unchecked")
  <T> Object parse(Class<T> type, Object value) {
    if (type.isAssignableFrom(Boolean.class)) {
      return P6Util.isTrue(value.toString(), true);
    } else if (type.isAssignableFrom(String.class)) {
      return value.toString();
    } else if (type.isAssignableFrom(Long.class)) {
      return Long.parseLong(value.toString());
    } else if (type.isAssignableFrom(Integer.class)) {
      return Integer.parseInt(value.toString());
    } else if (type.isAssignableFrom(Set.class)) {
      throw new IllegalArgumentException("please call the setSet() method instead!");
    } else if (type.isAssignableFrom(Collection.class) || type.isAssignableFrom(List.class)) {
      throw new IllegalArgumentException("type not supported:" + type.getName());
    } else if (type.isAssignableFrom(Pattern.class)) {
      return Pattern.compile(value.toString());
    } else if (type.isAssignableFrom(Category.class)) {
      return new Category(value.toString());
    } else {
      // if (type.isEnum()) {
      // // is sufficient for our use case where toString returns enum name
      // for (T enumConstant : type.getEnumConstants()) {
      // if (enumConstant.toString().equalsIgnoreCase(value.toString())) {
      // return enumConstant;
      // }
      // }
      // }

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
        return (T) instance;
      } catch (ClassCastException e) {
        System.err.println("Value " + value + ", is not of expected type. Error: " + e);
        return null;
      }
    }
  }

  void setInternal(String key, Object value) {
    final Object oldValue = map.put(key, value);

    // propagate the changes
    getOptionChangePropagator().fireOptionChanged(key, oldValue, value);
  }

}
