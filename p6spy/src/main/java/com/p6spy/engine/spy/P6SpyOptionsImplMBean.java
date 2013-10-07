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
*/package com.p6spy.engine.spy;

import java.util.List;

public interface P6SpyOptionsImplMBean {

  /**
   * Reloads the whole configuration. 
   */
  void reload();
  
  void setUsePrefix(String usePrefix);

  boolean getUsePrefix();

  void setAutoflush(String autoflush);

  boolean getAutoflush();

  String getDriverlist();

  void setDriverlist(String driverlist);

  List<String> getDriverNames();

  boolean getReloadProperties();

  void setReloadProperties(String reloadproperties);

  long getReloadPropertiesInterval();

  void setReloadPropertiesInterval(String reloadpropertiesinterval);

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

  List<String> getModuleNames();

  String getDatabaseDialectDateFormat();

  void setDatabaseDialectDateFormat(String databaseDialectDateFormat);

}
