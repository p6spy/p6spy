
package com.p6spy.engine.spy;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.CompoundJdbcEventListener;
import com.p6spy.engine.event.DefaultEventListener;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.wrapper.ConnectionWrapper;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class P6Core {

  private static ServiceLoader<JdbcEventListener> jdbcEventListenerServiceLoader = ServiceLoader.load(JdbcEventListener.class, P6Core.class.getClassLoader());

  public static Connection wrapConnection(Connection realConnection, ConnectionInformation connectionInformation) {
    if (realConnection == null) {
      return null;
    }
    final CompoundJdbcEventListener compoundEventListener = new CompoundJdbcEventListener();
    compoundEventListener.addListender(DefaultEventListener.INSTANCE);
    registerEventListenersFromFactories(compoundEventListener);
    registerEventListenersFromServiceLoader(compoundEventListener);
    return ConnectionWrapper.wrap(realConnection, compoundEventListener, connectionInformation);
  }

  private static void registerEventListenersFromFactories(CompoundJdbcEventListener compoundEventListener) {
    List<P6Factory> factories = P6ModuleManager.getInstance().getFactories();
    if (factories != null) {
      for (P6Factory factory : factories) {
        final JdbcEventListener eventListener = factory.getJdbcEventListener();
        if (eventListener != null) {
          compoundEventListener.addListender(eventListener);
        }
      }
    }
  }

  private static void registerEventListenersFromServiceLoader(CompoundJdbcEventListener compoundEventListener) {
    for (Iterator<JdbcEventListener> iterator = jdbcEventListenerServiceLoader.iterator(); iterator.hasNext(); ) {
      try {
        compoundEventListener.addListender(iterator.next());
      } catch (ServiceConfigurationError e) {
        e.printStackTrace();
      }
    }
  }

}
