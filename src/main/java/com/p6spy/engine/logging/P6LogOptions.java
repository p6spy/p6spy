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

package com.p6spy.engine.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.management.StandardMBean;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.P6ModuleManager;
import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6LogOptions extends StandardMBean implements P6LogLoadableOptions {

  public static final String EXCLUDE = "exclude";
  public static final String INCLUDE = "include";
  public static final String FILTER = "filter";
  public static final String EXCLUDECATEGORIES = "excludecategories";
  public static final String EXCLUDEBINARY = "excludebinary";
  public static final String EXECUTION_THRESHOLD = "executionThreshold";
  public static final String SQLEXPRESSION = "sqlexpression";

  // those set indirectly (via properties visible from outside) 
  public static final String INCLUDE_LIST = "includeList";
  public static final String EXCLUDE_LIST = "excludeList";
  public static final String INCLUDE_EXCLUDE_PATTERN = "includeExcludePattern";
  public static final String EXCLUDECATEGORIES_SET = "excludecategoriesSet";
  public static final String SQLEXPRESSION_PATTERN = "sqlexpressionPattern";
  
  public static final Map<String, String> defaults;
  
  static {
    defaults = new HashMap<String, String>();
    
    defaults.put(FILTER, Boolean.toString(false));
    defaults.put(EXCLUDECATEGORIES, "info,debug,result,resultset,batch");
    defaults.put(EXCLUDEBINARY, Boolean.toString(false));
    defaults.put(EXECUTION_THRESHOLD, Long.toString(0));
  }

  private final P6OptionsRepository optionsRepository;

  public P6LogOptions(final P6OptionsRepository optionsRepository) {
    super(P6LogOptionsMBean.class, false);
    this.optionsRepository = optionsRepository;
  }
  
  @Override
  public void load(Map<String, String> options) {
    
    setSQLExpression(options.get(SQLEXPRESSION));
    setExecutionThreshold(options.get(EXECUTION_THRESHOLD));
    
    setExcludecategories(options.get(EXCLUDECATEGORIES));
    
    setFilter(options.get(FILTER));
    setInclude(options.get(INCLUDE));
    setExclude(options.get(EXCLUDE));
    setExcludebinary(options.get(EXCLUDEBINARY));
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
    optionsRepository.setSet(String.class, EXCLUDE_LIST, exclude);
    // setting effective string
    optionsRepository.set(String.class, EXCLUDE, P6Util.joinNullSafe(optionsRepository.getSet(String.class, EXCLUDE_LIST), ","));
    optionsRepository.setOrUnSet(Pattern.class, INCLUDE_EXCLUDE_PATTERN, computeIncludeExcludePattern(), defaults.get(INCLUDE_EXCLUDE_PATTERN));
  }

  private String computeIncludeExcludePattern() {
    final String excludes = P6Util.joinNullSafe(optionsRepository.getSet(String.class, EXCLUDE_LIST), "|");
    final String includes = P6Util.joinNullSafe(optionsRepository.getSet(String.class, INCLUDE_LIST), "|");
    
    if (excludes.length() == 0 && includes.length() == 0) {
      return null;
    }

    final StringBuilder sb = new StringBuilder("(?mis)^");
    
    if (excludes.length() > 0 ) {
      sb.append("(?!.*(").append(excludes).append(").*)");
    }
    
    if (includes.length() > 0 ) {
      sb.append("(.*(").append(includes).append(").*)");
    } else {
      // make sure to match any string here, if no explicit includes specified
      sb.append("(.*)");
    }
    
    return sb.append("$").toString();
  }
  
  @Override
  public String getExclude() {
    return optionsRepository.get(String.class, EXCLUDE);
  }
  
  @Override
  public void setExcludebinary(boolean excludebinary) {
    optionsRepository.set(Boolean.class, EXCLUDEBINARY, excludebinary);
  }

  @Override
  public void setExcludebinary(String excludebinary) {
    optionsRepository.set(Boolean.class, EXCLUDEBINARY, excludebinary);
  }
  
  @Override
  public boolean getExcludebinary() {
    return optionsRepository.get(Boolean.class, EXCLUDEBINARY);
  }
  
  @Override
  public void setExcludecategories(String excludecategories) {
    optionsRepository.set(String.class, EXCLUDECATEGORIES, excludecategories);
    optionsRepository.setSet(Category.class, EXCLUDECATEGORIES_SET, excludecategories);
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
    optionsRepository.setSet(String.class, INCLUDE_LIST, include);
    // setting effective string
    optionsRepository.set(String.class, INCLUDE, P6Util.joinNullSafe(optionsRepository.getSet(String.class, INCLUDE_LIST), ","));
    optionsRepository.setOrUnSet(Pattern.class, INCLUDE_EXCLUDE_PATTERN, computeIncludeExcludePattern(), defaults.get(INCLUDE_EXCLUDE_PATTERN));
  }

  @Override
  public String getInclude() {
    return optionsRepository.get(String.class, INCLUDE);
  }

  @Override
  public String getSQLExpression() {
    return optionsRepository.get(String.class, SQLEXPRESSION);
  }
  
  @Override
  public Pattern getSQLExpressionPattern() {
    return optionsRepository.get(Pattern.class, SQLEXPRESSION_PATTERN);
  }

  @Override
  public void setSQLExpression(String sqlexpression) {
    optionsRepository.set(String.class, SQLEXPRESSION, sqlexpression);
    optionsRepository.set(Pattern.class, SQLEXPRESSION_PATTERN, sqlexpression);
  }
  
  @Override
  public void unSetSQLExpression() {
    optionsRepository.setOrUnSet(String.class, SQLEXPRESSION, null, defaults.get(SQLEXPRESSION));
    optionsRepository.setOrUnSet(Pattern.class, SQLEXPRESSION_PATTERN, null, defaults.get(SQLEXPRESSION_PATTERN));
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
  public Set<String> getIncludeList() {
    return optionsRepository.getSet(String.class, INCLUDE_LIST);
  }

  @Override
  public Set<String> getExcludeList() {
    return optionsRepository.getSet(String.class, EXCLUDE_LIST);
  }
  
  @Override
  public Pattern getIncludeExcludePattern() {
    return optionsRepository.get(Pattern.class, INCLUDE_EXCLUDE_PATTERN);
  }

  @Override
  public Set<Category> getExcludeCategoriesSet() {
    return optionsRepository.getSet(Category.class, EXCLUDECATEGORIES_SET);
  }
}
