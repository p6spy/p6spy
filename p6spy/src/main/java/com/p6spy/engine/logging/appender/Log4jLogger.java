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
package com.p6spy.engine.logging.appender;

import com.p6spy.engine.common.P6SpyProperties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.PrintWriter;
import java.io.StringWriter;

// adapted from Rafael Alvarez's LoggingStream class
public class Log4jLogger extends FormattedLogger implements P6Logger {
    
    protected Level level = Level.INFO;
    protected String lastEntry;
    private static Logger log;
    
    // By configuring log4j by this method, we control the p6spy logger behavior
    // using the same configuration file as p6spy, and any other attempt
    // to configure log4j will add to this configuration.
    public Log4jLogger() {
        P6SpyProperties properties = new P6SpyProperties();
        PropertyConfigurator.configure(properties.forceReadProperties());
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
