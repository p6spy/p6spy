package com.p6spy.engine.common;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SpyDotProperties {

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
    return null;
  }
  
  public Properties getProperties() {
   return properties;
  }
}
