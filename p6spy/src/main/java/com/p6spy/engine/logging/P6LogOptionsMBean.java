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

public interface P6LogOptionsMBean {

  void setExclude(String exclude);

  String getExclude();

  void setExcludecategories(String excludecategories);

  String getExcludecategories();

  void setFilter(boolean filter);

  boolean getFilter();

  void setInclude(String include);

  String getInclude();

  void setIncludecategories(String includecategories);

  String getIncludecategories();

  String getSQLExpression();

  void setSQLExpression(String sqlexpression);

  void setExecutionThreshold(long executionThreshold);

  long getExecutionThreshold();

  List<String> getIncludeTableList();

  List<String> getExcludeTableList();

  List<String> getIncludeCategoriesList();

  List<String> getExcludeCategoriesList();

}
