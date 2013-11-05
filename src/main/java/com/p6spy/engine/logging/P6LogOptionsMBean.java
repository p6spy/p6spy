package com.p6spy.engine.logging;

import java.util.Set;

public interface P6LogOptionsMBean {

  void setExclude(String exclude);

  String getExclude();

  void setExcludecategories(String excludecategories);

  String getExcludecategories();

  void setFilter(boolean filter);

  boolean getFilter();

  void setInclude(String include);

  String getInclude();

  String getSQLExpression();

  void setSQLExpression(String sqlexpression);

  void setExecutionThreshold(long executionThreshold);

  long getExecutionThreshold();

  Set<String> getIncludeTables();

  Set<String> getExcludeTables();

  Set<String> getExcludeCategoriesSet();

}
