
package com.p6spy.engine.spy.appender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.p6spy.engine.logging.Category;

/**
 * Appender which delegates to SLF4J. All log messages are logged at the INFO
 * level using the "p6spy" category, except debug and error ones that log on the
 * respective SLF4J categories.
 */
public class Slf4JLogger extends FormattedLogger {
	private Logger log;

	public Slf4JLogger() {
		 log = LoggerFactory.getLogger("p6spy");
	}

	@Override
	public void logException(Exception e) {
		log.info("", e);
	}

	@Override
	public void logText(String text) {
		log.info(text);
	}

	@Override
	public void logSQL(int connectionId, String now, long elapsed,
			Category category, String prepared, String sql) {
		final String msg = strategy.formatMessage(connectionId, now, elapsed,
				category.toString(), prepared, sql);

		if (Category.ERROR.equals(category)) {
			log.error(msg);
		} else if (Category.WARN.equals(category)) {
			log.warn(msg);
		} else if (Category.DEBUG.equals(category)) {
			log.debug(msg);
		} else {
			log.info(msg);
		}
	}

	@Override
	public boolean isCategoryEnabled(Category category) {
		if (Category.ERROR.equals(category)) {
			return log.isErrorEnabled();
		} else if (Category.WARN.equals(category)) {
			return log.isWarnEnabled();
		} else if (Category.DEBUG.equals(category)) {
			return log.isDebugEnabled();
		} else {
			return log.isInfoEnabled();
		}
	}
}
