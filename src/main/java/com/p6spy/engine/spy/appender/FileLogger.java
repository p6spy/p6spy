
package com.p6spy.engine.spy.appender;

import com.p6spy.engine.spy.P6SpyOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Appender which writes log messages to a file.  This is the default appended for P6Spy.
 */
public class FileLogger extends StdoutLogger {

  private String fileName = null;
  private PrintStream printStream = null;

  private void init() {
    if (fileName == null) {
      throw new IllegalStateException("setLogfile() must be called before init()");
    }
    try {
      printStream = new PrintStream(new FileOutputStream(fileName, P6SpyOptions.getActiveInstance().getAppend()));
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }

  @Override
  protected PrintStream getStream() {
    // Lazy init to allow for the appender to be changed at Runtime without creating an empty log file (assuming
    // that no log message has been written yet)
    if (printStream == null) {
      synchronized (this) {
        if (printStream == null) {
          init();
        }
      }
    }
    return printStream;
  }

  public void setLogfile(String fileName) {
    this.fileName = fileName;
  }
}

