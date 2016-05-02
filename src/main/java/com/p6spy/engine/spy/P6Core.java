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
package com.p6spy.engine.spy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class P6Core {

  private static boolean initialized;

  public static Connection wrapConnection(Connection realConnection) {
    Connection con = realConnection;
    List<P6Factory> factories = P6ModuleManager.getInstance().getFactories();
    if (factories != null) {
      for (P6Factory factory : factories) {
        con = factory.getConnection(con);
      }
    }
    return con;
  }

  /**
   * Initializes the P6Spy framework
   */
  public static void initialize() {
    try {
      if (!initialized) {
        synchronized (P6Core.class) {
          if( !initialized) {
            // just make sure to cause module initialization (if not done yet)
            P6ModuleManager.getInstance();
          }
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * Used by tests to reinitialize the framework.  This method should not be used by production code!
   */
  public static synchronized void reinit() {
    initialized = false;
    // force modules to be reloaded
    P6ModuleManager.getInstance().reload();
    
    initialize();
    initialized = true;
  }
}
