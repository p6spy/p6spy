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

package com.p6spy.engine.test;

import com.p6spy.engine.event.JdbcEventListener;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @author Quinton McCombs
 * @since 11/2013
 */
public class BaseTestCase {
  private static final Logger log = Logger.getLogger(BaseTestCase.class);
  protected JdbcEventListener noOpEventListener = new JdbcEventListener() {};

  @Rule
  public TestRule testExecutionLogger = new TestWatcher() {
    /**
     * Invoked when a test is about to start
     */
    @Override
    protected void starting(final Description description) {
      log.info("\n" +
          "*****************************************************************************************\n" +
          "\n" +
          "Executing test " + description.getDisplayName() + "\n" +
          "\n" +
          "*****************************************************************************************");
    }

    /**
     * Invoked when a test method finishes (whether passing or failing)
     */
    @Override
    protected void finished(final Description description) {
      log.info("Completed test " + description.getDisplayName());
    }

    @Override
    protected void failed(Throwable e, Description description) {
      log.error("Failed test "+description.getDisplayName(), e);
    }
  };


}
