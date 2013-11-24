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

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.test.P6TestFramework;
import com.p6spy.engine.test.P6TestOptions;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;


/*
   This test does not make sense any longer
 */
@Ignore
@RunWith(Parameterized.class)
public class P6TestDriverMulti extends P6TestFramework {
  private static final Logger log = Logger.getLogger(P6TestDriverMulti.class);

  private static final Collection<Object[]> DBS_IN_TEST = Arrays.asList(new Object[][]{{"multidriver"}, {"multidb"}});

  public P6TestDriverMulti(String db) throws SQLException, IOException {
    super(db);
  }

  /**
   * Always returns {@link #DBS_IN_TEST} as we don't
   * need to rerun for each DB here, rather we run for multiple DBs in one shot.
   *
   * @return {@link #DBS_IN_TEST}
   */
  @Parameters
  public static Collection<Object[]> dbs() {
    return DBS_IN_TEST;
  }

  @Test
  public void testMultiDriver() throws Exception {
    // rebuild a the 2.nd connection for the multi-driver test
    Collection<String> driverNames = P6SpyOptions.getActiveInstance().getDriverNames();
    if (driverNames != null && driverNames.isEmpty() && driverNames.size() > 1) {
      Iterator<String> iterator = driverNames.iterator();
      // skip the 1.st elem
      iterator.next();

      String driverName2 = iterator.next();
      if (driverName2 != null) {
        P6Util.forName(driverName2);
        log.info("REGISTERED: " + driverName2);
      }
    }
    String url2 = P6TestOptions.getActiveInstance().getUrl2();
    String user2 = P6TestOptions.getActiveInstance().getUser2();
    String password2 = P6TestOptions.getActiveInstance().getPassword2();

    P6TestUtil.printAllDrivers();
    Driver driver = DriverManager.getDriver(url2);
    System.err.println("FRAMEWORK USING DRIVER == " + driver.getClass().getName() + " FOR URL " + url2);
    Connection connection2 = DriverManager.getConnection(url2, user2, password2);

    // setup database for testing
    P6TestUtil.setupTestData(url2, user2, password2);

    // add different data to each connection
    P6TestUtil.execute(connection, "insert into customers(id,name) values (100,'you')");
    P6TestUtil.execute(connection2, "insert into customers(id,name) values (101,'me')");

    // verify data in connection 1
    assertEquals(1, P6TestUtil.queryForInt(connection, "select count(*) from customers where id=100"));
    assertEquals(0, P6TestUtil.queryForInt(connection, "select count(*) from customers where id=101"));

    // verify data in connection 2
    assertEquals(0, P6TestUtil.queryForInt(connection2, "select count(*) from customers where id=100"));
    assertEquals(1, P6TestUtil.queryForInt(connection2, "select count(*) from customers where id=101"));

  }


}
