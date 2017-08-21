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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.spy.appender.P6TestLogger;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;
import com.p6spy.engine.wrapper.AbstractWrapper;
import org.eclipse.jetty.plus.jndi.Resource;
import org.h2.jdbcx.JdbcDataSource;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

/**
 * @author Quinton McCombs
 * @since 10/2013
 */
public class MultipleDataSourceTest extends BaseTestCase {
  private List<Resource> jndiResources;

  @Before
  public void setUp() throws Exception {
    // make sure to reinit properly
    new P6TestFramework("ds") {
    };


    jndiResources = new ArrayList<Resource>();

    // create the real data sources and bind to jndi
    JdbcDataSource realDs1 = new JdbcDataSource();
    realDs1.setUser("sa");
    realDs1.setURL("jdbc:h2:mem:multids1");
    jndiResources.add(new Resource("jdbc/realDs1", realDs1));

    JdbcDataSource realDs2 = new JdbcDataSource();
    realDs2.setUser("sa");
    realDs2.setURL("jdbc:h2:mem:multids2");
    jndiResources.add(new Resource("jdbc/realDs2", realDs2));

    JDBCDataSource realDs3 = new JDBCDataSource();
    realDs3.setUser("sa");
    realDs3.setPassword("");
    realDs3.setUrl("jdbc:hsqldb:mem:multids3");
    jndiResources.add(new Resource("jdbc/realDs3", realDs3));

    // create the spy wrapper data sources and bind to jndi
    P6DataSource spyDs1 = new P6DataSource();
    spyDs1.setRealDataSource("jdbc/realDs1");
    jndiResources.add(new Resource("jdbc/spyDs1", spyDs1));


    P6DataSource spyDs3 = new P6DataSource();
    spyDs3.setRealDataSource("jdbc/realDs3");
    jndiResources.add(new Resource("jdbc/spyDs3", spyDs3));

    // reset captured log messages
    ((P6TestLogger) P6LogQuery.getLogger()).clearLogs();
  }

  @After
  public void cleanup() {
    for (Resource resource : jndiResources) {
      try {
        resource.release();
      } catch (Exception e) {
      }
    }
  }

  @Test
  public void testSpyEnabledDataSource() throws SQLException {
    DataSource spyDs1 = new JndiDataSourceLookup().getDataSource("jdbc/spyDs1");

    // The spy data sources should be spy enabled
    validateSpyEnabled(spyDs1);
    // verify that the correct database driver was used
    assertTrue(spyDs1.getConnection().getMetaData().getDatabaseProductName().contains("H2"));

    DataSource spyDs3 = new JndiDataSourceLookup().getDataSource("jdbc/spyDs3");
    // The spy data sources should be spy enabled
    validateSpyEnabled(spyDs3);
    // verify that the correct database driver was used
    assertTrue(spyDs3.getConnection().getMetaData().getDatabaseProductName().contains("HSQL"));
  }

  @Test
  public void testNotSpyEnabledDataSource() throws SQLException {
    // the read data sources should NOT be spy enabled
    DataSource realDs1 = new JndiDataSourceLookup().getDataSource("jdbc/realDs1");
    validateNotSpyEnabled(realDs1);
    DataSource realDs2 = new JndiDataSourceLookup().getDataSource("jdbc/realDs2");
    validateNotSpyEnabled(realDs2);
    DataSource realDs3 = new JndiDataSourceLookup().getDataSource("jdbc/realDs3");
    validateNotSpyEnabled(realDs3);
  }

  private void validateSpyEnabled(DataSource ds) throws SQLException {
    assertNotNull("JNDI data source not found", ds);

    // get the connection
    Connection con = ds.getConnection();

    // verify that the connection class is a proxy
    assertTrue("Connection is not a proxy", AbstractWrapper.isProxy(con.getClass()));

    if (con.getMetaData().getDatabaseProductName().contains("HSQL")) {
      con.createStatement().execute("set database sql syntax ora true");
    }
    con.createStatement().execute("select current_date from dual");
    assertTrue(((P6TestLogger) P6LogQuery.getLogger()).getLastEntry().indexOf("select current_date") != -1);
  }

  private void validateNotSpyEnabled(DataSource ds) throws SQLException {
    assertNotNull("JNDI data source not found", ds);

    // get the connection
    Connection con = ds.getConnection();

    if (con.getMetaData().getDatabaseProductName().contains("HSQL")) {
      con.createStatement().execute("set database sql syntax ora true");
    }
    con.createStatement().execute("select current_date from dual");
    assertNull(((P6TestLogger) P6LogQuery.getLogger()).getLastEntry());
  }
}
