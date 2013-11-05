/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
 * %%
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
 * #L%
 */
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
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.test.P6TestOptions;

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

        Collection<String> driverNames = P6SpyOptions.getActiveInstance().getDriverNames();
          if (driverNames != null && driverNames.isEmpty() && driverNames.size() > 1) {
            Iterator<String> iterator = driverNames.iterator();
            // skip the 1.st elem
            iterator.next();
            
            String driverName2 = iterator.next();
            if( driverName2 != null ) {
              P6Util.forName(driverName2);
              System.err.println("REGISTERED: "+driverName2);
            }  
          }
          String url2 = P6TestOptions.getActiveInstance().getUrl2();
          String user2 = P6TestOptions.getActiveInstance().getUser2();
          String password2 = P6TestOptions.getActiveInstance().getPassword2();
          
          
          printAllDrivers();
          Driver driver = DriverManager.getDriver(url2);
          System.err.println("FRAMEWORK USING DRIVER == " + driver.getClass().getName() + " FOR URL " + url2);
          Connection conn2 = DriverManager.getConnection(url2, user2, password2);
          statement2 = conn2.createStatement();

          // the original
          Statement statement = connection.createStatement();

          // rebuild the tables
          statement.execute("drop table if exists multidriver_test2");
          statement2.execute("create table multidriver_test2 (col1 varchar(255), col2 integer)");

          // this should be fine
          String query = "select 'q1' from multidriver_test2";
          statement2.executeQuery(query);
          assertTrue(super.getLastLogEntry().contains(query));

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
          assertTrue(super.getLastLogEntry().contains(query));

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
