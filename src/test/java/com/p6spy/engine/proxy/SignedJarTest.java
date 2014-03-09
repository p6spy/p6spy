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

import com.p6spy.engine.test.BaseTestCase;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.p6spy.signedjartest.SignedJarTestTarget;
import org.p6spy.signedjartest.SignedJarTestTargetImpl;

import java.io.File;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SignedJarTest extends BaseTestCase {

  @Test
  public void testProxyOfClassFromSignedJar() {
    SignedJarTestTarget target = new SignedJarTestTargetImpl();
    Delegate delegate = new Delegate() {
      @Override
      public Object invoke(Object proxy, Object underlying, Method method, Object[] args) throws Throwable {
        return method.invoke(underlying, args);
      }
    };

    GenericInvocationHandler<SignedJarTestTarget> invocationHandler = new GenericInvocationHandler<SignedJarTestTarget>(target);
    invocationHandler.addDelegate(new MethodNameMatcher("doSomething"), delegate);

    SignedJarTestTarget proxy = ProxyFactory.createProxy(target, invocationHandler);

    proxy.doSomething();

  }
}
