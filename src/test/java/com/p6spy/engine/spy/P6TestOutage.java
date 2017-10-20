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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.test.P6TestFramework;
import com.p6spy.engine.test.P6TestOptions;

@RunWith(Parameterized.class)
public class P6TestOutage extends P6TestFramework {

  public P6TestOutage(String db) throws SQLException, IOException {
    super(db);
  }

  // make sure outage is properly configured
  private static final Collection<Object[]> DBS_IN_TEST = Arrays
      .asList(new Object[][]{{"outage"}});

  /**
   * Always returns {@link P6TestOptions#dbs()} as we don't need to rerun for each DB here, rather
   * we run for the specific config only.
   *
   * @return
   */
  @Parameters
  public static Collection<Object[]> dbs() {
    return DBS_IN_TEST;
  }

  @Test
  public void testOutage() throws SQLException {
    // exec fast query => no outage detected
    callSleep(1);
    Assert.assertFalse(super.getLastLogEntry().contains(Category.OUTAGE.toString()));
    Assert.assertTrue(super.getLastLogEntry().contains("CALL SLEEP"));
    Assert.assertTrue(getLastTimeElapsed() >= 1);

    // exec slooooow query => outage detected
    callSleep(3000);
    Assert.assertTrue(super.getLastButOneLogEntry().contains(Category.OUTAGE.toString()));
    Assert.assertTrue(super.getLastLogEntry().contains("CALL SLEEP"));
    Assert.assertTrue(getLastTimeElapsed() >= 3000);
  }

  @Test
  public void testCallingSetMethodsOnStatementInterface() throws SQLException {

    // not a great test - just protecting against regression for issue #275

    String sql = "select * from customers where id = ?";
    PreparedStatement prep = connection.prepareStatement(sql);

    prep.setMaxRows(1);
    assertEquals(1, prep.getMaxRows());

    prep.close();
  }

  private void callSleep(long milliseconds) throws SQLException {
    final Statement statement = connection.createStatement();
    statement.executeQuery("CALL SLEEP( " + milliseconds + ")");
    statement.close();
  }
}
