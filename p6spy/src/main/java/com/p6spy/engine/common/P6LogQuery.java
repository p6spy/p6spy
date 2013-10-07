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
package com.p6spy.engine.common;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.logging.appender.FileLogger;
import com.p6spy.engine.logging.appender.FormattedLogger;
import com.p6spy.engine.logging.appender.MessageFormattingStrategy;
import com.p6spy.engine.logging.appender.P6Logger;

public class P6LogQuery {
  protected static PrintStream qlog;

  protected static P6Logger logger;

  static {
    initMethod();
  }

  public synchronized static void initMethod() {
    String appender = P6LogOptions.getActiveInstance().getAppender();

    // create the logger
    try {
      logger = (P6Logger) P6Util.forName(appender).newInstance();
    } catch (Exception e1) {
      // try one more hack to load the thing
      try {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        logger = (P6Logger) loader.loadClass(appender).newInstance();

      } catch (Exception e) {
        System.err.println("Cannot instantiate " + appender + ", even on second attempt.  Logging to file log4jaux.log: " + e);
      }
    }

    if (logger != null) {
      if (logger instanceof FileLogger) {
        String logfile = P6LogOptions.getActiveInstance().getLogfile();
        logfile = (logfile == null) ? "spy.log" : logfile;

        ((FileLogger) logger).setLogfile(logfile);
      }
      if (logger instanceof FormattedLogger) {
        String logMessageFormatter = P6LogOptions.getActiveInstance().getLogMessageFormatter();
        if (logMessageFormatter != null) {
          MessageFormattingStrategy strategy = null;
          try {
            strategy = (MessageFormattingStrategy) P6Util.forName(logMessageFormatter).newInstance();
          } catch (Exception e1) {
            // try one more hack to load the thing
            try {
              ClassLoader loader = ClassLoader.getSystemClassLoader();
              strategy = (MessageFormattingStrategy) loader.loadClass(logMessageFormatter).newInstance();
            } catch (Exception e) {
              System.err.println("Cannot instantiate " + logMessageFormatter + ", even on second attempt. " + e);
            }
          }
          if (strategy != null) {
            ((FormattedLogger) logger).setStrategy(strategy);
          }

        }
      }
    }
  }

  static public PrintStream logPrintStream(String file) {
    PrintStream ps = null;
    try {
      String path = P6Util.classPathFile(file);
      file = (path == null) ? file : path;
      ps = P6Util.getPrintStream(file, P6LogOptions.getActiveInstance().getAppend());
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
    if (logger != null) {
      java.util.Date now = P6Util.timeNow();
      SimpleDateFormat sdf = P6LogOptions.getActiveInstance().getDateformatter();
      String stringNow;
      if (sdf == null) {
        stringNow = Long.toString(now.getTime());
      } else {
        stringNow = sdf.format(new java.util.Date(now.getTime())).trim();
      }

      logger.logSQL(connectionId, stringNow, elapsed, category, prepared, sql);

      boolean stackTrace = P6LogOptions.getActiveInstance().getStackTrace();
      String stackTraceClass = P6LogOptions.getActiveInstance().getStackTraceClass();
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
    List<String> includeCategories = P6LogOptions.getActiveInstance().getIncludeCategoriesList();
    List<String> excludeCategories = P6LogOptions.getActiveInstance().getExcludeCategoriesList();
    
    return (includeCategories == null || includeCategories.isEmpty() || foundCategory(category, includeCategories))
        && !foundCategory(category, excludeCategories);
  }

  static boolean foundCategory(String category, List<String> categories) {
    if (categories != null) {
      for (String categorie : categories) {
        if (category.equals(categorie)) {
          return true;
        }
      }
    }
    return false;
  }

  static boolean isQueryOk(final String sql) {
    if (P6LogOptions.getActiveInstance().getSQLExpression() != null) {
      return isSqlOk(sql);
    } 
    
    List<String> includeTables = P6LogOptions.getActiveInstance().getIncludeTableList();
    List<String> excludeTables = P6LogOptions.getActiveInstance().getExcludeTableList();
    
    return ((includeTables == null || includeTables.isEmpty() || isTableFound(sql, includeTables))) && !isTableFound(sql, excludeTables);
  }

  static boolean isSqlOk(final String sql) {
    String sqlexpression = P6LogOptions.getActiveInstance().getSQLExpression();
    return Pattern.matches(sqlexpression, sql);
  }

  static boolean isTableFound(final String sql, final List<String> tables) {
    final String sqlLowercased = sql.toLowerCase();

    if (tables != null) {
      for (String table : tables) {
        // TODO [Peter Butkovic] improve performance by precompiling + caching the patterns
        if (Pattern.matches("select.*from(.*" + table + ".*)(where|;|$)", sqlLowercased)) {
          return true;
        }
      }
    }

    return false;
  }

  // ----------------------------------------------------------------------------------------------------------
  // public accessor methods for logging and viewing query data
  // ----------------------------------------------------------------------------------------------------------

  // this a way for an external to dump an unrestricted line of text into the log
  // useful for the JSP demarcation tool
  static public void logText(String text) {
    logger.logText(text);
    //qlog.println(text);
  }

  static public void log(String category, String prepared, String sql) {
    //if (qlog != null) {
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
