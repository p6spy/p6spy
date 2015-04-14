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
  private ArrayList<String> instanceIds = new ArrayList<String>();
  private String lastStacktrace;

  @Override
  public void logText(final String instanceId, String text) {
    if (null != text) {
      logs.add(text);
      instanceIds.add(instanceId);
    }
    super.logText(instanceId, text);
  }

  public List<String> getLogs() {
    return Collections.unmodifiableList(logs);
  }

  public void clearLogs() {
    logs.clear();
    instanceIds.clear();
  }

  public String getLastEntry() {
    return logs.isEmpty() ? null : logs.get(logs.size() - 1);
  }
  
  public String getLastInstanceId() {
    return instanceIds.isEmpty() ? null : instanceIds.get(instanceIds.size() - 1);
  }

  public String getLastButOneEntry() {
    return logs.isEmpty() ? null : logs.get(logs.size() - 2);
  }

  public String getLastStacktrace() {
    return lastStacktrace;
  }

  @Override
  public void logException(String instanceId, Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    lastStacktrace = sw.toString();
    super.logException(instanceId, e);
  }

  public void clearLastStacktrace() {
    this.lastStacktrace = null;
  }

  public void clearLogEntries() {
    this.logs.clear();
  }

}
