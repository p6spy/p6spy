
package com.p6spy.engine.spy;

import java.sql.SQLException;

import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

public class P6XAConnection extends P6PooledConnection implements XAConnection {

  public P6XAConnection(PooledConnection connection) {
    super(connection);
    
    if (!(connection instanceof XAConnection)) {
      throw new IllegalArgumentException("Argument is supposed to be of type XAConnection, but is rather:" + connection);
    }
  }

  @Override
  public XAResource getXAResource() throws SQLException {
    return ((XAConnection) passthru).getXAResource();
  }
}
