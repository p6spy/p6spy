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

package com.p6spy.engine.spy;

import java.util.Set;

public interface P6SpyOptionsMBean {

  /**
   * Reloads the whole configuration. 
   */
  void reload();
  
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
  
  void unSetJNDIContextFactory();

  void setJNDIContextProviderURL(String jndicontextproviderurl);
  
  void unSetJNDIContextProviderURL();

  String getJNDIContextProviderURL();

  void setJNDIContextCustom(String jndicontextcustom);
  
  void unSetJNDIContextCustom();

  String getJNDIContextCustom();

  void setRealDataSource(String realdatasource);
  
  void unSetRealDataSource();

  String getRealDataSource();

  void setRealDataSourceClass(String realdatasourceclass);
  
  void unSetRealDataSourceClass();

  String getRealDataSourceClass();

  void setRealDataSourceProperties(String realdatasourceproperties);
  
  void unSetRealDataSourceProperties();

  String getRealDataSourceProperties();

  String getModulelist();

  void setModulelist(String modulelist);

  Set<String> getModuleNames();

  String getDatabaseDialectDateFormat();

  void setDatabaseDialectDateFormat(String databaseDialectDateFormat);

  String getDatabaseDialectBooleanFormat();

  void setDatabaseDialectBooleanFormat(String databaseDialectBooleanFormat);

  String getCustomLogMessageFormat();

  void setCustomLogMessageFormat(String customLogMessageFormat);

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
  
  boolean getJmx();
  
  void setJmx(boolean jmx);
  
  String getJmxPrefix();
  
  void setJmxPrefix(String jmxPrefix);

}
