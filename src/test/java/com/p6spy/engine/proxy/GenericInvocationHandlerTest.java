/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.p6spy.engine.proxy;

import net.sf.cglib.proxy.UndeclaredThrowableException;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class GenericInvocationHandlerTest {
  @Test
  public void testInvoke() throws Exception {

    Set set = new HashSet();

    TestDelegate delegate = new TestDelegate();

    GenericInvocationHandler<Set> invocationHandler = new GenericInvocationHandler<Set>(set);
    invocationHandler.addDelegate(new MethodNameMatcher("size"), delegate);
    invocationHandler.addDelegate(new MethodNameMatcher("clear"), delegate);

    Set proxy = ProxyFactory.createProxy(set, Set.class, invocationHandler);

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
    ExceptionHandling proxy = ProxyFactory.createProxy(targetObj, ExceptionHandling.class, invocationHandler);

    // unchecked exceptions will be passed through
    try {
      delegate.setExceptionToThrow(new IllegalArgumentException("blah"));
      proxy.methodA();
      fail("No exception thrown");
    } catch(Exception e) {
      assertEquals("Wrong exception thrown!",IllegalArgumentException.class, e.getClass());
    }

    // declared checked exception will be passed through only if they are declared by the interface
    try {
      delegate.setExceptionToThrow(new IOException("blah"));
      proxy.methodA();
      fail("No exception thrown");
    } catch(Exception e) {
      assertEquals("Wrong exception thrown", UndeclaredThrowableException.class, e.getClass());
      assertEquals("Wrong nested exception", IOException.class, e.getCause().getClass());
    }
    try {
      delegate.setExceptionToThrow(new SQLException("blah"));
      proxy.methodA();
      fail("No exception thrown");
    } catch(Exception e) {
      assertEquals("Wrong exception thrown", SQLException.class, e.getClass());
    }
  }

  @Test
  public void testExceptionHandlingWithExceptionThrownByMethodWithoutDelegate() {
    ExceptionHandlingImpl targetObj = new ExceptionHandlingImpl();
    GenericInvocationHandler<ExceptionHandling> invocationHandler = new GenericInvocationHandler<ExceptionHandling>(targetObj);
    ExceptionHandling proxy = ProxyFactory.createProxy(targetObj, ExceptionHandling.class, invocationHandler);

    try {
      targetObj.throwException = true;
      proxy.methodA();
      fail("No exception thrown");
    } catch(Exception e) {
      assertEquals("Wrong exception thrown", SQLException.class, e.getClass());
    }
  }

  @Test
  public void testExceptionHandlingWithExceptionThrownByMethodWithDelegate() {
    ExceptionHandlingImpl targetObj = new ExceptionHandlingImpl();
    TestDelegate delegate = new TestDelegate();
    GenericInvocationHandler<ExceptionHandling> invocationHandler = new GenericInvocationHandler<ExceptionHandling>(targetObj);
    invocationHandler.addDelegate(new MethodNameMatcher("methodA"), delegate);
    ExceptionHandling proxy = ProxyFactory.createProxy(targetObj, ExceptionHandling.class, invocationHandler);

    try {
      targetObj.throwException = true;
      proxy.methodA();
      fail("No exception thrown");
    } catch(Exception e) {
      assertEquals("Wrong exception thrown", SQLException.class, e.getClass());
    }
  }

  public class TestDelegate implements Delegate {
    private Boolean invokedFlag;
    private Throwable exceptionToThrow = null;

    public TestDelegate()  {
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
    public Object invoke(Object targetObject, Method method, Object[] args) throws Throwable {
      setInvoked(true);
      if( exceptionToThrow != null ) {
        throw exceptionToThrow;
      }
      return method.invoke(targetObject, args);
    }
  }

  public class TestDelegate2 extends TestDelegate {

    public TestDelegate2()  {
    }
  }

  public interface ExceptionHandling {
    void methodA() throws SQLException;
  }

  public class ExceptionHandlingImpl implements ExceptionHandling {
    boolean throwException = false;

    public ExceptionHandlingImpl() {
    }

    @Override
    public void methodA() throws SQLException {
      if( throwException ) throw new SQLException("fgdfgdfg");
    }
  }
}
