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

import java.util.Set;

public interface P6LogOptionsMBean {

  void setExclude(String exclude);

  String getExclude();

  void setExcludecategories(String excludecategories);

  String getExcludecategories();

  void setExcludebinary(boolean excludebinary);

  boolean getExcludebinary();
  
  void setFilter(boolean filter);

  boolean getFilter();

  void setInclude(String include);

  String getInclude();

  String getSQLExpression();

  void setSQLExpression(String sqlexpression);
  
  void unSetSQLExpression();

  void setExecutionThreshold(long executionThreshold);

  long getExecutionThreshold();

  Set<String> getIncludeList();

  Set<String> getExcludeList();

  Set<Category> getExcludeCategoriesSet();

}
