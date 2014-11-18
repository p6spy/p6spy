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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.FileWriter;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;

public class TinyLogLoggerTest extends BaseTestCase {

  private static final File SPY_TINYLOG_FILE = new File("target", "spy.tinylog.log");
  
  private P6TestFramework framework;

  @Before
  public void setup() throws Exception {
    // reset log4j
    LogManager.resetConfiguration();

    // initialize framework
    framework = new P6TestFramework("tinylog") {
    };
    framework.setUpFramework();
  }
  
  @After
  public void cleanUp() {
    // not sure, how they do proper cleanup, but who cares?
    Configurator.defaultConfig().removeAllWriters();
    
    if (SPY_TINYLOG_FILE.exists()) {
      SPY_TINYLOG_FILE.delete();
    }
  }

  @Test
  public void testCategoryToLevelMapping() throws Exception {

    checkCategoryToLevelMapping(Category.ERROR, null, Level.OFF);
    checkCategoryToLevelMapping(Category.ERROR, Level.ERROR, Level.ERROR);
    checkCategoryToLevelMapping(Category.ERROR, Level.ERROR, Level.WARNING);
    checkCategoryToLevelMapping(Category.ERROR, Level.ERROR, Level.INFO);
    checkCategoryToLevelMapping(Category.ERROR, Level.ERROR, Level.DEBUG);

    checkCategoryToLevelMapping(Category.WARN, null, Level.OFF);
    checkCategoryToLevelMapping(Category.WARN, null, Level.ERROR);
    checkCategoryToLevelMapping(Category.WARN, Level.WARNING, Level.WARNING);
    checkCategoryToLevelMapping(Category.WARN, Level.WARNING, Level.INFO);
    checkCategoryToLevelMapping(Category.WARN, Level.WARNING, Level.DEBUG);

    checkCategoryToInfoLevelMapping(Category.INFO);

    checkCategoryToLevelMapping(Category.DEBUG, null, Level.OFF);
    checkCategoryToLevelMapping(Category.DEBUG, null, Level.ERROR);
    checkCategoryToLevelMapping(Category.DEBUG, null, Level.WARNING);
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
    checkCategoryToLevelMapping(category, null, Level.WARNING);
    checkCategoryToLevelMapping(category, Level.INFO, Level.INFO);
    checkCategoryToLevelMapping(category, Level.INFO, Level.DEBUG);
  }

  public void checkCategoryToLevelMapping(Category category, Level expectedLevel,
                                          Level thresholdLevel) throws Exception {
    cleanUp();
    configure(thresholdLevel, true);

    P6LogQuery.log(category, "sample msg", "sample msg");

    List<String> fileLines = FileUtils.readLines(SPY_TINYLOG_FILE);
    
    if (expectedLevel == null) {
      assertEquals(0, fileLines.size());
    } else {
      assertEquals(2 /* as they log 2 lines per msg */, fileLines.size());
      assertTrue(fileLines.get(0).contains("[main] com.p6spy.engine.spy.appender.TinyLogLogger.logSQL()"));
      assertTrue(fileLines.get(1).contains("sample msg|sample msg"));
    }
  }

  protected void configure(Level thresholdLevel, boolean removeDefaultExcludedCategories)
      throws Exception {

    if (removeDefaultExcludedCategories) {
      // we test tinylog filtering here rather than categories one
      P6LogOptions.getActiveInstance().setExcludecategories("");
    }
    Configurator.defaultConfig().writer(new FileWriter(SPY_TINYLOG_FILE.getAbsolutePath())).level(thresholdLevel)
        .activate();
  }
  
}
