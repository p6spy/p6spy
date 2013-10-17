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

import java.util.List;
import java.util.Properties;

import com.p6spy.engine.common.P6ModuleManager;
import com.p6spy.engine.common.P6Util;

public class P6LogOptions implements P6LogLoadableOptions {

  private String sqlExpression;
  private boolean filter;
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
    
    setSQLExpression(properties.getProperty("sqlexpression"));
    setExecutionThreshold(properties.getProperty("executionThreshold"));
    
    setIncludecategories(properties.getProperty("includecategories"));
    setExcludecategories(properties.getProperty("excludecategories"));
    
    setFilter(properties.getProperty("filter"));
    // following depend on the filter => keep the order here
    setInclude(properties.getProperty("include"));
    setExclude(properties.getProperty("exclude"));
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
  public String getSQLExpression() {
    return sqlExpression;
  }

  @Override
  public void setSQLExpression(String sqlexpression) {
    this.sqlExpression = sqlexpression != null && sqlexpression.equals("") ? null : sqlexpression;
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
