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

package com.p6spy.engine.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HsqlProcedures {

  public static void testProcRs(Connection connection, String custName, ResultSet[] result) throws SQLException {
    Statement statement = connection.createStatement();
    ResultSet rs = statement.executeQuery("select * from customers where name like '%" + custName + "%'");
    result[0] = rs;
  }

  public static void testProc(Integer notUsed1, String notUsed2, Integer[] result) throws SQLException {
    result[0] = 2;
  }



}
