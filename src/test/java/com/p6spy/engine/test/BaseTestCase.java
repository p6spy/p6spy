package com.p6spy.engine.test;

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
  };


}
