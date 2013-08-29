package com.p6spy.engine.spy;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6Util;

@RunWith(Parameterized.class)
public class P6TestDriverMulti extends P6TestFramework {

  private static final Collection<Object[]> DBS_IN_TEST = Arrays.asList(new Object[][] { { "multidriver" } , { "multidb" } });
  
  public P6TestDriverMulti(String db) throws SQLException, IOException {
    super(db);
  }

  /**
   * Always returns {@link DBS_IN_TEST} as we don't
   * need to rerun for each DB here, rather we run for multiple DBs in one shot.
   * 
   * @return {@link DBS_IN_TEST}
   */
  @Parameters
  public static Collection<Object[]> dbs() {
    return DBS_IN_TEST;
  }
  
  @Test
  public void testMultiDriver() throws SQLException {
    Statement statement2 = null;

      try {
          // rebuild a the 2.nd connection for the multi-driver test
          Properties props = loadProperties(p6TestProperties);
          String drivername = props.getProperty("p6driver2");
          String user = props.getProperty("user2");
          String password = props.getProperty("password2");
          String url = props.getProperty("url2");

          P6Util.forName(drivername);
          System.err.println("REGISTERED: "+drivername);
          printAllDrivers();
          Driver driver = DriverManager.getDriver(url);
          System.err.println("FRAMEWORK USING DRIVER == " + driver.getClass().getName() + " FOR URL " + url);
          Connection conn2 = DriverManager.getConnection(url, user, password);
          statement2 = conn2.createStatement();

          // the original
          Statement statement = connection.createStatement();

          // rebuild the tables
          statement.execute("drop table if exists multidriver_test2");
          statement2.execute("create table multidriver_test2 (col1 varchar(255), col2 integer)");

          // this should be fine
          String query = "select 'q1' from multidriver_test2";
          statement2.executeQuery(query);
          assertTrue(P6LogQuery.getLastEntry().indexOf(query) != -1);

          // this table should not exist
          try {
              query = "select 'q2' from multidriver_test2";
              statement.executeQuery(query);
              fail("Exception should have occured");
          } catch (Exception e) {
          }

          // this should be fine for the second connection
          query = "select 'b' from multidriver_test2";
          statement2.executeQuery(query);
          assertTrue(P6LogQuery.getLastEntry().indexOf(query) != -1);

          // this table should not exist
          try {
              query = "select 'q3' from common_test";
              statement2.executeQuery(query);
              fail("Exception should have occured");
          } catch (Exception e) {
          }

      } catch (Exception e) {
          printAllDrivers();
          fail(e.getMessage()+getStackTrace(e));
      } finally {
        if (null != statement2) {
          try {
              statement2.execute("drop table multidriver_test2");
          } finally {
            statement2.close();  
          }
        }
      }
  }

}
