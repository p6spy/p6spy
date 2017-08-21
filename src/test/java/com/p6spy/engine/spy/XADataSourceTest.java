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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import liquibase.exception.LiquibaseException;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.jetty.plus.jndi.Resource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.test.P6TestFramework;
import com.p6spy.engine.test.P6TestLoadableOptions;
import com.p6spy.engine.test.P6TestOptions;

@RunWith(Parameterized.class)
public class XADataSourceTest extends P6TestFramework {

  private static final Pattern URL_PATTERN = Pattern
      .compile("jdbc:([a-zA-Z0-9]+)://([a-zA-Z0-9]+)[:]?([0-9]*)/([a-zA-Z0-9]+)");

  private TransactionManager tm;
  private List<Resource> jndiResources;
  private List<PoolingDataSource> poolingDSs;

  public XADataSourceTest(String db) throws SQLException, IOException {
    super(db);
  }

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> dbs() {
    Collection<Object[]> result = new ArrayList<Object[]>();
    for (Object o : P6TestFramework.dbs()) {
      // SQLite provides no datasource implementation => skip it
      if (!Arrays.equals(new Object[] { "SQLite" }, (Object[]) o) //
        // TODO MSSQLServer didn't figure it out (yet)
        && !Arrays.equals(new Object[] { "MSSQLServer" }, (Object[]) o)) {
        result.add((Object[]) o);
      }
    }
    return result;
  }

