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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class P6TestBasics extends P6TestFramework {

    public P6TestBasics(String db) throws SQLException, IOException {
      super(db);
    }
    
    /* (non-Javadoc)
     * @see com.p6spy.engine.spy.P6TestFramework#setUpFramework()
     */
    @Override
    // just prevent inherited stuff
    public void setUpFramework() {
    }
    
    @After
    public void tearDown() throws SQLException {
      if (this.connection != null) {
        // close connection properly
        this.connection.close();
      }
    }
    
    @Test
    public void testNative() throws Exception {
        connection = P6TestUtil.loadDrivers(null);
        sqltests();
    }
    
    @Test
    public void testSpy() throws Exception {
        Collection<String> driverNames = P6SpyOptions.getActiveInstance().getDriverNames();
        if (null != driverNames && !driverNames.isEmpty()) {
          connection = P6TestUtil.loadDrivers(driverNames.iterator().next());
          sqltests();
        } 
    }

    protected void preparesql() throws SQLException {
        final Statement statement = connection.createStatement();
        drop(statement);
        statement.execute("create table basic_test (col1 varchar(255), col2 integer)");
        statement.close();
    }

    protected void sqltests() throws SQLException {
        preparesql();

        // insert test
        String insert = "insert into basic_test values (\'bob\', 5)";
        Statement statement = connection.createStatement();
        statement.executeUpdate(insert);

        // update test
        String update = "update basic_test set col1 = \'bill\' where col2 = 5";
        statement.executeUpdate(update);

        // query test
        String query = "select col1 from basic_test where col2 = 5";
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        assertEquals(rs.getString(1), "bill");

        // prepared test
        PreparedStatement ps = connection.prepareStatement("insert into basic_test values (?, ?)");
        ps.setString(1,"joe");
        ps.setInt(2,6);
        ps.executeUpdate();
        ps.setString(1,"andy");
        ps.setInt(2,7);
        ps.execute();

        ps = connection.prepareStatement("update basic_test set col1 = ? where col2 = ?");
        ps.setString(1,"charles");
        ps.setInt(2,6);
        ps.executeUpdate();
        ps.setString(1,"bobby");
        ps.setInt(2,7);
        ps.execute();

        ps = connection.prepareStatement("select col1 from basic_test where col1 = ? and col2 = ?");
        ps.setString(1,"charles");
        ps.setInt(2,6);
        rs = ps.executeQuery();
        rs.next();
        assertEquals("charles", rs.getString(1));
        
        ps.close();
    }

    protected void drop(Statement statement) {
      try {
        if (statement == null) {
          return;
        }
        statement.execute("drop table basic_test");
      } catch (Exception e) {
        // we don't really care about cleanup failing
      }
    }

}
