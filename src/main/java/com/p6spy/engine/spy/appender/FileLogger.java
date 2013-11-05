package com.p6spy.engine.spy.appender;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.p6spy.engine.spy.P6SpyOptions;

public class FileLogger extends StdoutLogger {

    public void setLogfile(String fileName) {
    	try {
    	    qlog = new PrintStream(new FileOutputStream(fileName, P6SpyOptions.getActiveInstance().getAppend()));
    	} catch (IOException e) {
    	    e.printStackTrace(System.err);
    	}
    }
}

