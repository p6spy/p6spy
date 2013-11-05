package com.p6spy.engine.spy;

import java.util.Set;

public interface P6SpyOptionsMBean {

  /**
   * Reloads the whole configuration. 
   */
  void reload();
  
//  void setUsePrefix(boolean usePrefix);
//
//  boolean getUsePrefix();

  void setAutoflush(boolean autoflush);

  boolean getAutoflush();

  String getDriverlist();

  void setDriverlist(String driverlist);

  Set<String> getDriverNames();

  boolean getReloadProperties();

  void setReloadProperties(boolean reloadproperties);

  long getReloadPropertiesInterval();

  void setReloadPropertiesInterval(long reloadpropertiesinterval);

  void setJNDIContextFactory(String jndicontextfactory);

  String getJNDIContextFactory();

  void setJNDIContextProviderURL(String jndicontextproviderurl);

  String getJNDIContextProviderURL();

  void setJNDIContextCustom(String jndicontextcustom);

  String getJNDIContextCustom();

  void setRealDataSource(String realdatasource);

  String getRealDataSource();

  void setRealDataSourceClass(String realdatasourceclass);

  String getRealDataSourceClass();

  void setRealDataSourceProperties(String realdatasourceproperties);

  String getRealDataSourceProperties();

  String getModulelist();

  void setModulelist(String modulelist);

  Set<String> getModuleNames();

  String getDatabaseDialectDateFormat();

  void setDatabaseDialectDateFormat(String databaseDialectDateFormat);

  void setAppend(boolean append);

  boolean getAppend();

  void setLogfile(String logfile);

  String getLogfile();

  String getAppender();

  void setAppender(String className);

  void setDateformat(String dateformat);

  String getDateformat();

  boolean getStackTrace();

  void setStackTrace(boolean stacktrace);
  
  String getStackTraceClass();

  void setStackTraceClass(String stacktraceclass);

  String getLogMessageFormat();

  void setLogMessageFormat(String logMessageFormatter);


}
