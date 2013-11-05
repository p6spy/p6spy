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

import com.p6spy.engine.logging.P6LogLoadableOptions;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.P6ModuleManager;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.FileLogger;
import com.p6spy.engine.spy.appender.FormattedLogger;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.p6spy.engine.spy.appender.P6Logger;
import com.p6spy.engine.spy.option.P6OptionChangedListener;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class P6LogQuery implements P6OptionChangedListener {
  
  protected static P6Logger logger;

  static {
    initialize();
  }

  /**
   * Options that cause re-init of {@link P6LogQuery}.
   */
  private static final Set<String> ON_CHANGE = new HashSet<String>(Arrays.asList(
      P6SpyOptions.APPENDER, P6SpyOptions.LOGFILE, P6SpyOptions.LOG_MESSAGE_FORMAT_INSTANCE));

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

  static public PrintStream logPrintStream(String file) {
    PrintStream ps = null;
    try {
      String path = P6Util.classPathFile(file);
      file = (path == null) ? file : path;
      ps = P6Util.getPrintStream(file, P6SpyOptions.getActiveInstance().getAppend());
    } catch (IOException io) {
      P6LogQuery.error("Error opening " + file + ", " + io.getMessage());
      ps = null;
    }

    return ps;
  }

  static protected void doLog(long elapsed, String category, String prepared, String sql) {
    doLog(-1, elapsed, category, prepared, sql);
  }

  // this is an internal method called by logElapsed
  static protected void doLogElapsed(int connectionId, long startTime, long endTime, String category, String prepared, String sql) {
    doLog(connectionId, (endTime - startTime), category, prepared, sql);
  }

  // this is an internal procedure used to actually write the log information
  static protected void doLog(int connectionId, long elapsed, String category, String prepared, String sql) {
    // giveit one more try if not initialized yet
    if (logger == null) {
      initialize();
    }
    
    if (logger != null) {
      final String format = P6SpyOptions.getActiveInstance().getDateformat();
      final String stringNow;
      if (format == null) {
        stringNow = Long.toString(System.currentTimeMillis());
      } else {
        stringNow = new SimpleDateFormat(format).format(new java.util.Date()).trim();
      }

      logger.logSQL(connectionId, stringNow, elapsed, category, prepared, sql);

      final boolean stackTrace = P6SpyOptions.getActiveInstance().getStackTrace();
      final String stackTraceClass = P6SpyOptions.getActiveInstance().getStackTraceClass();
      if (stackTrace) {
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
  }

  static boolean isLoggable(String sql) {
    return !P6LogOptions.getActiveInstance().getFilter() || isQueryOk(sql);
  }

  static boolean isCategoryOk(String category) {
    P6LogLoadableOptions opts = P6LogOptions.getActiveInstance();
    if (null == opts) {
      return true;
    }
    
    final Set<String> excludeCategories = opts.getExcludeCategoriesSet();
    
    return excludeCategories == null || !excludeCategories.contains(category);
  }
  
  static boolean isQueryOk(final String sql) {
    final P6LogLoadableOptions opts = P6LogOptions.getActiveInstance();
    
    final Pattern sqlExpressionPattern = opts.getSQLExpressionPattern();
    if (sqlExpressionPattern != null) {
      return sqlExpressionPattern.matcher(sql).matches();
    } 
    
    final Pattern includePattern = opts.getIncludeTablesPattern();
    final Pattern excludePattern = opts.getExcludeTablesPattern();
    
    final Set<String> includeTables = opts.getIncludeTables();
    final Set<String> excludeTables = opts.getExcludeTables();
    
    final String sqlLowercased = sql.toLowerCase();
    
    return (includeTables == null || includeTables.isEmpty() || includePattern.matcher(sqlLowercased).matches()) 
        && (excludeTables == null || excludeTables.isEmpty() || !excludePattern.matcher(sqlLowercased).matches());
  }

  // ----------------------------------------------------------------------------------------------------------
  // public accessor methods for logging and viewing query data
  // ----------------------------------------------------------------------------------------------------------

  // this a way for an external to dump an unrestricted line of text into the log
  // useful for the JSP demarcation tool
  static public void logText(String text) {
    logger.logText(text);
  }

  static public void log(String category, String prepared, String sql) {
    if (logger != null && isCategoryOk(category)) {
      doLog(-1, category, prepared, sql);
    }
  }

  static public void logElapsed(int connectionId, long startTime, String category, String prepared, String sql) {
    logElapsed(connectionId, startTime, System.currentTimeMillis(), category, prepared, sql);
  }

  static public void logElapsed(int connectionId, long startTime, long endTime, String category, String prepared, String sql) {
    if (logger != null && meetsThresholdRequirement(endTime - startTime) && isLoggable(sql) && isCategoryOk(category)) {
      doLogElapsed(connectionId, startTime, endTime, category, prepared, sql);
    } else if (isDebugEnabled()) {
      debug("P6Spy intentionally did not log category: " + category + ", statement: " + sql + "  Reason: logger=" + logger + ", isLoggable="
          + isLoggable(sql) + ", isCategoryOk=" + isCategoryOk(category) + ", meetsTreshold=" + meetsThresholdRequirement(endTime - startTime));
    }
  }

  //->JAW: new method that checks to see if this statement should be logged based
  //on whether on not it has taken greater than x amount of time.
  static private boolean meetsThresholdRequirement(long timeTaken) {
    long executionThreshold = P6LogOptions.getActiveInstance().getExecutionThreshold();

    if (executionThreshold <= 0) {
      return true;
    } else if (timeTaken > executionThreshold) {
      return true;
    } else {
      return false;
    }
  }

  static public void info(String sql) {
    if (logger != null && isCategoryOk("info")) {
      doLog(-1, "info", "", sql);
    }
  }

  static public boolean isDebugEnabled() {
    return isCategoryOk("debug");
  }

  static public void debug(String sql) {
    if (isDebugEnabled()) {
      if (logger != null) {
        doLog(-1, "debug", "", sql);
      } else {
        System.err.println(sql);
      }
    }
  }

  static public void error(String sql) {
    System.err.println("Warning: " + sql);
    if (logger != null) {
      doLog(-1, "error", "", sql);
    }
  }
  
  public static P6Logger getLogger() {
    return logger;
  }

}
