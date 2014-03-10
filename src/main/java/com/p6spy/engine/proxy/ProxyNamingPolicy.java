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
package com.p6spy.engine.proxy;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

/**
 * Naming policy for CGLIB generated classes.
 */
public class ProxyNamingPolicy extends DefaultNamingPolicy {
  public static NamingPolicy INSTANCE = new ProxyNamingPolicy();

  @Override
  public String getClassName(String prefix, String source, Object key, Predicate names) {
    // Prefix the package name with org.p6spy to avoid problems with using signed jars
    // see https://github.com/p6spy/p6spy/issues/200
    return "org.p6spy."+super.getClassName(prefix, source, key, names);
  }
}
