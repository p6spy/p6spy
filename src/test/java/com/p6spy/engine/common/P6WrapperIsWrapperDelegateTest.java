
package com.p6spy.engine.common;

import com.p6spy.engine.test.AbstractTestConnection;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.TestConnection;
import com.p6spy.engine.test.TestConnectionImpl;
import com.p6spy.engine.wrapper.ConnectionWrapper;

import org.apache.commons.dbcp.DelegatingConnection;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Wrapper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class P6WrapperIsWrapperDelegateTest extends BaseTestCase {

  @Test
  public void testCastableFromProxy() throws SQLException {
    Connection con = new TestConnectionImpl();
    Connection proxy = ConnectionWrapper.wrap(con, noOpEventListener, ConnectionInformation.fromTestConnection(con));

    // if the proxy implements the interface then true should be returned.
    assertTrue(proxy.isWrapperFor(Connection.class));
    assertTrue(proxy.isWrapperFor(TestConnection.class));
    assertTrue(proxy.isWrapperFor(Wrapper.class));
  }

  @Test
  public void testCastableFromUnderlying() throws SQLException {
    Connection con = new TestConnectionImpl();
    Connection proxy = ConnectionWrapper.wrap(con, noOpEventListener, ConnectionInformation.fromTestConnection(con));

    // if the underlying object extends the class (or matches the class) then true should be returned.
    assertTrue(proxy.isWrapperFor(TestConnectionImpl.class));
    assertTrue(proxy.isWrapperFor(AbstractTestConnection.class));
  }

  @Test
  public void testProxyOfWrappedConnection() throws SQLException {
    // this will be the actual connection
    Connection con = new TestConnectionImpl();

    // use a wrapper from DBCP to create a proxy of a proxy
    // Note: DBCP implements with JDBC 4.0 API so the Wrapper interface
    // is implemented here.
    DelegatingConnection underlying = new DelegatingConnection(con);

    Connection proxy = ConnectionWrapper.wrap(underlying, noOpEventListener, ConnectionInformation.fromTestConnection(con));

    // TestConnection is an interface of the wrapped underlying object.
    assertTrue(proxy.isWrapperFor(TestConnection.class));

    // ResultSet is not implemented at all - false should be returned
    assertFalse(proxy.isWrapperFor(ResultSet.class));

  }

}
