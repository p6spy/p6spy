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
import net.sf.cglib.proxy.UndeclaredThrowableException;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GenericInvocationHandlerTest extends BaseTestCase {
  @Test
  public void testInvoke() throws Exception {

    Set set = new HashSet();

    TestDelegate delegate = new TestDelegate();

    GenericInvocationHandler<Set> invocationHandler = new GenericInvocationHandler<Set>(set);
    invocationHandler.addDelegate(new MethodNameMatcher("size"), delegate);
    invocationHandler.addDelegate(new MethodNameMatcher("clear"), delegate);

    Set proxy = ProxyFactory.createProxy(set, invocationHandler);

    proxy.add("a");
    assertFalse("interceptor should not have been invoked", delegate.isInvoked());
    delegate.setInvoked(false);

    proxy.size();
    assertTrue("interceptor should have been invoked", delegate.isInvoked());
    delegate.setInvoked(false);

    proxy.clear();
    assertTrue("interceptor should have been invoked", delegate.isInvoked());
    delegate.setInvoked(false);
  }

  @Test
  public void testDelegateReplacement() throws Exception {

    Set set = new HashSet();

    TestDelegate delegate = new TestDelegate();
    TestDelegate2 delegate2 = new TestDelegate2();

    GenericInvocationHandler<Set> invocationHandler = new GenericInvocationHandler<Set>(set);
    invocationHandler.addDelegate(new MethodNameMatcher("clear"), delegate);
    // verify that adding a second delegate with the same method matcher replaces the previous one
    invocationHandler.addDelegate(new MethodNameMatcher("clear"), delegate2);
    assertTrue(invocationHandler.getDelegate(new MethodNameMatcher("clear")).equals(delegate2));
  }

  @Test
  public void testExceptionHandlingWithExceptionThrownByDelegate() {
    ExceptionHandling targetObj = new ExceptionHandlingImpl();
    TestDelegate delegate = new TestDelegate();
    GenericInvocationHandler<ExceptionHandling> invocationHandler = new GenericInvocationHandler<ExceptionHandling>(targetObj);
    invocationHandler.addDelegate(new MethodNameMatcher("methodA"), delegate);
    ExceptionHandling proxy = ProxyFactory.createProxy(targetObj, invocationHandler);

    // unchecked exceptions will be passed through
    try {
      delegate.setExceptionToThrow(new IllegalArgumentException("blah"));
      proxy.methodA();
      fail("No exception thrown");
    } catch (Exception e) {
      assertEquals("Wrong exception thrown!", IllegalArgumentException.class, e.getClass());
    }

    // declared checked exception will be passed through only if they are declared by the interface
    try {
      delegate.setExceptionToThrow(new IOException("blah"));
      proxy.methodA();
      fail("No exception thrown");
    } catch (Exception e) {
      assertEquals("Wrong exception thrown", UndeclaredThrowableException.class, e.getClass());
      assertEquals("Wrong nested exception", IOException.class, e.getCause().getClass());
    }
    try {
      delegate.setExceptionToThrow(new SQLException("blah"));
      proxy.methodA();
      fail("No exception thrown");
    } catch (Exception e) {
      assertEquals("Wrong exception thrown", SQLException.class, e.getClass());
    }
  }

  @Test
  public void testExceptionHandlingWithExceptionThrownByMethodWithoutDelegate() {
    ExceptionHandlingImpl targetObj = new ExceptionHandlingImpl();
    GenericInvocationHandler<ExceptionHandling> invocationHandler = new GenericInvocationHandler<ExceptionHandling>(targetObj);
    ExceptionHandling proxy = ProxyFactory.createProxy(targetObj, invocationHandler);

    try {
      targetObj.throwException = true;
      proxy.methodA();
      fail("No exception thrown");
    } catch (Exception e) {
      assertEquals("Wrong exception thrown", SQLException.class, e.getClass());
    }
  }

  @Test
  public void testExceptionHandlingWithExceptionThrownByMethodWithDelegate() {
    ExceptionHandlingImpl targetObj = new ExceptionHandlingImpl();
    TestDelegate delegate = new TestDelegate();
    GenericInvocationHandler<ExceptionHandling> invocationHandler = new GenericInvocationHandler<ExceptionHandling>(targetObj);
    invocationHandler.addDelegate(new MethodNameMatcher("methodA"), delegate);
    ExceptionHandling proxy = ProxyFactory.createProxy(targetObj, invocationHandler);

    try {
      targetObj.throwException = true;
      proxy.methodA();
      fail("No exception thrown");
    } catch (Exception e) {
      assertEquals("Wrong exception thrown", SQLException.class, e.getClass());
    }
  }

  @Test
  public void testEqualsDelegate() {
    TestObject underlying = new TestObject().setId(1);
    GenericInvocationHandler<TestObject> invocationHandler = new GenericInvocationHandler<TestObject>(underlying);
    Object proxy = ProxyFactory.createProxy(underlying, invocationHandler);

    assertTrue(proxy.equals(proxy));
    assertTrue(proxy.equals(underlying));

  }

  public static class TestDelegate implements Delegate {
    private Boolean invokedFlag;
    private Throwable exceptionToThrow = null;

    public TestDelegate() {
      this.invokedFlag = false;
    }

    public boolean isInvoked() {
      return invokedFlag;
    }

    public void setInvoked(boolean invoked) {
      this.invokedFlag = invoked;
    }

    public void setExceptionToThrow(final Throwable exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }

    @Override
    public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
      setInvoked(true);
      if (exceptionToThrow != null) {
        throw exceptionToThrow;
      }
      return method.invoke(underlying, args);
    }
  }

  public static class TestDelegate2 extends TestDelegate {

    public TestDelegate2() {
    }
  }


  public static class TestObject {
    int id;
    TestObject setId(int id) {
      this.id = id;
      return this;
    }
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TestObject that = (TestObject) o;
      if (id != that.id) return false;
      return true;
    }
  }
  public static class TestObjectSubclass extends TestObject {
  }


  public static interface ExceptionHandling {
    void methodA() throws SQLException;
  }

  public static class ExceptionHandlingImpl implements ExceptionHandling {
    boolean throwException = false;

    public ExceptionHandlingImpl() {
    }

    @Override
    public void methodA() throws SQLException {
      if (throwException) throw new SQLException("fgdfgdfg");
    }
  }
}
