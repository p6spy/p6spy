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

import com.p6spy.engine.logging.Category;

public interface P6Logger {

        /**
         * Logs the {@code SQL}.
         * 
         * @param instanceId
         *            instance identifier. 
         * @param connectionId
         *            connection identifier.
         * @param now
         *            current time.
         * @param elapsed
         * @param category
         *            the category to be used for logging.
         * @param prepared
         *            the prepared statement to be logged.
         * @param sql
         *            the {@code SQL} to be logged.
         */
        public void logSQL(final String instanceId, int connectionId, String now, long elapsed,
                        Category category, String prepared, String sql);

        /**
         * Logs the stacktrace of the exception.
         * 
         * @param instanceId
         *            instance identifier.
         * @param e
         *            exception holding the stacktrace to be logged.
         */
        public void logException(final String instanceId, Exception e);

        /**
         * Logs the text.
         * 
         * @param instanceId
         *            instance identifier.
         * @param text
         *            to be logged
         */
        public void logText(final String instanceId, String text);

        /**
         * @param category
         *            the category to be evaluated.
         * @return {@code true} if category is enabled. Otherwise returns
         *         {@code false}
         */
        public boolean isCategoryEnabled(Category category);
}
