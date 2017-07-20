package com.p6spy.engine.spy;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.SimpleJdbcEventListener;
import com.p6spy.engine.test.P6TestFramework;
import com.p6spy.engine.wrapper.ConnectionWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class P6TestPool extends P6TestFramework {

  public P6TestPool(String db) throws SQLException, IOException {
    super(db);


    org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
    ds.setDriverClassName("com.p6spy.engine.spy.P6SpyDriver");
    ds.setUsername("sa");
    ds.setPassword("");
    ds.setUrl("jdbc:p6spy:h2:mem:p6spy");
    ds.setTestOnBorrow(true);
    ds.setTestOnConnect(true);
    ds.setValidationQuery("SELECT 1");

    connection = ds.getConnection().unwrap(ConnectionWrapper.class);
  }


  @Test
  public void testExecute() throws SQLException {
    String query = "select 2";

    final Connection connectionWrapper = ConnectionWrapper.wrap(this.connection, new SimpleJdbcEventListener() {
      @Override
      public void onBeforeAnyExecute(StatementInformation statementInformation) {
        assertThat("sql of statementInformation", statementInformation.getSql(), is(notNullValue()));
      }
    }, ConnectionInformation.fromTestConnection(this.connection));

    P6TestUtil.execute(connectionWrapper, query);
  }
}
