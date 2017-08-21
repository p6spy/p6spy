/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
 *
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
 */

package com.p6spy.engine.spy;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.management.InstanceNotFoundException;
import javax.management.JMException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.j256.simplejmx.client.JmxClient;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6MBeanRegistryTest {

  private static final String JMX_PREFIX_NONE = "";
  private static final String JMX_PREFIX1 = "prefix1";
  private static final String JMX_PREFIX2 = "prefix2";
  private static final String JMX_PROPERTY_APPEND = "Append";

  private static JmxClient jmxClient = null;

  @BeforeClass
  public static void connectToJMX() throws JMException, SQLException, IOException,
      InterruptedException {
    final String jmxPortProperty = System
        .getProperty(P6TestMBean.COM_SUN_MANAGEMENT_JMXREMOTE_PORT);
    final int jmxPort = P6Util.parseInt(jmxPortProperty, P6TestMBean.JMXREMOTE_PORT_DEFAULT);
    jmxClient = new JmxClient(jmxPort);
  }

  @Test
  public void testReRegisterMBeansSameRegistryWorks() throws Exception {
    P6MBeansRegistry mBeansRegistry = constructMBean(null, JMX_PREFIX_NONE, true);
    constructMBean(mBeansRegistry, JMX_PREFIX_NONE, false);
  }

  @Test
  public void testReRegisterMBeansDifferentRegistryWorks() throws Exception {
    constructMBean(null, JMX_PREFIX_NONE, true);
    constructMBean(null, JMX_PREFIX_NONE, false);
  }

  @Test
  public void testUnregisterMBeansWorks() throws Exception {
    P6MBeansRegistry mBeansRegistry = constructMBean(null, JMX_PREFIX_NONE, true);
    destroyMBean(mBeansRegistry, JMX_PREFIX_NONE);
  }

  @Test
  public void testMBeansDifferentPrefixAreIndependent() throws Exception {
    P6MBeansRegistry reg1 = constructMBean(null, JMX_PREFIX1, true);
    P6MBeansRegistry reg2 = constructMBean(null, JMX_PREFIX2, false);

    { // make sure, that jmx with prefix1 as well as the one with prefix2 are exposed
      checkMBeanAppendProperty(JMX_PREFIX1, true);
      checkMBeanAppendProperty(JMX_PREFIX2, false);
    }

    destroyMBean(reg1, JMX_PREFIX1);

    { // make sure, that jmx with prefix1 is no more exposed, but the one with prefix2 is still
      // exposed
      checkMBeanNotExposed(JMX_PREFIX1);
      checkMBeanAppendProperty(JMX_PREFIX2, false);
    }

    destroyMBean(reg2, JMX_PREFIX1);

    { // make sure, that jmx with prefix2 is not exposed either
      checkMBeanNotExposed(JMX_PREFIX2);
    }
  }

  //
  // helpers
  //

  private P6MBeansRegistry constructMBean(P6MBeansRegistry mBeansRegistry, final String jmxPrefix,
                                          final boolean appendProperty) throws Exception {
    // none to be reused registry
    if (null == mBeansRegistry) {
      mBeansRegistry = new P6MBeansRegistry();
    }

    final P6OptionsRepository repo = new P6OptionsRepository();
    final P6SpyOptions opts = new P6SpyOptions(repo);
    opts.load(opts.getDefaults());
    opts.setAppend(appendProperty);
    opts.setJmxPrefix(jmxPrefix);
    repo.initCompleted();

    mBeansRegistry.registerMBeans(new ArrayList<P6LoadableOptions>(Arrays.asList(opts)));

    checkMBeanAppendProperty(jmxPrefix, appendProperty);

    return mBeansRegistry;
  }

  private void checkMBeanAppendProperty(final String jmxPrefix, final boolean appendProperty)
      throws Exception {
    final Boolean append = (Boolean) jmxClient.getAttribute(
        P6MBeansRegistry.getPackageName(jmxPrefix), P6SpyOptions.class.getName(),
        JMX_PROPERTY_APPEND);
    Assert.assertEquals(append, appendProperty);
  }

  private void destroyMBean(P6MBeansRegistry mBeansRegistry, final String jmxPrefix)
      throws Exception {
    // none to be reused registry
    if (null == mBeansRegistry) {
      mBeansRegistry = new P6MBeansRegistry();
    }

    // unreg
    mBeansRegistry.unregisterAllMBeans(null);

    checkMBeanNotExposed(jmxPrefix);
  }

  private void checkMBeanNotExposed(final String jmxPrefix) throws Exception {
    // jmx is not exposed after unreg any more
    try {
      jmxClient.getAttribute(P6MBeansRegistry.getPackageName(null), P6SpyOptions.class.getName(),
          JMX_PROPERTY_APPEND);
    } catch (InstanceNotFoundException e) {
      // we should end up here
      return;
    }
    Assert.fail();
  }
}
