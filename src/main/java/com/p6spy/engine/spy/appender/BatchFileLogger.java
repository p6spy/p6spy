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
package com.p6spy.engine.spy.appender;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * SQL batch file logger Private: (?) No Appender that writes a trace of JDBC activity into an SQL
 * batch file that can be later "replayed" using a generic SQL client.
 * <p>
 * modify the spy.properties file to make p6spy use this appender by setting
 * appender=com.p6spy.engine.spy.appender.BatchFileLogger.
 * </p>
 * <p>Here's how it works for me: logfile = spy.sql The appender writes the text of SQL
 * statements as well as commit and rollback commands to the specified file, each on a new line. For
 * prepared statements the effective text resulting from substitution of parameter signs with their
 * values is written. Other event categories are logged as SQL comments ("-- " followed by the
 * category name). Exceptions and text are silently discarded. All SQL statements except the last
 * one will have a delimiter character ';' appended to them. This batch format works well with
 * WinSQL. The format is hardcoded, so if you want to make it configurable you'll have to patch my
 * patch or write your own :)
 */
public class BatchFileLogger extends FileLogger {
  public static final char BATCH_SEPARATOR = ';';
  private boolean endOfStatement = true;

  @Override
  public void logException(Exception e) {
  }

  @Override
  public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql) {
    if (endOfStatement) {
      getStream().println(BATCH_SEPARATOR);
    }
    if (category.equals("statement")) {
      String actual = null == sql || 0 == sql.length() ? prepared : sql;
      getStream().print(actual);
      endOfStatement = true;
    } else if (Category.COMMIT.equals(category) || Category.ROLLBACK.equals(category)) {
      getStream().print(category);
      endOfStatement = true;
    } else {
      getStream().println("-- " + category);
      endOfStatement = false;
    }
    getStream().flush();
  }

  @Override
  public void logText(String text) {
  }

}
