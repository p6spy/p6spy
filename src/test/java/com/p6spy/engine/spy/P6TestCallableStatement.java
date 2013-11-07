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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class P6TestCallableStatement extends P6TestPreparedStatement {

  private static final Collection<Object[]> DBS_IN_TEST = Arrays.asList(new Object[][] { { "H2" } });
  
  /**
   * Always returns {@link #DBS_IN_TEST} as we don't
   * need to rerun for each DB here.
   * The thing is that not all the DBs support stored procedures. Morever syntax might differ. 
   * We want just to prove that callable statements are correctly prxied.
   * So let's test just with H2 (default DB).
   * 
   * @return {@link #DBS_IN_TEST}
   */
  @Parameters
  public static Collection<Object[]> dbs() {
    return DBS_IN_TEST;
  }

  
	public P6TestCallableStatement(String db) throws SQLException, IOException {
    super(db);
  }
	  
    @Test
    public void testCallable() throws SQLException {
      
      // tests inspired by: http://opensourcejavaphp.net/java/h2/org/h2/test/jdbc/TestCallableStatement.java.html
      Statement stat = connection.createStatement();
      {
        stat.execute("CREATE TABLE TEST(ID INT, NAME VARCHAR)");
        CallableStatement call = connection.prepareCall("INSERT INTO TEST VALUES(?, ?)");
        call.setInt(1, 1);
        call.setString(2, "Hello");
        call.execute();
      
        assertTrue(((P6TestLogger) P6LogQuery.getLogger()).getLastEntry().indexOf("INSERT INTO TEST VALUES") != -1);
      }
     
      {
        CallableStatement call = connection.prepareCall("SELECT * FROM TEST", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = call.executeQuery();
        rs.next();
        assertEquals(1, rs.getInt(1));
        assertEquals("Hello", rs.getString(2));
        assertFalse(rs.next());
        
        assertTrue(((P6TestLogger) P6LogQuery.getLogger()).getLastEntry().indexOf("SELECT * FROM TEST") != -1);
      }
      
      {
        CallableStatement call = connection.prepareCall("SELECT * FROM TEST", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
        ResultSet rs = call.executeQuery();
        rs.next();
        assertEquals(1, rs.getInt(1));
        assertEquals("Hello", rs.getString(2));
        assertFalse(rs.next());

        assertTrue(((P6TestLogger) P6LogQuery.getLogger()).getLastEntry().indexOf("SELECT * FROM TEST") != -1);
      }
      
  }
  
  @Test
  public void testStoredProcedureNoResultSet() throws SQLException {
    this.clearLogEnties();

    // register the stored proc with the database - only for H2!!!!
    connection.createStatement().execute("create alias TEST_PROC for \""+this.getClass().getName()+".testProc\"");
    
    // execute the statement
    String query ="? = call TEST_PROC(?,?)";
    CallableStatement stmt = connection.prepareCall(query);
    stmt.registerOutParameter(1, Types.INTEGER);
    stmt.setInt(2, 1);
    stmt.setString(3,"hi");
    stmt.execute();
    int retVal = stmt.getInt(1);
    assertEquals(2, retVal);
    
    // the last log message should have the original query
    assertTrue(getLastLogEntry().contains(query));
    
    // verify that the bind parameters are resolved in the log message
    assertTrue(getLastLogEntry().contains("1,'hi'"));
    
    
  }
  
  @Test
  public void testStoredProcedureWithNullInputParameter() throws SQLException {
    this.clearLogEnties();

    // register the stored proc with the database - only for H2!!!!
    connection.createStatement().execute("create alias TEST_PROC for \""+this.getClass().getName()+".testProc\"");
    
    // execute the statement
    String query ="? = call TEST_PROC(?,?)";
    CallableStatement stmt = connection.prepareCall(query);
    stmt.registerOutParameter(1, Types.INTEGER);
    stmt.setInt(2, 1);
    stmt.setNull(3,Types.VARCHAR);
    stmt.execute();
    int retVal = stmt.getInt(1);
    assertEquals(2, retVal);
    
    // verify that the third parameter is NULL
    assertTrue(getLastLogEntry().contains("1,NULL"));
    
    
  }
  
  public static int testProc(int param1, String param2) {
    return 2;
  }

}
