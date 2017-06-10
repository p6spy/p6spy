
package com.p6spy.engine.spy.appender;

import com.p6spy.engine.logging.Category;

public interface P6Logger {

        /**
         * Logs the {@code SQL}.
         * 
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
        public void logSQL(int connectionId, String now, long elapsed,
                        Category category, String prepared, String sql);

        /**
         * Logs the stacktrace of the exception.
         * 
         * @param e
         *            exception holding the stacktrace to be logged.
         */
        public void logException(Exception e);

        /**
         * Logs the text.
         * 
         * @param text
         *            to be logged
         */
        public void logText(String text);

        /**
         * @param category
         *            the category to be evaluated.
         * @return {@code true} if category is enabled. Otherwise returns
         *         {@code false}
         */
        public boolean isCategoryEnabled(Category category);
}
