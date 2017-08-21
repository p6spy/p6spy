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

package com.p6spy.engine.spy.option;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.p6spy.engine.test.BaseTestCase;

/**
 * @author Quinton McCombs
 * @since 11/2013
 */
public class SpyDotPropertiesTest extends BaseTestCase {
  private Properties systemProps;
  private ClassLoader currentThreadLoader;
  private File testFile;

  @Before
  public void setup() {
    systemProps = new Properties();
    for( String key : System.getProperties().stringPropertyNames()) {
      systemProps.setProperty(key, System.getProperty(key));
    }
    currentThreadLoader = Thread.currentThread().getContextClassLoader();
  }
  
  @After
  public void cleanup() throws IOException {
    System.setProperties(systemProps);
    Thread.currentThread().setContextClassLoader(currentThreadLoader);
    if( testFile != null ) {
      testFile.delete();
    }
  }

  @Test
  public void testLoadFromJarFile() throws Exception {
    // create new classloader
    ClassLoader tempLoader = new URLClassLoader(new URL[]{new File("src/test/resources/SpyDotPropertiesTest.jar").toURI().toURL()}, currentThreadLoader);
    Thread.currentThread().setContextClassLoader(tempLoader);

    // configure the file to load
    System.setProperty(SpyDotProperties.OPTIONS_FILE_PROPERTY, "SpyDotPropertiesTest.properties");
    SpyDotProperties props = new SpyDotProperties();
    assertNotNull("properties not loaded!", props.getOptions());
  }

  @Test
  public void testLoadFromCurrentWorkingDirectory() throws Exception {
    // create the file to load
    testFile = File.createTempFile("test", null, new File("."));
    testFile.deleteOnExit();
    PrintWriter pw = new PrintWriter(testFile);
    pw.println("modulelist=testModule");
    pw.close();
    System.setProperty(SpyDotProperties.OPTIONS_FILE_PROPERTY, testFile.getName());

    SpyDotProperties props = new SpyDotProperties();
    assertNotNull("properties not loaded!", props.getOptions());
    
    if( !testFile.delete() ) {      
      // if the file was not closed properly, the delete will fail!
      fail("temporary file was not deleted!");
    }
  }

  @Test
  public void testMissingSpyDotProperties() throws Exception {
    
    System.setProperty(SpyDotProperties.OPTIONS_FILE_PROPERTY, UUID.randomUUID().toString());

    SpyDotProperties props = new SpyDotProperties();
    assertNull("properties loaded!", props.getOptions());
  }

}
