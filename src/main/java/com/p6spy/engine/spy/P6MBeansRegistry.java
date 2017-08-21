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

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public class P6MBeansRegistry {

  private final Collection<ObjectName> mBeans = new ArrayList<ObjectName>();

  public static final String PACKAGE_NAME = "com.p6spy";
  
  public void registerMBeans(Collection<P6LoadableOptions> allOptions) throws MBeanRegistrationException, InstanceNotFoundException, MalformedObjectNameException, NotCompliantMBeanException {
    boolean jmx = true; 
    String jmxPrefix = "";
    
    for (P6LoadableOptions options : allOptions) {
      if (options instanceof P6SpyOptions) {
        jmx = ((P6SpyOptions) options).getJmx();
        jmxPrefix = ((P6SpyOptions) options).getJmxPrefix();
        break;
      }
    }
       
    if (!jmx) {
      return;
    }
    
    // unreg possible conflicting ones first
    unregisterAllMBeans(jmxPrefix);
    
    // reg all
    for (P6LoadableOptions options : allOptions) {
      try {
        registerMBean(options, jmxPrefix);
      } catch (InstanceAlreadyExistsException e) {
        // sounds like someone registered beans already (before we had a chance to do so)
        // so let's just make things consistent and re-register again
        registerMBeans(allOptions);
      }
    }
  }

  protected void registerMBean(P6LoadableOptions mBean, String jmxPrefix) throws InstanceAlreadyExistsException,
      MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {

    checkMBean(mBean);

    final ObjectName mBeanObjectName = getObjectName(mBean, jmxPrefix);
    ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, mBeanObjectName);
    mBeans.add(mBeanObjectName);
  }

  public void unregisterAllMBeans(String jmxPrefix) throws MBeanRegistrationException, MalformedObjectNameException {

    // those we have reference to 
    final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    for (ObjectName mBeanObjectName : mBeans) {
      try {
        mbs.unregisterMBean(mBeanObjectName);
      } catch (InstanceNotFoundException e) {
        // this just means someone unregistered our beans already
        // but we're OK with that and it can't cause failure
      }
    }
    mBeans.clear();

    // to prevent naming conflicts: let's unreg also possible leftovers (with the same prefix)
    for (ObjectName objectName : mbs.queryNames(new ObjectName(getPackageName(jmxPrefix) + ":name=com.p6spy.*"), null)) {
      try {
        mbs.unregisterMBean(objectName);
      } catch (InstanceNotFoundException e) {
        // this just means someone unregistered the bean earlier than us
        // (quite unprobable, but parallel unreg could happen)
        // but we're OK with that and it can't cause failure
      }
      
    }
  }

  private void checkMBean(P6LoadableOptions mBean) {
    if (null == mBean) {
      throw new IllegalArgumentException("mBean is null!");
    }

    if (!(mBean instanceof StandardMBean)) {
      throw new IllegalArgumentException(
          "mBean has to be instance of the StandardMBean class! But is not: " + mBean);
    }
  }

  protected ObjectName getObjectName(P6LoadableOptions mBean, String jmxPrefix) throws MalformedObjectNameException {
    return new ObjectName(getPackageName(jmxPrefix) + ":name=" + mBean.getClass().getName());
  }
  
  protected static String getPackageName(String jmxPrefix) {
    return PACKAGE_NAME +  (null == jmxPrefix || jmxPrefix.isEmpty() ? "" : "." + jmxPrefix);
  }
  
}