  @Before
  public void setUpXADataSourceTest() throws NamingException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException,
      InstantiationException {
    final P6TestLoadableOptions testOptions = P6TestOptions.getActiveInstance();
    jndiResources = new ArrayList<Resource>();
    poolingDSs = new ArrayList<PoolingDataSource>();
    tm = TransactionManagerServices.getTransactionManager();

    // in test DS setup
    {
      final XADataSource realInTestDs = (XADataSource) P6Util.forName(
          testOptions.getXaDataSource().getClass().getName()).newInstance();
      setXADSProperties(realInTestDs, testOptions.getUrl().replace("p6spy:", ""),
          testOptions.getUser(), testOptions.getPassword());
      jndiResources.add(new Resource("jdbc/realInTestDs", realInTestDs));

      final PoolingDataSource inTestDs = new PoolingDataSource();
      inTestDs.setClassName(P6DataSource.class.getName());
      inTestDs.setUniqueName("jdbc/inTestDs");
      inTestDs.setMaxPoolSize(10);
      inTestDs.getDriverProperties().setProperty("realDataSource", "jdbc/realInTestDs");
      inTestDs.setAllowLocalTransactions(true);
      inTestDs.init();
      jndiResources.add(new Resource("jdbc/inTestDs", inTestDs));
      poolingDSs.add(inTestDs);
    }

    // fixed DS setup
    {
      final XADataSource realFixedDs = (XADataSource) P6Util.forName("org.h2.jdbcx.JdbcDataSource")
          .newInstance();
      setXADSProperties(realFixedDs, "jdbc:h2:mem:p6spy_realFixedDs", "sa", "sa");
      jndiResources.add(new Resource("jdbc/realFixedDs", realFixedDs));

      final PoolingDataSource fixedDs = new PoolingDataSource();
      fixedDs.setClassName(P6DataSource.class.getName());
      fixedDs.setUniqueName("jdbc/fixedDs");
      fixedDs.setMaxPoolSize(10);
      fixedDs.getDriverProperties().setProperty("realDataSource", "jdbc/realFixedDs");
      fixedDs.setAllowLocalTransactions(true);
      fixedDs.init();
      jndiResources.add(new Resource("jdbc/fixedDs", fixedDs));
      poolingDSs.add(fixedDs);
    }

    // liquibase opens it's own transaction => keep it out of ours
    try {
      P6TestUtil.setupTestData(new JndiDataSourceLookup().getDataSource("jdbc/inTestDs"));
      P6TestUtil.setupTestData(new JndiDataSourceLookup().getDataSource("jdbc/fixedDs"));
    } catch (LiquibaseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try {
      tm.begin();
      // TODO move to liquibase?
      cleanData(new JndiDataSourceLookup().getDataSource("jdbc/inTestDs"));
      cleanData(new JndiDataSourceLookup().getDataSource("jdbc/fixedDs"));
      tm.commit();
    } catch (NotSupportedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SystemException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalStateException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (HeuristicMixedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (HeuristicRollbackException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (RollbackException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @After
  public void tearDownXADataSourceTest() {
    ((BitronixTransactionManager) tm).shutdown();

    for (PoolingDataSource psd : poolingDSs) {
      try {
        psd.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    poolingDSs = null;

    for (Resource resource : jndiResources) {
      try {
        resource.release();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    jndiResources = null;
  }

  @Test
  public void twoPhaseCommitDataPersistedOnCommit() {
    try {
      tm.begin();
      insertData(new JndiDataSourceLookup().getDataSource("jdbc/inTestDs"));
      insertData(new JndiDataSourceLookup().getDataSource("jdbc/fixedDs"));
      tm.commit();

      tm.begin();
      assertEquals(1, queryForInt(new JndiDataSourceLookup().getDataSource("jdbc/inTestDs")));
      assertEquals(1, queryForInt(new JndiDataSourceLookup().getDataSource("jdbc/fixedDs")));
      tm.commit();

    } catch (NotSupportedException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (SystemException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (IllegalStateException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (SecurityException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (HeuristicMixedException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (HeuristicRollbackException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (RollbackException e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void twoPhaseCommitDataNotPersistedOnRollback() {
    try {
      tm.begin();
      insertData(new JndiDataSourceLookup().getDataSource("jdbc/inTestDs"));
      insertData(new JndiDataSourceLookup().getDataSource("jdbc/fixedDs"));
      tm.rollback();

      tm.begin();
      assertEquals(0, queryForInt(new JndiDataSourceLookup().getDataSource("jdbc/inTestDs")));
      assertEquals(0, queryForInt(new JndiDataSourceLookup().getDataSource("jdbc/fixedDs")));
      tm.commit();

    } catch (NotSupportedException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (SystemException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (IllegalStateException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (SecurityException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (HeuristicMixedException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (HeuristicRollbackException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (RollbackException e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  private void cleanData(DataSource dataSource) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      P6TestUtil.execute(connection, "delete from customers where id=50");
    } catch (SQLException e) {
      e.printStackTrace();
      Assert.fail();
    } finally {
      try {
        if (null != connection) {
          connection.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private void insertData(DataSource ds) {
    Connection connection = null;
    try {
      connection = ds.getConnection();
      P6TestUtil.execute(connection, "insert into customers(id,name) values (50,'foo')");
    } catch (SQLException e) {
      e.printStackTrace();
      Assert.fail();
    } finally {
      try {
        if (connection != null) {
          connection.close();	  
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private int queryForInt(DataSource ds) {
    Connection connection = null;
    try {
      connection = ds.getConnection();

      // add different data to each connection
      return P6TestUtil.queryForInt(connection, "select count(*) from customers where id=50");
    } catch (SQLException e) {
      e.printStackTrace();
      Assert.fail();
    } finally {
      try {
        if (connection != null) {
          connection.close();     
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return -1;
  }

  public void setXADSProperties(XADataSource ds, String url, String userName, String password)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

    if (url.contains(":derby:") || url.contains(":sqlfire:")) {
      PropertyUtils.setProperty(ds, "databaseName", "p6spy_xds");
      PropertyUtils.setProperty(ds, "createDatabase", "create");
      return;
    } else if (url.contains(":firebirdsql:")) {
      PropertyUtils.setProperty(ds, "databaseName", url.replace("jdbc:firebirdsql:", ""));
    } else if (PropertyUtils.isWriteable(ds, "URL")) {
      PropertyUtils.setProperty(ds, "URL", url);
    } else if (PropertyUtils.isWriteable(ds, "Url")) {
      PropertyUtils.setProperty(ds, "Url", url);
    } else if (PropertyUtils.isWriteable(ds, "url")) {
      PropertyUtils.setProperty(ds, "url", url);
    } else if (PropertyUtils.isWriteable(ds, "serverName")
        && PropertyUtils.isWriteable(ds, "portNumber")
        && PropertyUtils.isWriteable(ds, "databaseName") && URL_PATTERN.matcher(url).matches()) {

      final Matcher matcher = URL_PATTERN.matcher(url);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("url in incorrect format: " + url);
      }
      final String host = matcher.group(2);
      final String port = matcher.group(3);
      final String db = matcher.group(4);

      PropertyUtils.setProperty(ds, "serverName", host);
      if (null != port && !port.isEmpty()) {
        PropertyUtils.setProperty(ds, "portNumber", Integer.parseInt(port));
      }
      PropertyUtils.setProperty(ds, "databaseName", db);
    } else {
      throw new IllegalArgumentException(
          "Datasource imlpementation not supported by tests (yet) (for url setting): " + ds);
    }

    if (PropertyUtils.isWriteable(ds, "userName")) {
      PropertyUtils.setProperty(ds, "userName", userName);
    } else if (PropertyUtils.isWriteable(ds, "user")) {
      PropertyUtils.setProperty(ds, "user", userName);
    } else {
      throw new IllegalArgumentException(
          "Datasource imlpementation not supported by tests (yet) (for username setting): " + ds);
    }

    if (PropertyUtils.isWriteable(ds, "password")) {
      PropertyUtils.setProperty(ds, "password", password);
    } else {
      throw new IllegalArgumentException(
          "Datasource implementation not supported by tests (yet) (for password setting): " + ds);
    }
  }
}
