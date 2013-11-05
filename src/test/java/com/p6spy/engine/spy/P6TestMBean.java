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

public class P6TestMBean {

  public static final String COM_SUN_MANAGEMENT_JMXREMOTE_PORT = "com.sun.management.jmxremote.port";
  public static final int JMXREMOTE_PORT_DEFAULT = 1234;

  private static JmxClient jmxClient = null;
      
  @BeforeClass
  public static void connectToJMX() throws JMException, SQLException, IOException, InterruptedException {
    // make sure to reinit properly
    new P6TestFramework("mbean") {};

    String jmxPortProperty = System.getProperty(COM_SUN_MANAGEMENT_JMXREMOTE_PORT);
    int jmxPort = P6Util.parseInt(jmxPortProperty, JMXREMOTE_PORT_DEFAULT);
    jmxClient = new JmxClient(jmxPort);
  }
  
  @Test
  public void testPropertyExposalViaJMX() throws Exception {
    {
      final Set<ObjectName> beanNames = jmxClient.getBeanNames();
      assertNotNull(beanNames);
    }
    
    {
      final Boolean filterJmxApi = (Boolean) jmxClient.getAttribute(P6LogOptions.class.getPackage().getName(), P6LogOptions.class.getSimpleName(), "Filter");
      final Boolean filterApi = P6LogOptions.getActiveInstance().getFilter();
      assertNotNull(filterJmxApi);
      assertEquals(filterApi, filterJmxApi);
    }

    {
      final String moduleListJmxApi = (String) jmxClient.getAttribute(P6SpyOptions.class.getPackage().getName(), P6SpyOptions.class.getSimpleName(), "Modulelist");
      final String moduleListApi = P6SpyOptions.getActiveInstance().getModulelist();
      assertNotNull(moduleListJmxApi);
      assertEquals(moduleListApi, moduleListJmxApi);
    }
  
    {
      final Boolean outageDetectionJmxApi = (Boolean) jmxClient.getAttribute(P6OutageOptions.class.getPackage().getName(), P6OutageOptions.class.getSimpleName(), "OutageDetection");
      final Boolean outageDetectionApi = P6OutageOptions.getActiveInstance().getOutageDetection();
      assertNotNull(outageDetectionJmxApi);
      assertEquals(outageDetectionApi, outageDetectionJmxApi);
    }
  }
  
}
