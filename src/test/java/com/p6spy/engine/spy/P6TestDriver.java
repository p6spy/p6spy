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
import java.sql.Driver;
import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.p6spy.engine.test.P6TestFramework;

@RunWith(Parameterized.class)
public class P6TestDriver extends P6TestFramework {

  public P6TestDriver(String db) throws SQLException, IOException {
    super(db);
  }

  @Test
  public void testVersion() throws Exception {
    Driver driver = new P6SpyDriver();
    assertEquals(2, driver.getMajorVersion());
    assertEquals(0, driver.getMinorVersion());
  }

}
