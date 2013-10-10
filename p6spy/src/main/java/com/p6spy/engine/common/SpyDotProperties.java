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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SpyDotProperties {

  public static final String OPTIONS_FILE_PROPERTY = "spy.properties";
  public static final String DEFAULT_OPTIONS_FILE = OPTIONS_FILE_PROPERTY;

  private final long lastModified;
  private final Properties properties = new Properties();

  private final File file;

  public SpyDotProperties() throws IOException {
    file = locate();
    lastModified = file.lastModified();

    FileReader fr = null;
    try {
      fr = new FileReader(file);
      properties.load(fr);
    } finally {
      if (null != fr) {
        fr.close();
      }
    }
  }

  public boolean isModified() {
    return locate().lastModified() != lastModified;
  }

  private File locate() {
    String propsFileName = System.getProperty(OPTIONS_FILE_PROPERTY, DEFAULT_OPTIONS_FILE);
    if (null == propsFileName || propsFileName.isEmpty()) {
      propsFileName = DEFAULT_OPTIONS_FILE;
    }

    final String propsFile = P6Util.classPathFile(propsFileName);
    if (propsFile != null) {
      final File propertiesFile = new File(propsFile);
      if (propertiesFile.exists()) {
        return propertiesFile;
      }
    }

    // locating failed!
    return null;
  }

  public Properties getProperties() {
    return properties;
  }
}
