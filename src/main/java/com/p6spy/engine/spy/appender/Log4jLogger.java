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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Appender which uses Log4J configured via spy.properties.
 * <p>
 *   Note: This appender is deprecated in P6Spy 2.0.  For similar functionality,
 *   use the Slf4JLogger instead.
 * </p>
 *
 */
// adapted from Rafael Alvarez's LoggingStream class
@Deprecated
public class Log4jLogger extends FormattedLogger implements P6Logger {
    
    protected Level level = Level.INFO;
    private static Logger log;
    
    // By configuring log4j by this method, we control the p6spy logger behavior
    // using the same configuration file as p6spy, and any other attempt
    // to configure log4j will add to this configuration.
    public Log4jLogger() {
      // [Peter Butkovic] functionality moved to P6LogOptions.load()
//        P6SpyProperties properties = new P6SpyProperties();
//        P6LogLoadableOptions options = P6ModuleManager.getInstance().getOptions(P6LogLoadableOptions.class);
//        PropertyConfigurator.configure(options.getLog4JProperties());
        log = Logger.getLogger("p6spy");
        log.setAdditivity(false);
    }
    
    public void logException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw  = new PrintWriter(sw);
        e.printStackTrace(pw);
        logText(sw.toString());
    }
    
    public void logText(String text) {
        log.log(this.level, text);
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    public void setLevel(Level inVar) {
        this.level = inVar;
    }   
}
