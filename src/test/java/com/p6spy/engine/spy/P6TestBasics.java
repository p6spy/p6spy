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

import com.p6spy.engine.test.P6TestFramework;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class P6TestBasics extends P6TestFramework {

  public P6TestBasics(String db) throws SQLException, IOException {
    super(db);
  }

  @Test
  public void sqltests() throws SQLException {

    // insert test
    String insert = "insert into customers(name,id) values (\'bob\', 100)";
    Statement statement = connection.createStatement();
    statement.executeUpdate(insert);

    // update test
    String update = "update customers set name = \'bill\' where id = 100";
    statement.executeUpdate(update);

    // query test
    String query = "select name from customers where id = 100";
    ResultSet rs = statement.executeQuery(query);
    rs.next();
    assertEquals(rs.getString(1), "bill");
    rs.close();
    statement.close();

    // prepared test
    PreparedStatement ps = connection.prepareStatement("insert into customers(name,id) values (?, ?)");
    ps.setString(1, "joe");
    ps.setInt(2, 200);
    ps.executeUpdate();
    ps.setString(1, "andy");
    ps.setInt(2, 201);
    ps.execute();
    ps.close();

    ps = connection.prepareStatement("update customers set name = ? where id = ?");
    ps.setString(1, "charles");
    ps.setInt(2, 200);
    ps.executeUpdate();
    ps.setString(1, "bobby");
    ps.setInt(2, 201);
    ps.execute();
    ps.close();


    ps = connection.prepareStatement("select name from customers where name = ? and id = ?");
    ps.setString(1, "charles");
    ps.setInt(2, 200);
    rs = ps.executeQuery();
    rs.next();
    assertEquals("charles", rs.getString(1));
    rs.close();

    ps.close();
  }


}
