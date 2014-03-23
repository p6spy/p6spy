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
package com.p6spy.engine.proxy.cache;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.p6spy.engine.proxy.GenericInvocationHandler;

public class MethodMatcherCacheKeyTest {

  @Test
  public void testEquals() throws NoSuchMethodException, SecurityException {
    final Method method1 = TestClass.class.getMethod("method1");
    final Method method2 = TestClass.class.getMethod("method2");

    Assert.assertEquals(new MethodMatcherCacheKey(InvocationHandler1.class, method1),
        new MethodMatcherCacheKey(InvocationHandler1.class, method1));
    Assert.assertNotEquals(new MethodMatcherCacheKey(InvocationHandler1.class, method1),
        new MethodMatcherCacheKey(InvocationHandler2.class, method1));
    Assert.assertNotEquals(new MethodMatcherCacheKey(InvocationHandler1.class, method1),
        new MethodMatcherCacheKey(InvocationHandler1.class, method2));
  }

  @Test
  public void testHashCode() throws NoSuchMethodException, SecurityException {
    final Method method1 = TestClass.class.getMethod("method1");
    final Method method2 = TestClass.class.getMethod("method2");

    Assert.assertEquals(new MethodMatcherCacheKey(InvocationHandler1.class, method1).hashCode(),
        new MethodMatcherCacheKey(InvocationHandler1.class, method1).hashCode());
    Assert.assertNotEquals(new MethodMatcherCacheKey(InvocationHandler1.class, method1).hashCode(),
        new MethodMatcherCacheKey(InvocationHandler2.class, method1).hashCode());
    Assert.assertNotEquals(new MethodMatcherCacheKey(InvocationHandler1.class, method1).hashCode(),
        new MethodMatcherCacheKey(InvocationHandler1.class, method2).hashCode());
  }

  class TestClass {
    
    public TestClass() {
    }
    
    public void method1() {
    }

    public void method2() {
    }
  }

  class InvocationHandler1 extends GenericInvocationHandler<TestClass> {
    public InvocationHandler1(TestClass underlying) {
      super(underlying);
    }
  }

  class InvocationHandler2 extends GenericInvocationHandler<TestClass> {
    public InvocationHandler2(TestClass underlying) {
      super(underlying);
    }
  }
}
