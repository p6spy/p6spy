package com.p6spy.engine.spy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.management.JMException;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.j256.simplejmx.client.JmxClient;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.common.SpyDotProperties;
import com.p6spy.engine.logging.P6LogOptions;

public class P6TestOptionsReload {
  
  private static JmxClient jmxClient = null;
  
  @BeforeClass
  public static void connectToJMX() throws JMException, SQLException, IOException, InterruptedException {
    // make sure to reinit properly
    new P6TestFramework("reload") {};

    String jmxPortProperty = System.getProperty(P6TestMBean.COM_SUN_MANAGEMENT_JMXREMOTE_PORT);
    int jmxPort = P6Util.parseInt(jmxPortProperty, P6TestMBean.JMXREMOTE_PORT_DEFAULT);
    jmxClient = new JmxClient(jmxPort);
  }

  /**
   * Please note, when modifying this one to check
   * {@link P6TestOptionsReload#testSetPropertyDiscartedOnExplicitReload()} as well.
   * @throws Exception
   */
  @Test
  public void testJmxSetPropertyDiscartedOnExplicitJmxReload() throws Exception {
    final String domainName = P6LogOptions.class.getPackage().getName();
    final String beanName = P6LogOptions.class.getSimpleName();
    final String attributeName = "StackTrace";
    
    // precondition
    assertFalse((Boolean) jmxClient.getAttribute(domainName, beanName, attributeName));
    
    // jmx value modification
    jmxClient.setAttribute(domainName, beanName, attributeName, true);
    assertTrue((Boolean) jmxClient.getAttribute(domainName, beanName, attributeName));
    
    // props reload
    jmxClient.invokeOperation(P6SpyOptions.class.getPackage().getName(), P6SpyOptions.class.getSimpleName(), "reload");
    
    // jmx value modification discarted
    assertFalse((Boolean) jmxClient.getAttribute(domainName, beanName, attributeName));
  }
  
  /**
   * Please note, when modifying this one to check
   * {@link P6TestOptionsReload#testJmxSetPropertyDiscartedOnExplicitJmxReload()} as well.
   * 
   * @throws Exception
   */
  @Test
  public void testSetPropertyDiscartedOnExplicitReload() throws Exception {
    // precondition
    assertFalse(P6LogOptions.getActiveInstance().getStackTrace());

    // value modification
    P6LogOptions.getActiveInstance().setStackTrace(true);
    assertTrue(P6LogOptions.getActiveInstance().getStackTrace());

    // props reload
    P6SpyOptions.getActiveInstance().reload();

    // jmx value modification discarted
    assertFalse(P6LogOptions.getActiveInstance().getStackTrace());
  }

  @Test
  public void testSetPropertyDiscartedOnAutoReload() throws Exception {
    // precondition
    assertFalse(P6LogOptions.getActiveInstance().getStackTrace());

    // value modification
    P6LogOptions.getActiveInstance().setStackTrace(true);
    assertTrue(P6LogOptions.getActiveInstance().getStackTrace());

    // no explicit props reload, just modify timestamp and wait till autoreload happens
    FileUtils.touch(new File(System.getProperty(SpyDotProperties.OPTIONS_FILE_PROPERTY)));
    Thread.sleep(2000);

    // jmx value modification discarted
    assertFalse(P6LogOptions.getActiveInstance().getStackTrace());
  }
  
  @Test
  public void testAutoReloadLifecycle() throws Exception {
    // precondition
    assertFalse(P6LogOptions.getActiveInstance().getStackTrace());

    // value modification
    P6LogOptions.getActiveInstance().setStackTrace(true);
    assertTrue(P6LogOptions.getActiveInstance().getStackTrace());

    // disable auto reload
    P6SpyOptions.getActiveInstance().setReloadProperties(false);
    FileUtils.touch(new File(System.getProperty(SpyDotProperties.OPTIONS_FILE_PROPERTY)));
    Thread.sleep(2000);

    // reload didn't happen
    assertTrue(P6LogOptions.getActiveInstance().getStackTrace());
    
    // enable auto reload
    P6SpyOptions.getActiveInstance().setReloadProperties(true);
    FileUtils.touch(new File(System.getProperty(SpyDotProperties.OPTIONS_FILE_PROPERTY)));
    Thread.sleep(2000);

    // reload did happen
    assertFalse(P6LogOptions.getActiveInstance().getStackTrace());
    
  }

}
