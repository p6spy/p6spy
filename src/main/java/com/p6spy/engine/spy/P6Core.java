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

  public static Connection wrapConnection(Connection realConnection) throws SQLException {
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
    if (!initialized) {
      synchronized (P6Core.class) {
        if( !initialized) {
          // just make sure to cause module initialization (if not done yet)
          P6ModuleManager.getInstance();
        }
      }
    }
  }

  /**
   * Used by tests to reinitialize the framework.  This method should not be used by production code!
   */
  static synchronized void reinit() {
    initialized = false;
    // force modules to be reloaded
    P6ModuleManager.getInstance().reload();
    
    initialize();
    initialized = true;
  }
}
