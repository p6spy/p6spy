
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
