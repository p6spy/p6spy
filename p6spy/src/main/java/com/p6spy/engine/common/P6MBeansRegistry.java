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
package com.p6spy.engine.common;

import java.lang.management.ManagementFactory;
import java.util.Collection;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class P6MBeansRegistry {

  private final Collection<P6LoadableOptions> mBeans;

  public P6MBeansRegistry(Collection<P6LoadableOptions> mBeans)
                                                               throws InstanceAlreadyExistsException,
                                                               MBeanRegistrationException,
                                                               NotCompliantMBeanException,
                                                               MalformedObjectNameException {
    if (null == mBeans || mBeans.isEmpty()) {
      throw new IllegalArgumentException("mBeans is empty!");
    }

    this.mBeans = mBeans;
    registerMBeans();
  }

  private void registerMBeans() throws InstanceAlreadyExistsException, MBeanRegistrationException,
      NotCompliantMBeanException, MalformedObjectNameException {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    for (P6LoadableOptions mBean : mBeans) {
      mbs.registerMBean(mBean, getObjectName(mBean));
    }
  }

  public void unregisterMBeans() throws MBeanRegistrationException, InstanceNotFoundException,
      MalformedObjectNameException {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    for (P6LoadableOptions mBean : mBeans) {
      mbs.unregisterMBean(getObjectName(mBean));
    }
  }

  private ObjectName getObjectName(P6LoadableOptions mBean) throws MalformedObjectNameException {
    String packageName = mBean.getClass().getPackage().getName();
    packageName = null == packageName ? "com.p6spy" : packageName;
    return new ObjectName(packageName + ":name=" + mBean.getClass().getSimpleName());
  }

}
