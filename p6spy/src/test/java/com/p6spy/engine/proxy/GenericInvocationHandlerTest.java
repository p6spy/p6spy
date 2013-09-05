package com.p6spy.engine.proxy;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenericInvocationHandlerTest {
  @Test
  public void testInvoke() throws Exception {

    Set set = new HashSet();

    TestDelegate delegate = new TestDelegate();

    GenericInvocationHandler<Set> invocationHandler = new GenericInvocationHandler<Set>(set);
    invocationHandler.addDelegate(new MethodNameMatcher("size"), delegate);
    invocationHandler.addDelegate(new MethodNameMatcher("clear"), delegate);

    Set proxy = (Set) Proxy.newProxyInstance(
        set.getClass().getClassLoader(),
        new Class[]{Set.class},
        invocationHandler);


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

  public class TestDelegate implements Delegate {
    private Boolean invokedFlag;

    public TestDelegate() throws NoSuchMethodException {
      this.invokedFlag = false;
    }

    public boolean isInvoked() {
      return invokedFlag;
    }

    public void setInvoked(boolean invoked) {
      this.invokedFlag = invoked;
    }

    @Override
    public Object invoke(Object targetObject, Method method, Object[] args) throws Throwable {
      setInvoked(true);
      return method.invoke(targetObject, args);
    }
  }

  public class TestDelegate2 extends TestDelegate {

    public TestDelegate2() throws NoSuchMethodException {
    }
  }
}
