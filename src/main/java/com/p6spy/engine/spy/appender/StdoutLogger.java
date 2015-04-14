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

import java.io.PrintStream;

import com.p6spy.engine.logging.Category;

public class StdoutLogger extends FormattedLogger {
    protected PrintStream qlog;

    public StdoutLogger() {
      qlog = System.out;
    }
    
    @Override
    public void logException(final String instanceId, Exception e) {
      e.printStackTrace(qlog);
    }

    @Override
    public void logText(final String instanceId, String text) {
      qlog.println(text);
    }

    @Override
    public boolean isCategoryEnabled(Category category) {
            // no restrictions on logger side
            return true;
    }
}

