package com.p6spy.engine.spy;

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
