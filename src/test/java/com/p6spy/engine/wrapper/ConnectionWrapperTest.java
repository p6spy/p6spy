
package com.p6spy.engine.wrapper;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;

import org.junit.Test;

import java.sql.Connection;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ConnectionWrapperTest {

  private boolean onConnectionWrappedCalled;

  @Test
  public void testOnConnectionWrapped() throws Exception {
    final Connection connection = mock(Connection.class);
    ConnectionWrapper.wrap(connection, new JdbcEventListener() {
      @Override
      public void onConnectionWrapped(ConnectionInformation connectionInformation) {
        onConnectionWrappedCalled = true;
        assertEquals(42, connectionInformation.getTimeToGetConnectionNs());
      }
    }, ConnectionInformation.fromDataSource(mock(DataSource.class), connection, 42));
    assertTrue(onConnectionWrappedCalled);
  }
}
