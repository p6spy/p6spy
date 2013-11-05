package com.p6spy.engine.spy.appender;

import java.io.PrintStream;

public class StdoutLogger extends FormattedLogger implements P6Logger {
    protected PrintStream qlog;

    public StdoutLogger() {
      qlog = System.out;
    }
    
    public void logException(Exception e) {
      e.printStackTrace(qlog);
    }

    public void logText(String text) {
      qlog.println(text);
    }
}

