package com.p6spy.engine.logging;

import java.util.regex.Pattern;

import com.p6spy.engine.spy.P6LoadableOptions;

public interface P6LogLoadableOptions extends P6LoadableOptions, P6LogOptionsMBean {

  // these we don't need to be exposed via JMX
  
  void setFilter(String filter);

  void setExecutionThreshold(String executionThreshold);
  
  Pattern getIncludeTablesPattern();

  Pattern getExcludeTablesPattern();

  Pattern getSQLExpressionPattern();

}
