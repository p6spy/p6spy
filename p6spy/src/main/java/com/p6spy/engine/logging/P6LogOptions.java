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
package com.p6spy.engine.logging;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.p6spy.engine.common.P6ModuleManager;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.logging.appender.FileLogger;

public class P6LogOptions implements P6LogLoadableOptions {

  private boolean append;
  private String logMessageFormatter;
  private String dateformat;
  private String sqlExpression;
  private boolean stackTrace;
  private String stackTraceClass;
  private boolean filter;
  private String logfile;
  private String appender;
  private long executionThreshold;

  private String include;
  private String exclude;
  private List<String> includeTableList;
  private List<String> excludeTableList;
  
  private String includecategories;
  private String excludecategories;
  private List<String> includeCategoriesList;
  private List<String> excludeCategoriesList;


  @Override
  public void load(Properties properties) {
    loadLog4jConfig(properties);
    
    setAppend(properties.getProperty("append"));
    setAppender(properties.getProperty("appender"));
    setLogMessageFormatter(properties.getProperty("logMessageFormatter"));
    setDateformat(properties.getProperty("dateformat"));
    setSQLExpression(properties.getProperty("sqlexpression"));
    setStackTrace(properties.getProperty("stacktrace"));
    setStackTraceClass(properties.getProperty("stacktraceclass"));
    setLogfile(properties.getProperty("logfile"));
    setAppend(properties.getProperty("append"));
    setExecutionThreshold(properties.getProperty("executionThreshold"));
    
    setIncludecategories(properties.getProperty("includecategories"));
    setExcludecategories(properties.getProperty("excludecategories"));
    
    setFilter(properties.getProperty("filter"));
    // following depend on the filter => keep the order here
    setInclude(properties.getProperty("include"));
    setExclude(properties.getProperty("exclude"));
  }

  /**
   * Loads log4j specific configuration.
   * <br/><br/>
   * Please note: The existing configuration is not cleared nor reset. It's rather iterative approach here
   * once you require different behavior, provide your own log4j configuration file (holding these properties) 
   * and make sure to load/reload it properly.
   * 
   * @param properties the properties to load the configuration values from.
   */
  private void loadLog4jConfig(Properties properties) {
    PropertyConfigurator.configure(properties);
  }

  /**
   * Utility method, to make accessing options from app less verbose.
   * 
   * @return active instance of the {@link P6LogLoadableOptions}
   */
  public static P6LogLoadableOptions getActiveInstance() {
    return P6ModuleManager.getInstance().getOptions(P6LogOptions.class);
  }
  
  // JMX exposed API
  
  @Override
  public void setExclude(String exclude) {
    this.exclude = exclude;
    this.excludeTableList = P6Util.parseCSVList(exclude);  
  }

  @Override
  public String getExclude() {
    return exclude;
  }

  @Override
  public void setExcludecategories(String excludecategories) {
    this.excludecategories = excludecategories;
    this.excludeCategoriesList = P6Util.parseCSVList(excludecategories);  
  }

  @Override
  public String getExcludecategories() {
    return excludecategories;
  }

  @Override
  public void setFilter(String filter) {
    setFilter(P6Util.isTrue(filter, false));
  }
  
  @Override
  public void setFilter(boolean filter) {
    this.filter = filter;
  }

  @Override
  public boolean getFilter() {
    return filter;
  }

  @Override
  public void setInclude(String include) {
    this.include = include;
    
    if (getFilter()) {
      this.includeTableList = P6Util.parseCSVList(include);  
    }
  }

  @Override
  public String getInclude() {
    return include;
  }

  @Override
  public void setIncludecategories(String includecategories) {
    this.includecategories = includecategories;
    
    if (getFilter()) {
      this.includeCategoriesList = P6Util.parseCSVList(includecategories);  
    }
  }

  @Override
  public String getIncludecategories() {
    return includecategories;
  }

  @Override
  public void setLogfile(String logfile) {
    this.logfile = logfile == null ? "spy.log" : logfile;
  }

  @Override
  public String getLogfile() {
    return logfile;
  }

  @Override
  public String getAppender() {
    return appender;
  }

  @Override
  public void setAppender(String className) {
    appender = className;
    
    if (appender == null) {
      appender = FileLogger.class.getName();
    }
  }

  @Override
  public void setDateformat(String dateformat) {
    this.dateformat = dateformat;
  }

  @Override
  public String getDateformat() {
    return dateformat;
  }

  @Override
  public SimpleDateFormat getDateformatter() {
    if (dateformat == null || dateformat.equals("")) {
      return null;
    } else {
      return new SimpleDateFormat(dateformat);
    }
  }

  @Override
  public boolean getStackTrace() {
    return stackTrace;
  }

  @Override
  public void setStackTrace(boolean stacktrace) {
    this.stackTrace = stacktrace;
  }
  
  @Override
  public void setStackTrace(String stacktrace) {
    setStackTrace(P6Util.isTrue(stacktrace, true));
  }

  @Override
  public String getStackTraceClass() {
    return stackTraceClass;
  }

  @Override
  public void setStackTraceClass(String stacktraceclass) {
    this.stackTraceClass = stacktraceclass;
  }

  @Override
  public String getSQLExpression() {
    return sqlExpression;
  }

  @Override
  public void setSQLExpression(String sqlexpression) {
    this.sqlExpression = sqlexpression != null && sqlexpression.equals("") ? null : sqlexpression;
  }

  @Override
  public void setAppend(String append) {
    setAppend(P6Util.isTrue(append, true));
  }
  
  @Override
  public void setAppend(boolean append) {
    this.append = append;
  }

  @Override
  public boolean getAppend() {
    return append;
  }

  @Override
  public String getLogMessageFormatter() {
    return logMessageFormatter;
  }

  @Override
  public void setLogMessageFormatter(final String logMessageFormatter) {
    this.logMessageFormatter = logMessageFormatter;
  }

  @Override
  public void setExecutionThreshold(String executionThreshold) {
    setExecutionThreshold(P6Util.parseLong(executionThreshold, 0));
  }
  
  @Override
  public void setExecutionThreshold(long executionThreshold) {
    this.executionThreshold = executionThreshold;
  }

  @Override
  public long getExecutionThreshold() {
      return executionThreshold;
  }
  
  @Override
  public List<String> getIncludeTableList() {
    return includeTableList;
  }

  @Override
  public List<String> getExcludeTableList() {
    return excludeTableList;
  }

  @Override
  public List<String> getIncludeCategoriesList() {
    return includeCategoriesList;
  }

  @Override
  public List<String> getExcludeCategoriesList() {
    return excludeCategoriesList;
  }
}
