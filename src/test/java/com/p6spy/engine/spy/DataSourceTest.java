/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.p6spy.engine.spy;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.appender.P6TestLogger;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;
import com.p6spy.engine.wrapper.AbstractWrapper;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.eclipse.jetty.plus.jndi.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Quinton McCombs
 * @since 10/2013
 */
public class DataSourceTest extends BaseTestCase {

  /*
     This test is not parameterized because we are only testing generic
     functionality of adding a proxy with the data source instead of with
     the driver.
   */

  private String user;
  private String password;
  private String url;
  private Resource spyDsResource;
  private Resource realDsResource;
  private Connection con;
  private TestBasicDataSource realDs;
  private String driverClass;


  @Before
  public void setup() throws Exception {
    // make sure to reinit properly
    new P6TestFramework("ds") {
    };

    user = "sa";
    password ="sa";
    // please note non-typical DB name
    // however it seems that the typical one "jdbc:h2:mem:p6spy"
    // caused authorization exception, see: https://github.com/p6spy/p6spy/issues/76
    // in some test execution sequences (on some setups only)
    // I suspect non-proper cleanup in some of the previous tests causing this error
//  url = "jdbc:h2:mem:p6spy";
    url = "jdbc:h2:mem:p6spyDSTest";
    driverClass = "org.h2.Driver";

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
  public void testGenericDataSourceWithDriverManager() throws Exception {
    // Create and bind the real data source
    // Note: This will get the driver from the DriverManager
    realDs = new TestBasicDataSource();
    realDs.setDriverClassName(driverClass);
    realDs.setUrl(url);
    realDs.setUsername(user);
    realDs.setPassword(password);
    realDs.setUseDriverManager(true);
    realDsResource = new Resource("jdbc/realDs", realDs);

    P6TestUtil.setupTestData(realDs);


    // get the data source from JNDI
    DataSource ds = new JndiDataSourceLookup().getDataSource("jdbc/spyDs");
    assertNotNull("JNDI data source not found", ds);

    // get the connection
    con = ds.getConnection();

    // verify that the connection class is a proxy
    assertTrue("Connection is not a proxy", AbstractWrapper.isProxy(con));

    Statement stmt = con.createStatement();
    stmt.execute("select 1 from customers");
    stmt.close();
    assertTrue(((P6TestLogger) P6LogQuery.getLogger()).getLastEntry().indexOf("select 1") != -1);
    assertEquals("Incorrect number of spy log messages", 1, ((P6TestLogger) P6LogQuery.getLogger()).getLogs().size());
  }

  @Test
  public void testGenericDataSourceWithOutDriverManager() throws Exception {
    // Create and bind the real data source
    // Note: This will get the driver from the DriverManager
    realDs = new TestBasicDataSource();
    realDs.setDriverClassName(driverClass);
    realDs.setUrl(url);
    realDs.setUsername(user);
    realDs.setPassword(password);
    realDs.setUseDriverManager(false);
    realDsResource = new Resource("jdbc/realDs", realDs);

    P6TestUtil.setupTestData(realDs);

    // get the data source from JNDI
    DataSource ds = new JndiDataSourceLookup().getDataSource("jdbc/spyDs");
    assertNotNull("JNDI data source not found", ds);

    // get the connection
    con = ds.getConnection();

    // verify that the connection class is a proxy
    assertTrue("Connection is not a proxy", AbstractWrapper.isProxy(con));

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
