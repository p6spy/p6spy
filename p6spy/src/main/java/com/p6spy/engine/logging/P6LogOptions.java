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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.p6spy.engine.spy.P6ModuleManager;
import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6LogOptions implements P6LogLoadableOptions {

  public static final String EXCLUDE = "exclude";
  public static final String INCLUDE = "include";
  public static final String FILTER = "filter";
  public static final String EXCLUDECATEGORIES = "excludecategories";
  public static final String INCLUDECATEGORIES = "includecategories";
  public static final String EXECUTION_THRESHOLD = "executionThreshold";
  public static final String SQLEXPRESSION = "sqlexpression";

  // those set indirectly (via properties visible from outside) 
  public static final String INCLUDE_TABLES = "includeTables";
  public static final String EXCLUDE_TABLES = "excludeTables";
  public static final String INCLUDECATEGORIES_SET = "includecategoriesSet";
  public static final String EXCLUDECATEGORIES_SET = "excludecategoriesSet";

  
  public static final Map<String, String> defaults;

  static {
    defaults = new HashMap<String, String>();
    
    defaults.put(FILTER, Boolean.toString(false));
    defaults.put(EXCLUDECATEGORIES, "info,debug,result,resultset,batch");
    defaults.put(EXECUTION_THRESHOLD, Long.toString(0));
  }

  private final P6OptionsRepository optionsRepository;

  public P6LogOptions(final P6OptionsRepository optionsRepository) {
    this.optionsRepository = optionsRepository;
  }
  
  @Override
  public void load(Map<String, String> options) {
    
    setSQLExpression(options.get(SQLEXPRESSION));
    setExecutionThreshold(options.get(EXECUTION_THRESHOLD));
    
    setIncludecategories(options.get(INCLUDECATEGORIES));
    setExcludecategories(options.get(EXCLUDECATEGORIES));
    
    setFilter(options.get(FILTER));
    setInclude(options.get(INCLUDE));
    setExclude(options.get(EXCLUDE));
  }

  /**
   * Utility method, to make accessing options from app less verbose.
   * 
   * @return active instance of the {@link P6LogLoadableOptions}
   */
  public static P6LogLoadableOptions getActiveInstance() {
    return P6ModuleManager.getInstance().getOptions(P6LogOptions.class);
  }

  @Override
  public Map<String, String> getDefaults() {
    return defaults;
  }

  // JMX exposed API
  
  @Override
  public void setExclude(String exclude) {
    optionsRepository.set(String.class, EXCLUDE, exclude);
    optionsRepository.setSet(String.class, EXCLUDE_TABLES, exclude);
  }

  @Override
  public String getExclude() {
    return optionsRepository.get(String.class, EXCLUDE);
  }

  @Override
  public void setExcludecategories(String excludecategories) {
    optionsRepository.set(String.class, EXCLUDECATEGORIES, excludecategories);
    optionsRepository.setSet(String.class, EXCLUDECATEGORIES_SET, excludecategories);
  }

  @Override
  public String getExcludecategories() {
    return optionsRepository.get(String.class, EXCLUDECATEGORIES);
  }

  @Override
  public void setFilter(String filter) {
    optionsRepository.set(Boolean.class, FILTER, filter);
  }
  
  @Override
  public void setFilter(boolean filter) {
    optionsRepository.set(Boolean.class, FILTER, filter);
  }

  @Override
  public boolean getFilter() {
    return optionsRepository.get(Boolean.class, FILTER);
  }

  @Override
  public void setInclude(String include) {
    optionsRepository.set(String.class, INCLUDE, include);
    optionsRepository.setSet(String.class, INCLUDE_TABLES, include);
  }

  @Override
  public String getInclude() {
    return optionsRepository.get(String.class, INCLUDE);
  }

  @Override
  public void setIncludecategories(String includecategories) {
    optionsRepository.set(String.class, INCLUDECATEGORIES, includecategories);
    optionsRepository.setSet(String.class, INCLUDECATEGORIES_SET, includecategories);
  }

  @Override
  public String getIncludecategories() {
    return optionsRepository.get(String.class, INCLUDECATEGORIES);
  }

  @Override
  public String getSQLExpression() {
    return optionsRepository.get(String.class, SQLEXPRESSION);
  }

  @Override
  public void setSQLExpression(String sqlexpression) {
    optionsRepository.set(String.class, SQLEXPRESSION, sqlexpression);
  }

  @Override
  public void setExecutionThreshold(String executionThreshold) {
    optionsRepository.set(Long.class, EXECUTION_THRESHOLD, executionThreshold);
  }
  
  @Override
  public void setExecutionThreshold(long executionThreshold) {
    optionsRepository.set(Long.class, EXECUTION_THRESHOLD, executionThreshold);
  }

  @Override
  public long getExecutionThreshold() {
    return optionsRepository.get(Long.class, EXECUTION_THRESHOLD);
  }
  
  @Override
  public Set<String> getIncludeTables() {
    return optionsRepository.getSet(String.class, INCLUDE_TABLES);
  }

  @Override
  public Set<String> getExcludeTables() {
    return optionsRepository.getSet(String.class, EXCLUDE_TABLES);
  }

  @Override
  public Set<String> getIncludeCategoriesSet() {
    return optionsRepository.getSet(String.class, INCLUDECATEGORIES_SET);
  }

  @Override
  public Set<String> getExcludeCategoriesSet() {
    return optionsRepository.getSet(String.class, EXCLUDECATEGORIES_SET);
  }
}
