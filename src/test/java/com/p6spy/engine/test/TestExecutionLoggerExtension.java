/**
 * P6Spy
 *
 * Copyright (C) 2002 P6Spy
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

import org.apache.log4j.Logger;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class TestExecutionLoggerExtension  implements TestWatcher, BeforeTestExecutionCallback  {

   private static final Logger log = Logger.getLogger(BaseTestCase.class);
 
   @Override
   public void testSuccessful(ExtensionContext context) {
      log.info("Completed test " + context.getDisplayName());
      TestWatcher.super.testSuccessful(context);
   }

   @Override
   public void testFailed(ExtensionContext context, Throwable cause) {
      log.error("Failed test " + context.getDisplayName(), cause);
      TestWatcher.super.testFailed(context, cause);
   }

   @Override
   public void beforeTestExecution(ExtensionContext context) throws Exception {
      log.info("\n" + //
            "*****************************************************************************************\n" + //
            "\n" + //
            "Executing test " + context.getDisplayName() + //
            "\n" + //
            "\n" + //
            "*****************************************************************************************");
   }
}
