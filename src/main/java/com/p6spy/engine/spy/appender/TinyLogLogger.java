/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2014 P6Spy
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

import org.pmw.tinylog.Logger;

import com.p6spy.engine.logging.Category;

/**
 * TinyLog (http://www.tinylog.org/) logger implementation.
 * 
 * (as seen on: http://www.heise.de/developer/meldung/Java-Logger-tinylog-ist-Feature-Complete-2459393.html?wt_mc=rss.developer.beitrag.atom)
 * 
 * @see <a href="http://www.tinylog.org/">http://www.tinylog.org/</a>
 * @author peterb
 */
public class TinyLogLogger extends FormattedLogger {
  
  @Override
  public void logException(Exception e) {
    Logger.info(e);
  }

  @Override
  public void logText(String text) {
    Logger.info(text);
  }

  @Override
  public void logSQL(int connectionId, String now, long elapsed,
      Category category, String prepared, String sql) {
    final String msg = strategy.formatMessage(connectionId, now, elapsed,
        category.toString(), prepared, sql);

    if (Category.ERROR.equals(category)) {
      Logger.error(msg);
    } else if (Category.WARN.equals(category)) {
      Logger.warn(msg);
    } else if (Category.DEBUG.equals(category)) {
      Logger.debug(msg);
    } else {
      Logger.info(msg);
    }
  }

  @Override
  public boolean isCategoryEnabled(Category category) {
    // didn't see any option for filtering
    return true;
  }
}
