/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
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

	public void registerMBean(P6LoadableOptions mBean)
			throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException {
		
		checkMBean(mBean);
		
		final ObjectName mBeanObjectName = getObjectName(mBean);
		ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, mBeanObjectName);
		mBeans.add(mBeanObjectName);
	}

	public void unregisterAllMBeans() throws MBeanRegistrationException,
			InstanceNotFoundException, MalformedObjectNameException {
		
		final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		for (ObjectName mBeanObjectName : mBeans) {
			mbs.unregisterMBean(mBeanObjectName);
		}
		mBeans.clear();
	}

	private void checkMBean(P6LoadableOptions mBean) {
		if (null == mBean) {
			throw new IllegalArgumentException("mBean is null!");
		}

		if (!(mBean instanceof StandardMBean)) {
			throw new IllegalArgumentException(
					"mBean has to be instance of the StandardMBean class! But is not: "
							+ mBean);
		}
	}

	protected ObjectName getObjectName(P6LoadableOptions mBean)
			throws MalformedObjectNameException {
		String packageName = mBean.getClass().getPackage().getName();
		packageName = null == packageName ? "com.p6spy" : packageName;
		return new ObjectName(packageName + ":name="
				+ mBean.getClass().getSimpleName());
	}

}
