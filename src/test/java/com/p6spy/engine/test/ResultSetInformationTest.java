
package com.p6spy.engine.test;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.spy.P6SpyDriver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ResultSetInformationTest extends P6TestFramework {

  public ResultSetInformationTest(String db) throws SQLException, IOException {
    super(db);
  }

  @Test
  public void testResultSetInformation() throws Exception {
    final ConnectionInformation connectionInformation = connection.getConnectionInformation();
    assertSame(connection.getDelegate(), connectionInformation.getConnection());
    assertNotNull(connectionInformation.getDriver());
    assertNotEquals(P6SpyDriver.class, connectionInformation.getDriver().getClass());
    assertTrue(connectionInformation.getTimeToGetConnectionNs() > 0);
  }
}
