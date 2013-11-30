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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Appender which delegates to SLF4J.  All log messages are logged at the INFO level
 * using the "p6spy" category.
 */
public class Slf4JLogger extends FormattedLogger implements P6Logger {
  private Logger log;

  public Slf4JLogger() {
    log = LoggerFactory.getLogger("p6spy");
  }

  public void logException(Exception e) {
    log.info("", e);
  }

  public void logText(String text) {
    log.info(text);
  }

}
