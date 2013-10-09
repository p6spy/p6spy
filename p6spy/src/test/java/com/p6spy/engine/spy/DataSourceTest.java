package com.p6spy.engine.spy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.eclipse.jetty.plus.jndi.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.logging.P6LogConnectionInvocationHandler;
import com.p6spy.engine.logging.appender.P6TestLogger;
import com.p6spy.engine.test.P6TestOptions;

/**
 * @author Quinton McCombs (dt77102)
 * @since 10/2013
 */
@RunWith(Parameterized.class)
public class DataSourceTest extends P6TestFramework {

  String user;
  String password;
  String url;
  Resource spyDsResource;
  Resource realDsResource;
  private Connection con;
  private TestBasicDataSource realDs;


  public DataSourceTest(final String db) throws SQLException, IOException {
    super(db);
  }

  @Before
  @Override
  public void setUpFramework() throws Exception {
    P6Core.reinit();

    user = P6TestOptions.getActiveInstance().getUser();
    password = P6TestOptions.getActiveInstance().getPassword();
    url = P6TestOptions.getActiveInstance().getUrl();

    P6DataSource spyDs = new P6DataSource();
    spyDs.setRealDataSource("jdbc/realDs");
    spyDsResource = new Resource("jdbc/spyDs", spyDs);

    ((P6TestLogger) P6LogQuery.getLogger()).clearLogs();
  }

  @After
  public void cleanup() throws SQLException {
    try {
      con.close();
    } catch (Exception e) {
    }
    try {
      realDs.close();
    } catch (Exception e) {
    }
    try {
      spyDsResource.release();
    } catch (Exception e) {
    }
    try {
      realDsResource.release();
    } catch (Exception e) {
    }

  }

  @Test
  public void testGenericDataSourceWithDriverManager() throws SQLException, NamingException {
    // Create and bind the real data source
    // Note: This will get the driver from the DriverManager
    realDs = new TestBasicDataSource();
    realDs.setDriverClassName(P6SpyDriver.class.getName());
    realDs.setUrl(url);
    realDs.setUsername(user);
    realDs.setPassword(password);
    realDs.setUseDriverManager(true);
    realDsResource = new Resource("jdbc/realDs", realDs);


    // get the data source from JNDI
    DataSource ds = new JndiDataSourceLookup().getDataSource("jdbc/spyDs");
    assertNotNull("JNDI data source not found", ds);

    // get the connection
    con = ds.getConnection();

    // first verify that the connection class is a proxy
    assertTrue("Connection is not a proxy", Proxy.isProxyClass(con.getClass()));

    // now verify that the proxy is OUR proxy!
    assertTrue("Wrong invocation handler!", Proxy.getInvocationHandler(con) instanceof P6LogConnectionInvocationHandler);

    con.createStatement().execute("create table testtable (col1 integer)");
    con.createStatement().execute("select 1 from testtable");
    assertTrue(((P6TestLogger) P6LogQuery.getLogger()).getLastEntry().indexOf("select 1") != -1);
    assertEquals("Incorrect number of spy log messages", 2, ((P6TestLogger) P6LogQuery.getLogger()).getLogs().size());


  }

  @Test
  public void testGenericDataSourceWithOutDriverManager() throws SQLException, NamingException {
    // Create and bind the real data source
    // Note: This will get the driver from the DriverManager
    realDs = new TestBasicDataSource();
    realDs.setDriverClassName(P6SpyDriver.class.getName());
    realDs.setUrl(url);
    realDs.setUsername(user);
    realDs.setPassword(password);
    realDs.setUseDriverManager(false);
    realDsResource = new Resource("jdbc/realDs", realDs);


    // get the data source from JNDI
    DataSource ds = new JndiDataSourceLookup().getDataSource("jdbc/spyDs");
    assertNotNull("JNDI data source not found", ds);

    // get the connection
    con = ds.getConnection();

    // first verify that the connection class is a proxy
    assertTrue("Connection is not a proxy", Proxy.isProxyClass(con.getClass()));

    // now verify that the proxy is OUR proxy!
    assertTrue("Wrong invocation handler!", Proxy.getInvocationHandler(con) instanceof P6LogConnectionInvocationHandler);

  }

  class TestBasicDataSource extends BasicDataSource {
    private boolean useDriverManager = true;

    void setUseDriverManager(final boolean useDriverManager) {
      this.useDriverManager = useDriverManager;
    }

    /**
     * Creates a JDBC connection factory for this datasource.  This method only
     * exists so subclasses can replace the implementation class.
     */
    @Override
    protected ConnectionFactory createConnectionFactory() throws SQLException {
      if (useDriverManager) {
        return new ConnectionFactory() {
          @Override
          public Connection createConnection() throws SQLException {
            return DriverManager.getConnection(getUrl(), getUsername(), getPassword());
          }
        };
      } else {
        return new ConnectionFactory() {
          @Override
          public Connection createConnection() throws SQLException {
            Driver driver;
            try {
              driver = (Driver) P6Util.forName(getDriverClassName()).newInstance();
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
            return new DriverConnectionFactory(driver, getUrl(), connectionProperties).createConnection();
          }
        };
      }
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      throw new SQLFeatureNotSupportedException();
    }
  }

}
