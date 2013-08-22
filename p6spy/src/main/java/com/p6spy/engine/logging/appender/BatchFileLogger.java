package com.p6spy.engine.logging.appender;

import com.p6spy.engine.common.P6SpyOptions;

import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

/**
 * SQL batch file logger Private: (?) No Appender that writes a trace of JDBC activity into an SQL
 * batch file that can be later "replayed" using a generic SQL client.
 * <p>
 * modify the spy.properties file to make p6spy use this appender by adding line
 * appender=com.p6spy.engine.logging.appender.BatchFileLogger and commenting out the existing
 * appender #appender=com.p6spy.engine.logging.appender.FileLogger - specify an alternative log file
 * name in spy.properties so that diagnostic entries to spy.log made by p6spy at startup do not mess
 * up your batch.
 * </p>
 * <p>Here's how it works for me: logfile = spy.sql The appender writes the text of SQL
 * statements as well as commit and rollback commands to the specified file, each on a new line. For
 * prepared statements the effective text resulting from substitution of parameter signs with their
 * values is written. Other event categories are logged as SQL comments ("-- " followed by the
 * category name). Exceptions and text are silently discarded. All SQL statements except the last
 * one will have a delimiter character ';' appended to them. This batch format works well with
 * WinSQL. The format is hardcoded, so if you want to make it configurable you'll have to patch my
 * patch or write your own :)
 * */
public class BatchFileLogger extends FileLogger {

    public BatchFileLogger() {
        //this("spy.sql");
    }

    @Override
    public void logException(Exception e) {
    }

    @Override
    public void logSQL(int connectionId, String now, long elapsed, String category, String prepared, String sql) {
        if (endOfStatement) {
            qlog.println(BATCH_SEPARATOR);
        }
        if (category.equals("statement")) {
            String actual = null == sql || 0 == sql.length() ? prepared : sql;
            qlog.print(actual);
            endOfStatement = true;
        } else if ("commit".equalsIgnoreCase(category) || "rollback".equalsIgnoreCase(category)) {
            qlog.print(category.toUpperCase());
            endOfStatement = true;
        } else {
            qlog.println("-- " + category);
            endOfStatement = false;
        }
        qlog.flush();
    }

    @Override
    public void logText(String text) {
    }

    @Override
    public void setLogfile(String fileName) {
        try {
            boolean append = P6SpyOptions.getAppend();
            endOfStatement = append && 0L < new File(fileName).length();
            qlog = new PrintStream(new FileOutputStream(fileName, append));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public static final char BATCH_SEPARATOR = ';';

    private boolean endOfStatement;
}
