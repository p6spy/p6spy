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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import javax.management.JMException;
import javax.management.ObjectName;

import org.junit.BeforeClass;
import org.junit.Test;

import com.j256.simplejmx.client.JmxClient;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.outage.P6OutageOptions;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;

public class P6TestMBean extends BaseTestCase {

  public static final String COM_SUN_MANAGEMENT_JMXREMOTE_PORT = "com.sun.management.jmxremote.port";
  public static final int JMXREMOTE_PORT_DEFAULT = 1234;

  private static JmxClient jmxClient = null;
      
  @BeforeClass
  public static void connectToJMX() throws JMException, SQLException, IOException, InterruptedException {
    // make sure to reinit properly
    new P6TestFramework("mbean") {};

    final String jmxPortProperty = System.getProperty(COM_SUN_MANAGEMENT_JMXREMOTE_PORT);
    final int jmxPort = P6Util.parseInt(jmxPortProperty, JMXREMOTE_PORT_DEFAULT);
    jmxClient = new JmxClient(jmxPort);
  }
  
  @Test
  public void testPropertyExposalViaJMX() throws Exception {
    {
      final Set<ObjectName> beanNames = jmxClient.getBeanNames();
      assertNotNull(beanNames);
    }
    
    {
      final Boolean filterJmxApi = (Boolean) jmxClient.getAttribute(P6MBeansRegistry.PACKAGE_NAME, P6LogOptions.class.getName(), "Filter");
      final Boolean filterApi = P6LogOptions.getActiveInstance().getFilter();
      assertNotNull(filterJmxApi);
      assertEquals(filterApi, filterJmxApi);
    }

    {
      final String moduleListJmxApi = (String) jmxClient.getAttribute(P6MBeansRegistry.PACKAGE_NAME, P6SpyOptions.class.getName(), "Modulelist");
      final String moduleListApi = P6SpyOptions.getActiveInstance().getModulelist();
      assertNotNull(moduleListJmxApi);
      assertEquals(moduleListApi, moduleListJmxApi);
    }
  
    {
      final Boolean outageDetectionJmxApi = (Boolean) jmxClient.getAttribute(P6MBeansRegistry.PACKAGE_NAME, P6OutageOptions.class.getName(), "OutageDetection");
      final Boolean outageDetectionApi = P6OutageOptions.getActiveInstance().getOutageDetection();
      assertNotNull(outageDetectionJmxApi);
      assertEquals(outageDetectionApi, outageDetectionJmxApi);
    }
  }
  
}
