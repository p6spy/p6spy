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
package com.p6spy.engine.proxy;

import com.p6spy.engine.test.BaseTestCase;
import net.sf.cglib.proxy.Factory;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProxyFactoryTest extends BaseTestCase {
  @Test
  public void testCreateProxy() throws Exception {
    Set set = new HashSet();
    Set proxy = ProxyFactory.createProxy(set, new GenericInvocationHandler<Set>(set));

    assertTrue( proxy instanceof Factory);
    assertTrue(((Factory)proxy).getCallback(0) instanceof  GenericInvocationHandler);

  }

  @Test
  public void testIsProxyClass() throws Exception {
    Set set = new HashSet();
    Set proxy = ProxyFactory.createProxy(set, new GenericInvocationHandler<Set>(set));


    assertTrue(ProxyFactory.isProxy(proxy.getClass()));
    assertFalse(ProxyFactory.isProxy(set.getClass()));

    // a null class should return false
    assertFalse(ProxyFactory.isProxy((Object) null));
  }

  @Test
  public void testIsProxyObject() throws Exception {
    Set set = new HashSet();
    Set proxy = ProxyFactory.createProxy(set, new GenericInvocationHandler<Set>(set));

    assertTrue(ProxyFactory.isProxy(proxy));
    assertFalse(ProxyFactory.isProxy(set));

    // a null value should return false
    assertFalse(ProxyFactory.isProxy(null));

  }


}
