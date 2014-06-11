/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
 * %%
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
 * #L%
 */
package com.p6spy.engine.common;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.logging.P6LogLoadableOptions;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.P6ModuleManager;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.FileLogger;
import com.p6spy.engine.spy.appender.FormattedLogger;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.p6spy.engine.spy.appender.P6Logger;
import com.p6spy.engine.spy.option.P6OptionChangedListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class P6LogQuery implements P6OptionChangedListener {
  
  private static final Set<Category> CATEGORIES_IMPLICITLY_INCLUDED = new HashSet<Category>(
	      Arrays.asList(Category.ERROR, Category.OUTAGE /* we still want to have outage category enabled! */));
  
  protected static P6Logger logger;

  static {
    initialize();
  }

  /**
   * Options that cause re-init of {@link P6LogQuery}.
   */
  private static final Set<String> ON_CHANGE = new HashSet<String>(Arrays.asList(
      P6SpyOptions.APPENDER_INSTANCE, P6SpyOptions.LOGFILE, P6SpyOptions.LOG_MESSAGE_FORMAT_INSTANCE));

  public void optionChanged(final String key, final Object oldValue, final Object newValue) {
    if (ON_CHANGE.contains(key)) {
      initialize();
    }
  }

  public synchronized static void initialize() {
    final P6ModuleManager moduleManager = P6ModuleManager.getInstance();
    if (null == moduleManager) {
      // not initialized yet => can't proceed
      return;
    }
    
    final P6SpyOptions opts = moduleManager.getOptions(P6SpyOptions.class);
    logger = opts.getAppenderInstance();
    if (logger != null) {
      if (logger instanceof FileLogger) {
        final String logfile = opts.getLogfile();
        ((FileLogger) logger).setLogfile(logfile);
      }
      if (logger instanceof FormattedLogger) {
        final MessageFormattingStrategy strategy = opts.getLogMessageFormatInstance();
        if (strategy != null) {
          ((FormattedLogger) logger).setStrategy(strategy);
        }
      }
    }
  }

  static protected void doLog(long elapsed, Category category, String prepared, String sql) {
    doLog(-1, elapsed, category, prepared, sql);
  }

  // this is an internal method called by logElapsed
  static protected void doLogElapsed(int connectionId, long startTime, long endTime, Category category, String prepared, String sql) {
    doLog(connectionId, (endTime - startTime), category, prepared, sql);
  }

	/**
	 * Writes log information provided.
	 * 
	 * @param connectionId
	 * @param elapsed
	 * @param category
	 * @param prepared
	 * @param sql
	 */
	static protected void doLog(int connectionId, long elapsed, Category category, String prepared, String sql) {
	    // give it one more try if not initialized yet
	    if (logger == null) {
	      initialize();
	      if (logger == null) {
	    	  return;
	      }
	    }
    
      final String format = P6SpyOptions.getActiveInstance().getDateformat();
      final String stringNow;
      if (format == null) {
        stringNow = Long.toString(System.currentTimeMillis());
      } else {
        stringNow = new SimpleDateFormat(format).format(new java.util.Date()).trim();
      }

      logger.logSQL(connectionId, stringNow, elapsed, category, prepared, sql);

      final boolean stackTrace = P6SpyOptions.getActiveInstance().getStackTrace();
      if (stackTrace) {
	    final String stackTraceClass = P6SpyOptions.getActiveInstance().getStackTraceClass();
    	Exception e = new Exception();
        if (stackTraceClass != null) {
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          e.printStackTrace(pw);
          String stack = sw.toString();
          if (stack.indexOf(stackTraceClass) == -1) {
            e = null;
          }
        }
        if (e != null) {
          logger.logException(e);
        }
      }
  }

  static boolean isLoggable(String sql) {
	if (null == sql) {
		return false;
	}
	
    final P6LogLoadableOptions opts = P6LogOptions.getActiveInstance();
    
    if (!opts.getFilter()) {
      return true;
    }

    final Pattern sqlExpressionPattern = opts.getSQLExpressionPattern();
    final Pattern includeExcludePattern = opts.getIncludeExcludePattern();
    
    return (sqlExpressionPattern == null || sqlExpressionPattern != null && sqlExpressionPattern.matcher(sql).matches()) 
        && (includeExcludePattern == null || includeExcludePattern != null && includeExcludePattern.matcher(sql).matches());
  }

  static boolean isCategoryOk(Category category) {
    final P6LogLoadableOptions opts = P6LogOptions.getActiveInstance();
    if (null == opts) {
      return CATEGORIES_IMPLICITLY_INCLUDED.contains(category);
    }
    
    final Set<Category> excludeCategories = opts.getExcludeCategoriesSet();
    
    return logger != null && logger.isCategoryEnabled(category) 
    	&& (excludeCategories == null || !excludeCategories.contains(category));
  }
  
  // ----------------------------------------------------------------------------------------------------------
  // public accessor methods for logging and viewing query data
  // ----------------------------------------------------------------------------------------------------------

  static public void log(Category category, String prepared, String sql) {
    if (logger != null && isCategoryOk(category)) {
      doLog(-1, category, prepared, sql);
    }
  }

  static public void log(Category category, Loggable loggable) {
    if (logger != null && isCategoryOk(category)) {
      doLog(-1, category, loggable.getSql(), loggable.getSqlWithValues());
    }
  }

  static public void logElapsed(int connectionId, long startTime, Category category, String prepared, String sql) {
    logElapsed(connectionId, startTime, System.currentTimeMillis(), category, prepared, sql);
  }

  static public void logElapsed(int connectionId, long startTime, long endTime, Category category, String prepared, String sql) {
    if (logger != null && meetsThresholdRequirement(endTime - startTime) && isCategoryOk(category) && isLoggable(sql) ) {
      doLogElapsed(connectionId, startTime, endTime, category, prepared, sql);
    } else if (isDebugEnabled()) {
      debug("P6Spy intentionally did not log category: " + category + ", statement: " + sql + "  Reason: logger=" + logger + ", isLoggable="
          + isLoggable(sql) + ", isCategoryOk=" + isCategoryOk(category) + ", meetsTreshold=" + meetsThresholdRequirement(endTime - startTime));
    }
  }
  
  static public void logElapsed(int connectionId, long startTime, Category category, Loggable loggable) {
    logElapsed(connectionId, startTime, System.currentTimeMillis(), category, loggable);
  }

  static public void logElapsed(int connectionId, long startTime, long endTime, Category category, Loggable loggable) {
    // usually an expensive operation => cache where possible
    String sql = null;
    if (logger != null && meetsThresholdRequirement(endTime - startTime) && isCategoryOk(category) && isLoggable(sql = loggable.getSql())) {
      doLogElapsed(connectionId, startTime, endTime, category, sql, loggable.getSqlWithValues());
    } else if (isDebugEnabled()) {
      sql = loggable.getSqlWithValues();
      debug("P6Spy intentionally did not log category: " + category + ", statement: " + sql + "  Reason: logger=" + logger + ", isLoggable="
          + isLoggable(sql) + ", isCategoryOk=" + isCategoryOk(category) + ", meetsTreshold=" + meetsThresholdRequirement(endTime - startTime));
    }
  }

  //->JAW: new method that checks to see if this statement should be logged based
  //on whether on not it has taken greater than x amount of time.
  static private boolean meetsThresholdRequirement(long timeTaken) {
        final P6LogLoadableOptions opts = P6LogOptions.getActiveInstance();
        long executionThreshold = null != opts ? opts.getExecutionThreshold() : 0;
    
    return executionThreshold <= 0 || timeTaken > executionThreshold;
  }

  static public void info(String sql) {
    if (logger != null && isCategoryOk(Category.INFO)) {
      doLog(-1, Category.INFO, "", sql);
    }
  }

  static public boolean isDebugEnabled() {
    return isCategoryOk(Category.DEBUG);
  }

  static public void debug(String sql) {
    if (isDebugEnabled()) {
      if (logger != null) {
        doLog(-1, Category.DEBUG, "", sql);
      } else {
        System.err.println(sql);
      }
    }
  }

  static public void error(String sql) {
    System.err.println("Warning: " + sql);
    if (logger != null) {
      doLog(-1, Category.ERROR, "", sql);
    }
  }
  
  public static P6Logger getLogger() {
    return logger;
  }

}
