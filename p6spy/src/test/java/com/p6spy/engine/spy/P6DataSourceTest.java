package com.p6spy.engine.spy;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.logging.P6LogConnectionInvocationHandler;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.eclipse.jetty.plus.jndi.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Quinton McCombs (dt77102)
 * @since 10/2013
 */
@RunWith(Parameterized.class)
public class P6DataSourceTest extends P6TestFramework {

  String user;
  String password;
  String url;


  public P6DataSourceTest(final String db) throws SQLException, IOException {
    super(db);
  }

  @Override
  public void setUpFramework() throws Exception {
    Map tp = getTestSettings();
    reloadProperty(tp);
    Properties props = loadProperties(p6TestProperties);
    user = props.getProperty("user");
    password = props.getProperty("password");
    url = props.getProperty("url");

    P6DataSource spyDs = new P6DataSource();
    spyDs.setRealDataSource("jdbc/realDs");
    new Resource("jdbc/spyDs", spyDs);

  }

  @Test
  public void testGenericDataSourceWithDriverManager() throws SQLException, NamingException {
    // Create and bind the real data source
    // Note: This will get the driver from the DriverManager
    TestBasicDataSource realDs = new TestBasicDataSource();
    realDs.setDriverClassName(P6SpyDriver.class.getName());
    realDs.setUrl(url);
    realDs.setUsername(user);
    realDs.setPassword(password);
    realDs.setUseDriverManager(true);
    new Resource("jdbc/realDs", realDs);


    // get the data source from JNDI
    DataSource ds = new JndiDataSourceLookup().getDataSource("jdbc/spyDs");
    assertNotNull("JNDI data source not found", ds);

    // get the connection
    Connection con = ds.getConnection();

    // first verify that the connection class is a proxy
    assertTrue("Connection is not a proxy", Proxy.isProxyClass(con.getClass()));

    // now verify that the proxy is OUR proxy!
    assertTrue("Wrong invocation handler!", Proxy.getInvocationHandler(con) instanceof P6LogConnectionInvocationHandler);

  }

  @Test
  public void testGenericDataSourceWithOutDriverManager() throws SQLException, NamingException {
    // Create and bind the real data source
    // Note: This will get the driver from the DriverManager
    TestBasicDataSource realDs = new TestBasicDataSource();
    realDs.setDriverClassName(P6SpyDriver.class.getName());
    realDs.setUrl(url);
    realDs.setUsername(user);
    realDs.setPassword(password);
    realDs.setUseDriverManager(false);
    new Resource("jdbc/realDs", realDs);


    // get the data source from JNDI
    DataSource ds = new JndiDataSourceLookup().getDataSource("jdbc/spyDs");
    assertNotNull("JNDI data source not found", ds);

    // get the connection
    Connection con = ds.getConnection();

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
      if( useDriverManager ) {
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
