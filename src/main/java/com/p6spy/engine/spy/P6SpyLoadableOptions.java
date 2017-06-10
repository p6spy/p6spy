
package com.p6spy.engine.spy;

import java.util.Set;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.p6spy.engine.spy.appender.P6Logger;

public interface P6SpyLoadableOptions extends P6LoadableOptions, P6SpyOptionsMBean {
  
  public Set<P6Factory> getModuleFactories();

  void setAutoflush(String autoflush);

  void setReloadProperties(String reloadproperties);

  void setReloadPropertiesInterval(String reloadpropertiesinterval);
  
  void setStackTrace(String stacktrace);

  void setAppend(String append);
  
  P6Logger getAppenderInstance();

  MessageFormattingStrategy getLogMessageFormatInstance();
  
  void setJmx(String jmx);
}
