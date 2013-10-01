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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.p6spy.engine.logging.appender.FileLogger;
import com.p6spy.engine.logging.appender.FormattedLogger;
import com.p6spy.engine.logging.appender.MessageFormattingStrategy;
import com.p6spy.engine.logging.appender.P6Logger;

public class P6LogQuery {
  protected static PrintStream qlog;

  protected static String[] includeTables;

  protected static String[] excludeTables;

  protected static String[] includeCategories;

  protected static String[] excludeCategories;

  protected static P6Logger logger;

  static {
    initMethod();
  }

  public synchronized static void initMethod() {
    String appender = P6SpyOptions.getAppender();

    if (appender == null) {
      appender = "com.p6spy.engine.logging.appender.FileLogger";
    }

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
        String logfile = P6SpyOptions.getLogfile();
        logfile = (logfile == null) ? "spy.log" : logfile;

        ((FileLogger) logger).setLogfile(logfile);
      }
      if (logger instanceof FormattedLogger) {
        String logMessageFormatter = P6SpyOptions.getLogMessageFormatter();
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

    if (P6SpyOptions.getFilter()) {
      includeTables = parseCSVList(P6SpyOptions.getInclude());
      excludeTables = parseCSVList(P6SpyOptions.getExclude());
    }
    includeCategories = parseCSVList(P6SpyOptions.getIncludecategories());
    excludeCategories = parseCSVList(P6SpyOptions.getExcludecategories());
  }

  static public PrintStream logPrintStream(String file) {
    PrintStream ps = null;
    try {
      String path = P6Util.classPathFile(file);
      file = (path == null) ? file : path;
      ps = P6Util.getPrintStream(file, P6SpyOptions.getAppend());
    } catch (IOException io) {
      P6LogQuery.error("Error opening " + file + ", " + io.getMessage());
      ps = null;
    }

    return ps;
  }

  static String[] parseCSVList(String csvList) {
    String array[] = null;
    if (csvList != null) {
      StringTokenizer tok = new StringTokenizer(csvList, ",");
      String item;
      ArrayList list = new ArrayList();
      while (tok.hasMoreTokens()) {
        item = tok.nextToken().toLowerCase().trim();
        if (item != "") {
          list.add(item.toLowerCase().trim());
        }
      }

      int max = list.size();
      Iterator it = list.iterator();
      array = new String[max];
      int i;
      for (i = 0; i < max; i++) {
        array[i] = (String) it.next();
      }
    }

    return array;
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
      SimpleDateFormat sdf = P6SpyOptions.getDateformatter();
      String stringNow;
      if (sdf == null) {
        stringNow = Long.toString(now.getTime());
      } else {
        stringNow = sdf.format(new java.util.Date(now.getTime())).trim();
      }

      logger.logSQL(connectionId, stringNow, elapsed, category, prepared, sql);

      boolean stackTrace = P6SpyOptions.getStackTrace();
      String stackTraceClass = P6SpyOptions.getStackTraceClass();
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
    return (P6SpyOptions.getFilter() == false || queryOk(sql));
  }

  static boolean isCategoryOk(String category) {
    return (includeCategories == null || includeCategories.length == 0 || foundCategory(category, includeCategories))
        && !foundCategory(category, excludeCategories);
  }

  static boolean foundCategory(String category, String categories[]) {
    if (categories != null) {
      for (String categorie : categories) {
        if (category.equals(categorie)) {
          return true;
        }
      }
    }
    return false;
  }

  static boolean queryOk(String sql) {
    if (P6SpyOptions.getSQLExpression() != null) {
      return sqlOk(sql);
    } else {
      return ((includeTables == null || includeTables.length == 0 || foundTable(sql, includeTables))) && !foundTable(sql, excludeTables);
    }
  }

  static boolean sqlOk(String sql) {
    String sqlexpression = P6SpyOptions.getSQLExpression();
    return Pattern.matches(sqlexpression, sql);
  }

  static boolean foundTable(String sql, String tables[]) {
    sql = sql.toLowerCase();
    boolean found = false;

    if (tables != null) {
      for (int i = 0; !found && i < tables.length; i++) {
        found = Pattern.matches("select.*from(.*" + tables[i] + ".*)(where|;|$)", sql);
      }
    }

    return found;
  }

  // ----------------------------------------------------------------------------------------------------------
  // public accessor methods for logging and viewing query data
  // ----------------------------------------------------------------------------------------------------------

  static public String[] getIncludeTables() {
    return includeTables;
  }

  static public String[] getExcludeTables() {
    return excludeTables;
  }

  static public void setIncludeTables(String _includeTables) {
    P6LogQuery.includeTables = P6LogQuery.parseCSVList(_includeTables);
  }

  static public void setExcludeTables(String _excludeTables) {
    P6LogQuery.excludeTables = P6LogQuery.parseCSVList(_excludeTables);
  }

  static public void setIncludeCategories(String _includeCategories) {
    P6LogQuery.includeCategories = P6LogQuery.parseCSVList(_includeCategories);
  }

  static public void setExcludeCategories(String _excludeCategories) {
    P6LogQuery.excludeCategories = P6LogQuery.parseCSVList(_excludeCategories);
  }

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
    } else if (isDebugOn()) {
      debug("P6Spy intentionally did not log category: " + category + ", statement: " + sql + "  Reason: logger=" + logger + ", isLoggable="
          + isLoggable(sql) + ", isCategoryOk=" + isCategoryOk(category) + ", meetsTreshold=" + meetsThresholdRequirement(endTime - startTime));
    }
  }

  //->JAW: new method that checks to see if this statement should be logged based
  //on whether on not it has taken greater than x amount of time.
  static private boolean meetsThresholdRequirement(long timeTaken) {
    long executionThreshold = P6SpyOptions.getExecutionThreshold();

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

  static public boolean isDebugOn() {
    return isCategoryOk("debug");
  }

  static public void debug(String sql) {
    if (isDebugOn()) {
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
