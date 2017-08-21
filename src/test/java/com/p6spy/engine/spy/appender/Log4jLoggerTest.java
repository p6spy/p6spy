/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
 *
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
 */

package com.p6spy.engine.spy.appender;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.P6TestUtil;
import com.p6spy.engine.spy.option.P6TestOptionDefaults;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;

public class Log4jLoggerTest extends BaseTestCase {

  private static final String SUFFIX_DEBUG = "DEBUG";
  
  private P6TestFramework framework;

  @Before
  public void setup() throws Exception {
    // reset log4j
    LogManager.resetConfiguration();

    // initialize framework
    framework = new P6TestFramework("log4j") {
    };
    framework.setUpFramework();
  }
  
  protected void configure(String log4jConfSuffix, boolean removeDefaultExcludedCategories) throws Exception {

	if (removeDefaultExcludedCategories) {
		// we test slf4j filtering here rather than categories one
		P6LogOptions.getActiveInstance().setExcludecategories("");
	}
	
    // reset log4j
    LogManager.resetConfiguration();

    // configure log4j externally
    configureLog4JInTest(log4jConfSuffix);
  }

  @After
  public void cleanup() throws Exception {
	// restore default excluded categories
	P6LogOptions.getActiveInstance().setExcludecategories(
			StringUtils.join(P6TestOptionDefaults.DEFAULT_CATEGORIES, ","));
	
    framework.closeConnection();
    
    // reset log4j
    LogManager.resetConfiguration();

    // load default configuration
    configureLog4J();
  }

  private void configureLog4J() {
    DOMConfigurator.configure("src/test/resources/log4j.xml");
  }
  
  private void configureLog4JInTest(String log4jConfSuffix) {
    DOMConfigurator.configure("src/test/resources/log4j-in-test-" + log4jConfSuffix + ".xml");
  }

  @Test
  public void testExternallyConfiguredLog4J() throws Exception {
	configure(SUFFIX_DEBUG, false);
    Connection con = DriverManager.getConnection("jdbc:p6spy:h2:mem:p6spy", "sa", null);

    Log4JTestApppender.clearCapturedMessages();
    P6TestUtil.queryForInt(con, "select count(*) from customers");

    con.close();

    assertEquals(1, Log4JTestApppender.getCapturedMessages().size());
  }
  
  @Test
  public void testCategoryToLevelMapping() throws Exception {
	  
	  checkCategoryToLevelMapping(Category.ERROR, null, Level.OFF);
	  checkCategoryToLevelMapping(Category.ERROR, Level.ERROR, Level.ERROR);
	  checkCategoryToLevelMapping(Category.ERROR, Level.ERROR, Level.WARN);
	  checkCategoryToLevelMapping(Category.ERROR, Level.ERROR, Level.INFO);
	  checkCategoryToLevelMapping(Category.ERROR, Level.ERROR, Level.DEBUG);
	  
	  checkCategoryToLevelMapping(Category.WARN, null, Level.OFF);
	  checkCategoryToLevelMapping(Category.WARN, null, Level.ERROR);
	  checkCategoryToLevelMapping(Category.WARN, Level.WARN, Level.WARN);
	  checkCategoryToLevelMapping(Category.WARN, Level.WARN, Level.INFO);
	  checkCategoryToLevelMapping(Category.WARN, Level.WARN, Level.DEBUG);

	  checkCategoryToInfoLevelMapping(Category.INFO);

	  checkCategoryToLevelMapping(Category.DEBUG, null, Level.OFF);
	  checkCategoryToLevelMapping(Category.DEBUG, null, Level.ERROR);
	  checkCategoryToLevelMapping(Category.DEBUG, null, Level.WARN);
	  checkCategoryToLevelMapping(Category.DEBUG, null, Level.INFO);
	  checkCategoryToLevelMapping(Category.DEBUG, Level.DEBUG, Level.DEBUG);

	  checkCategoryToInfoLevelMapping(Category.BATCH);
	  checkCategoryToInfoLevelMapping(Category.STATEMENT);
	  checkCategoryToInfoLevelMapping(Category.RESULTSET);
	  checkCategoryToInfoLevelMapping(Category.COMMIT);
	  checkCategoryToInfoLevelMapping(Category.ROLLBACK);
	  checkCategoryToInfoLevelMapping(Category.RESULT);
	  checkCategoryToInfoLevelMapping(Category.OUTAGE);
	  
	  checkCategoryToInfoLevelMapping(new Category("newly_created_category"));
  }
  
  private void checkCategoryToInfoLevelMapping(Category category) throws Exception {
	  checkCategoryToLevelMapping(category, null, Level.OFF);
	  checkCategoryToLevelMapping(category, null, Level.ERROR);
	  checkCategoryToLevelMapping(category, null, Level.WARN);
	  checkCategoryToLevelMapping(category, Level.INFO, Level.INFO);
	  checkCategoryToLevelMapping(category, Level.INFO, Level.DEBUG);
  }

  public void checkCategoryToLevelMapping(Category category, Level expectedLevel, Level thresholdLevel) throws Exception {
  		configure(thresholdLevel.toString(), true);

		Log4JTestApppender.clearCapturedMessages();
	    P6LogQuery.log(category, "sample msg", "sample msg");
	    
	    if (expectedLevel == null) {
	    	assertEquals(0, Log4JTestApppender.getCapturedMessages().size());
	    } else {
	    	assertEquals(1, Log4JTestApppender.getCapturedMessages().size());
		    assertEquals(expectedLevel, Log4JTestApppender.getCapturedMessages().get(0).getLevel());	
	    }
  }

  public static class Log4JTestApppender extends ConsoleAppender {
    static List<LoggingEvent> messages = new ArrayList<LoggingEvent>();

    public static void clearCapturedMessages() {
      messages.clear();
    }

    public static List<LoggingEvent> getCapturedMessages() {
      return messages;
    }

    @Override
    protected void subAppend(LoggingEvent event) {
      messages.add(event);
      super.subAppend(event);
    }
  }
}
