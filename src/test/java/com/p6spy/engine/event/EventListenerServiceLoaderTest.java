
package com.p6spy.engine.event;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.logging.LoggingEventListener;
import com.p6spy.engine.spy.P6Core;
import com.p6spy.engine.test.TestJdbcEventListener;
import com.p6spy.engine.test.TestLoggingEventListener;
import com.p6spy.engine.wrapper.ConnectionWrapper;

import org.junit.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class EventListenerServiceLoaderTest {

  @Test
  public void testServiceLoader() throws Exception {
    final Connection connectionMock = mock(Connection.class);
    final Connection connection = P6Core.wrapConnection(connectionMock, ConnectionInformation.fromTestConnection(connectionMock));
    assertTrue(connection instanceof ConnectionWrapper);
    ConnectionWrapper connectionWrapper = (ConnectionWrapper) connection;
    final JdbcEventListener eventListener = connectionWrapper.getEventListener();
    assertTrue(eventListener instanceof CompoundJdbcEventListener);
    CompoundJdbcEventListener compoundJdbcEventListener = (CompoundJdbcEventListener) eventListener;

    final List<JdbcEventListener> eventListeners = compoundJdbcEventListener.getEventListeners();
    assertTrue(containsClass(TestJdbcEventListener.class, eventListeners));
    assertFalse(containsClass(JdbcEventListener.class, eventListeners));
    assertTrue(containsClass(TestLoggingEventListener.class, eventListeners));
    assertFalse(containsClass(LoggingEventListener.class, eventListeners));
  }

  private boolean containsClass(Class<?> clazz, List<JdbcEventListener> eventListeners) {
    for (JdbcEventListener jdbcEventListener : eventListeners) {
      if (clazz == jdbcEventListener.getClass()) {
        return true;
      }
    }
    return false;
  }
}
