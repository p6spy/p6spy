package com.p6spy.engine.logging;

import java.util.List;
import java.util.Properties;

import com.p6spy.engine.logging.appender.MessageFormattingStrategy;

public interface P6LogOptionsImplMBean {

  public boolean isFilter();
  public void setFilter(boolean isFilter);
  public boolean isInclude();
  public void setInclude(boolean isInclude);
  public boolean isExclude();
  public void serExclude(boolean setExclude);
  // TODO return real types => parsed Regexp ? => all on one place
  public String getSqlExpression();
  public void setSqlExpression(String sqlExpression);
  public String getDateFormat();
  public void setDateFormat(String dateFormat);
  public List<Category> getIncludeCategories();
  // TODO String[] param? for JMX 
  public void setIncludeCategories(List<Category> includeCategories);
  public List<Category> getExcludeCategories();
  public void setExcludeCategories(List<String> excludeCategories);
  public boolean isStackTrace();
  public String getStackTraceClass();
  public String getAppender();
  public void setAppender(String appender);
  
  public String getLogFile();
  public boolean isAppend();
  public Class<MessageFormattingStrategy> getLogMessageFormat();
  
  /**
   * @return all the properties and their values that are starting with: "log4j." string.
   */
  public Properties getLog4JProperties();
}
