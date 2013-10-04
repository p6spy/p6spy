/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.p6spy.engine.spy;

import com.p6spy.engine.common.OptionReloader;
import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6Options;
import com.p6spy.engine.common.P6SpyOptions;
import com.p6spy.engine.common.P6SpyProperties;
import com.p6spy.engine.common.P6Util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class P6Core {
  private static List<P6Factory> factories;
  private static boolean initialized;

  public static Connection wrapConnection(Connection realConnection) throws SQLException {
    Connection con = realConnection;
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
    if (!initialized) {
      synchronized (P6Core.class) {
        if (!initialized) {
          initMethod();
        }
      }
    }
  }

  /**
   * Used by tests to reinitialize the framework.  This method should not be used by production code!
   */
  static synchronized void reinit() {
    initialized = false;
    initialize();
  }

  private static void initMethod() {
    P6SpyProperties properties = new P6SpyProperties();
    P6SpyOptions coreOptions = new P6SpyOptions();
    OptionReloader.add(coreOptions, properties);

    // register the core options file with the reloader
    String className = "no class";
    String classType = "driver";
    try {
      List<String> driverNames = P6SpyOptions.allDriverNames();
      List modules = P6SpyOptions.allModules();

      boolean hasModules = modules.size() > 0;


      // register drivers and wrappers
      classType = "driver";
      for (String driverName : driverNames) {
        // you really only need to load the driver if it is not a type 4 driver!
        P6Util.forName(driverName).newInstance();

      }

      // instantiate the factories, if nec.
      if (hasModules) {
        factories = new CopyOnWriteArrayList<P6Factory>();
        classType = "factory";

        for (Iterator<String> i = modules.iterator(); i.hasNext(); ) {
          className = i.next();
          P6Factory factory = (P6Factory) P6Util.forName(className).newInstance();
          factories.add(factory);

          P6Options options = factory.getOptions();
          if (options != null) {
            OptionReloader.add(options, properties);
          }

          P6LogQuery.debug("Registered factory: " + className + " with options: " + options);
        }
      }

      initialized = true;

      for (Driver driver : P6SpyDriver.registeredDrivers()) {
        P6LogQuery.debug("Driver manager reporting driver registered: " + driver);
      }

    } catch (Exception e) {
      String err = "Error registering " + classType + "  [" + className + "]\nCaused By: " + e.toString();
      P6LogQuery.error(err);
      throw new P6DriverNotFoundError(err);
    }

  }

}
