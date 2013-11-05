package com.p6spy.engine.spy.appender;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link FileLogger} extension capable of keeping history of log messages as well as the last
 * Stacktrace.<br/>
 * <br/>
 * 
 * 
 * @author peterb
 */
public class P6TestLogger extends StdoutLogger {

  private ArrayList<String> logs = new ArrayList<String>();
  private String lastStacktrace;

  @Override
  public void logText(String text) {
    if (null != text) {
      logs.add(text);
    }
    super.logText(text);
  }

  public List<String> getLogs() {
    return Collections.unmodifiableList(logs);
  }

  public void clearLogs() {
    logs.clear();
  }

  public String getLastEntry() {
    return logs.isEmpty() ? null : logs.get(logs.size() - 1);
  }

  public String getLastButOneEntry() {
    return logs.isEmpty() ? null : logs.get(logs.size() - 2);
  }

  public String getLastStacktrace() {
    return lastStacktrace;
  }

  @Override
  public void logException(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    lastStacktrace = sw.toString();
    super.logException(e);
  }

  public void clearLastStacktrace() {
    this.lastStacktrace = null;
  }

  public void clearLogEntries() {
    this.logs.clear();
  }

}
