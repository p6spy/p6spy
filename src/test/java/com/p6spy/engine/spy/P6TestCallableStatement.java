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

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.spy.appender.P6TestLogger;
import com.p6spy.engine.test.P6TestFramework;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class P6TestCallableStatement extends P6TestFramework {
  private static final Logger log = Logger.getLogger(P6TestCallableStatement.class);

  // H2 is the only db with stored procs defined currently
  private static final Collection<String> testWithStoredProcs = Arrays.asList("H2");

  public P6TestCallableStatement(String db) throws SQLException, IOException {
    super(db);
  }
  
  private boolean storedProcTestingEnabled() {
    return testWithStoredProcs.contains(db);
  }
  
  

  @Test
  public void testCallable() throws SQLException {
    if( db.equals("SQLite")) {
      // sqllite does not support callable statements!
      return;
    }

    // tests inspired by: http://opensourcejavaphp.net/java/h2/org/h2/test/jdbc/TestCallableStatement.java.html
    {
      CallableStatement call = connection.prepareCall("insert into contacts(id,name) values(?, ?)");
      call.setInt(1, 100);
      call.setString(2, "David");
      call.execute();
      call.close();

      assertTrue(((P6TestLogger) P6LogQuery.getLogger()).getLastEntry().indexOf("insert into contacts") != -1);
    }

    {
      CallableStatement call = connection.prepareCall("select id,name from contacts where id=100", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      ResultSet rs = call.executeQuery();
      rs.next();
      assertEquals(100, rs.getInt(1));
      assertEquals("David", rs.getString(2));
      assertFalse(rs.next());
      rs.close();
      call.close();

      assertTrue(((P6TestLogger) P6LogQuery.getLogger()).getLastEntry().indexOf("select id,name from contacts") != -1);
    }

    {
      CallableStatement call = connection.prepareCall("select id,name from contacts where id=100", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
      ResultSet rs = call.executeQuery();
      rs.next();
      assertEquals(100, rs.getInt(1));
      assertEquals("David", rs.getString(2));
      assertFalse(rs.next());
      rs.close();
      call.close();

      assertTrue(((P6TestLogger) P6LogQuery.getLogger()).getLastEntry().indexOf("select id,name from contacts") != -1);
    }

  }

  @Test
  public void testStoredProcedureNoResultSet() throws SQLException {
    if( db.equals("SQLite")) {
      // sqllite does not support callable statements!
      return;
    }
    if( !storedProcTestingEnabled() ) {
      return;
    }
    
    this.clearLogEnties();

    // execute the statement
    String query = "? = call test_proc(?,?)";
    CallableStatement call = connection.prepareCall(query);
    call.registerOutParameter(1, Types.INTEGER);
    call.setInt(2, 1);
    call.setString(3, "hi");
    call.execute();
    int retVal = call.getInt(1);
    assertEquals(2, retVal);
    call.close();

    // the last log message should have the original query
    assertTrue(getLastLogEntry().contains(query));

    // verify that the bind parameters are resolved in the log message
    assertTrue(getLastLogEntry().contains("1,'hi'"));


  }

  @Test
  public void testStoredProcedureWithNullInputParameter() throws SQLException {
    if( db.equals("SQLite")) {
      // sqllite does not support callable statements!
      return;
    }
    if( !storedProcTestingEnabled() ) {
      return;
    }
    
    this.clearLogEnties();

    // execute the statement
    String query = "? = call test_proc(?,?)";
    CallableStatement stmt = connection.prepareCall(query);
    stmt.registerOutParameter(1, Types.INTEGER);
    stmt.setInt(2, 1);
    stmt.setNull(3, Types.VARCHAR);
    stmt.execute();
    int retVal = stmt.getInt(1);
    assertEquals(2, retVal);
    stmt.close();

    // verify that the third parameter is NULL
    assertTrue(getLastLogEntry().contains("1,NULL"));


  }

}
