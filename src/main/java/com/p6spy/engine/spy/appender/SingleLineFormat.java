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

import com.p6spy.engine.common.P6Util;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class SingleLineFormat implements MessageFormattingStrategy {

  /* (non-Javadoc)
   * @see com.p6spy.engine.spy.appender.MessageFormattingStrategy#formatMessage(java.lang.String, int, java.lang.String, long, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String formatMessage(final String instanceId, final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql) {
    return now + "|" + elapsed + "|" + category + "|" + (null == instanceId ? "" : instanceId) + "|connection " + connectionId + "|" + P6Util.singleLine(prepared) + "|" + P6Util.singleLine(sql);
  }
}
