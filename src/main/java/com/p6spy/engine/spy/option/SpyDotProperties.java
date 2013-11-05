package com.p6spy.engine.spy.option;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.P6ModuleManager;

public class SpyDotProperties implements P6OptionsSource {

  public static final String OPTIONS_FILE_PROPERTY = "spy.properties";
  public static final String DEFAULT_OPTIONS_FILE = OPTIONS_FILE_PROPERTY;

  private final long lastModified;
  
  private SpyDotPropertiesReloader reloader;

  private final File file;
  private final Map<String, String> options;

  public SpyDotProperties() throws IOException {
    file = locate();
    
    if (null == file) {
      // no config file preset => skip props loading
      lastModified = -1;
      options = null;
      return;
    }
    
    lastModified = file.lastModified();

    FileReader fr = null;
    try {
      fr = new FileReader(file);
      final Properties properties = new Properties();
      properties.load(fr);
      options = P6Util.getPropertiesMap(properties);
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

  @Override
  public Map<String, String> getOptions() {
    return options;
  }


  @Override
  public void preDestroy(P6ModuleManager p6moduleManager) {
    if (reloader != null) {
      reloader.kill(p6moduleManager);
    }
  }

  @Override
  public void postInit(P6ModuleManager p6moduleManager) {
    reloader = new SpyDotPropertiesReloader(this, p6moduleManager);
  }
}
