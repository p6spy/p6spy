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
package org.objectweb.util.monolog.wrapper.p6spy;

import com.p6spy.engine.spy.appender.FileLogger;
import com.p6spy.engine.spy.appender.P6Logger;

/**
 * JOnAS (tested on 5.3 version) requires us to have this one available. For the super simple implementation
 * we're going for file logger.
 * <p/>
 * TODO: Decide about proper inheritance hierarchy. Moreover packaging should be clarified (should
 * be an extra maven module per appender type?).
 * <p/>
 * Well this is kind of a hack, as we still have the same named interfaces/classes, we can use it.
 * See: http://search.maven.org/#search|ga|1|a%3A%22monolog-wrapper-p6spy%22%20AND%20g%3A%22org.ow2.
 * monolog%22 for original implementation. If not provided jonas throws:
 * 
 * <pre>
 * {@code
 * [INFO] [talledLocalContainer] 2013-12-07 00:05:07,862 : RARDeployer.doDeploy : Deploying ds-jdbc_p6spy-2832448702898055809.rar
 * [INFO] [talledLocalContainer] Cannot instantiate org.objectweb.util.monolog.wrapper.p6spy.P6SpyLogger, even on second attempt. 
 * [INFO] [talledLocalContainer] java.lang.ClassNotFoundException: org.objectweb.util.monolog.wrapper.p6spy.P6SpyLogger
 * [INFO] [talledLocalContainer]   at java.net.URLClassLoader$1.run(URLClassLoader.java:366)
 * [INFO] [talledLocalContainer]   at java.net.URLClassLoader$1.run(URLClassLoader.java:355)
 * [INFO] [talledLocalContainer]   at java.security.AccessController.doPrivileged(Native Method)
 * [INFO] [talledLocalContainer]   at java.net.URLClassLoader.findClass(URLClassLoader.java:354)
 * [INFO] [talledLocalContainer]   at java.lang.ClassLoader.loadClass(ClassLoader.java:425)
 * [INFO] [talledLocalContainer]   at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:308)
 * [INFO] [talledLocalContainer]   at java.lang.ClassLoader.loadClass(ClassLoader.java:358)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.option.P6OptionsRepository.parse(P6OptionsRepository.java:91)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.option.P6OptionsRepository.set(P6OptionsRepository.java:63)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.P6SpyOptions.setAppender(P6SpyOptions.java:346)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.P6SpyOptions.load(P6SpyOptions.java:100)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.P6ModuleManager.loadOptions(P6ModuleManager.java:181)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.P6ModuleManager.<init>(P6ModuleManager.java:126)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.P6ModuleManager.initMe(P6ModuleManager.java:73)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.P6ModuleManager.<clinit>(P6ModuleManager.java:61)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.P6Core.initialize(P6Core.java:53)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.P6SpyDriver.<init>(P6SpyDriver.java:61)
 * [INFO] [talledLocalContainer]   at com.p6spy.engine.spy.P6SpyDriver.<clinit>(P6SpyDriver.java:40)
 * [INFO] [talledLocalContainer]   at java.lang.Class.forName0(Native Method)
 * [INFO] [talledLocalContainer]   at java.lang.Class.forName(Class.java:270)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.ee.jdbc.DriverManagerMCFImpl.createManagedConnection(DriverManagerMCFImpl.java:59)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.cm.ConnectionManagerImpl.createResource(ConnectionManagerImpl.java:889)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.pool.lib.HArrayPool.createResource(HArrayPool.java:806)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.pool.lib.HArrayPool.setInitSize(HArrayPool.java:276)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.cm.ConnectionManagerImpl.setResourceAdapter(ConnectionManagerImpl.java:443)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.Rar.processRar(Rar.java:466)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.JOnASResourceService$2.execute(JOnASResourceService.java:442)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.JOnASResourceService$2.execute(JOnASResourceService.java:440)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.lib.execution.RunnableHelper.execute(RunnableHelper.java:60)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.JOnASResourceService.__M_createRA(JOnASResourceService.java:447)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.JOnASResourceService.createRA(JOnASResourceService.java)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.JOnASResourceService.__M_createResourceAdapter(JOnASResourceService.java:352)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.JOnASResourceService.createResourceAdapter(JOnASResourceService.java)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.JOnASResourceService.__M_deployRar(JOnASResourceService.java:810)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.JOnASResourceService.deployRar(JOnASResourceService.java)
 * [INFO] [talledLocalContainer]   at org.ow2.jonas.resource.internal.RARDeployer.doDeploy(RARDeployer.java:79)
 * [INFO] [talledLocalContainer]   at org.ow2.util.ee.deploy.impl.deployer.AbsDeployer$1.execute(AbsDeployer.java:85)
 * [INFO] [talledLocalContainer]   at org.ow2.util.ee.deploy.impl.deployer.AbsDeployer$1.execute(AbsDeployer.java:83)
 * [INFO] [talledLocalContainer]   at org.ow2.util.execution.helper.RunnableHelper.execute(RunnableHelper.java:69)
 * [INFO] [talledLocalContainer]   at org.ow2.util.ee.deploy.impl.deployer.AbsDeployer.deploy(AbsDeployer.java:83)
 * [INFO] [talledLocalContainer]   at org.ow2.util.ee.deploy.impl.deployer.DeployerManager.deploy(DeployerManager.java:149)
 * 
 * }
 * </pre>
 * 
 * Btw.: Going for p6spy 1.3 compatible one:
 * 
 * <pre>
 * {@code
 *  <dependency>
 *     <groupId>org.ow2.monolog</groupId>
 *     <artifactId>monolog-wrapper-p6spy</artifactId>
 *     <version>2.2.1-RC1</version>
 *   </dependency>
 * }
 * </pre>
 * 
 * Fails as well => new implementation provided for 2.x.
 * 
 * @author peterb
 * @author $Author: $
 * @review.state RED Rev: 0
 * @version $Rev: $
 */
public class P6SpyLogger extends FileLogger implements P6Logger {
}
