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
package com.p6spy.engine.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.sql.DataSource;
import javax.sql.PooledConnection;

import com.p6spy.engine.common.CallableStatementInformation;
import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.spy.JdbcEventListenerFactory;
import com.p6spy.engine.spy.P6DataSource;
import com.p6spy.engine.spy.P6PooledConnection;
import com.p6spy.engine.spy.P6SpyDriver;
import com.p6spy.engine.test.P6TestFactory;
import com.p6spy.engine.test.P6TestFramework;
import com.p6spy.engine.wrapper.CallableStatementWrapper;
import com.p6spy.engine.wrapper.ConnectionWrapper;
import com.p6spy.engine.wrapper.PreparedStatementWrapper;
import com.p6spy.engine.wrapper.StatementWrapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CompoundJdbcEventListenerTest {

  @Mock
  private DataSource mockedDataSource;
  @Mock
  private PooledConnection mockedPooledConnection;
  @Mock
  private JdbcEventListener mockedJdbcListener;

  private DataSource wrappedDataSource;
  private PooledConnection wrappedPooledConnection;

  private Connection wrappedConnection;
  private Statement wrappedStatement;
  private PreparedStatement wrappedPreparedStatement;
  private CallableStatement wrappedCallableStatement;

  private ConnectionInformation connectionInformation;
  private StatementInformation statementInformation;
  private PreparedStatementInformation preparedStatementInformation;
  private CallableStatementInformation callableStatementInformation;

  private static final String SQL = "SELECT * FROM DUAL";

  @Before
  public void before() throws SQLException, IOException {
    P6TestFactory.setJdbcEventListener(mockedJdbcListener);
    new P6TestFramework("H2") {
    };

    final Connection mockedConnection = mock(Connection.class);
    final Statement mockedStatement = mock(Statement.class);
    final PreparedStatement mockedPreparedStatement = mock(PreparedStatement.class);
    final CallableStatement mockedCallableStatement = mock(CallableStatement.class);

    wrappedDataSource = new P6DataSource(mockedDataSource);
    ((P6DataSource) wrappedDataSource).setJdbcEventListenerFactory(new JdbcEventListenerFactory() {
      @Override
      public JdbcEventListener createJdbcEventListener() {
        return mockedJdbcListener;
      }
    });
    wrappedPooledConnection = new P6PooledConnection(mockedPooledConnection, new JdbcEventListenerFactory() {
      @Override
      public JdbcEventListener createJdbcEventListener() {
        return mockedJdbcListener;
      }
    });
    when(mockedDataSource.getConnection()).thenReturn(mockedConnection);
    when(mockedDataSource.getConnection(anyString(), anyString())).thenReturn(mockedConnection);
    when(mockedPooledConnection.getConnection()).thenReturn(mockedConnection);

    connectionInformation = ConnectionInformation.fromTestConnection(mockedConnection);
    statementInformation = new StatementInformation(connectionInformation);
    preparedStatementInformation = new PreparedStatementInformation(connectionInformation, "SELECT * FROM DUAL");
    callableStatementInformation = new CallableStatementInformation(connectionInformation, "SELECT * FROM DUAL");

    @SuppressWarnings("resource")
    Connection connectionWrapper = ConnectionWrapper.wrap(mockedConnection, mockedJdbcListener, connectionInformation);
    wrappedConnection = connectionWrapper;
    verify(mockedJdbcListener).onConnectionWrapped(eq(connectionInformation));
    wrappedStatement = StatementWrapper.wrap(mockedStatement, statementInformation, mockedJdbcListener);
    wrappedPreparedStatement = PreparedStatementWrapper.wrap(mockedPreparedStatement, preparedStatementInformation,
        mockedJdbcListener);
    wrappedCallableStatement = CallableStatementWrapper.wrap(mockedCallableStatement, callableStatementInformation,
        mockedJdbcListener);
    
    P6SpyDriver.setJdbcEventListenerFactory(new JdbcEventListenerFactory() {
      @Override
      public JdbcEventListener createJdbcEventListener() {
        return mockedJdbcListener;
      }
    });
  }

  @After
  public void after() throws Exception {
    P6TestFactory.setJdbcEventListener(null);
    P6SpyDriver.setJdbcEventListenerFactory(null);
  }

  @Test
  public void testConnectionOnBeforeAfterGetConnectionFromDataSource() throws SQLException {
    wrappedDataSource.getConnection();
    wrappedDataSource.getConnection("test", "test");
    verify(mockedJdbcListener, times(2)).onBeforeGetConnection(connectionInformationWithConnection());
    verify(mockedJdbcListener, times(2)).onAfterGetConnection(connectionInformationWithConnection(), ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testConnectionOnAfterGetConnectionAfterGettingFromDataSourceWithThrowingSQLException() throws SQLException {
    SQLException sqle = new SQLException();
    when(mockedDataSource.getConnection()).thenThrow(sqle);
    when(mockedDataSource.getConnection(anyString(), anyString())).thenThrow(sqle);

    try {
      wrappedDataSource.getConnection();
      Assert.fail("exception should be thrown");
    } catch (SQLException expected) {
    }
    try {
      wrappedDataSource.getConnection("test", "test");
      Assert.fail("exception should be thrown");
    } catch (SQLException expected) {
    }
    verify(mockedJdbcListener, times(2)).onAfterGetConnection(connectionInformationWithoutConnection(), eq(sqle));
  }

  @Test
  public void testConnectionOnBeforeAfterGetConnectionFromDriver() throws SQLException {
    DriverManager.getConnection("jdbc:p6spy:h2:mem:p6spy", "sa", null);
    verify(mockedJdbcListener).onBeforeGetConnection(connectionInformationWithConnection());
    verify(mockedJdbcListener).onAfterGetConnection(connectionInformationWithConnection(), ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testConnectionOnAfterGetConnectionAfterGettingFromDriverWithThrowingSQLException() throws SQLException {
    try {
      DriverManager.getConnection("jdbc:p6spy:h2:tcp://dev/null/", "sa", null);
      Assert.fail("exception should be thrown");
    } catch (SQLException expected) {
    }
    verify(mockedJdbcListener).onAfterGetConnection(connectionInformationWithoutConnection(), any(SQLException.class));
  }

  @Test
  public void testConnectionOnBeforeAfterGetConnectionFromPooledConnection() throws SQLException {
    wrappedPooledConnection.getConnection();
    verify(mockedJdbcListener).onBeforeGetConnection(connectionInformationWithConnection());
    verify(mockedJdbcListener).onAfterGetConnection(connectionInformationWithConnection(), ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testConnectionOnAfterGetConnectionAfterGettingFromPooledConnectionWithThrowingSQLException() throws SQLException {
    SQLException sqle = new SQLException();
    when(mockedPooledConnection.getConnection()).thenThrow(sqle);

    try {
      wrappedPooledConnection.getConnection();
      Assert.fail("exception should be thrown");
    } catch (SQLException expected) {
    }
    verify(mockedJdbcListener).onAfterGetConnection(connectionInformationWithoutConnection(), eq(sqle));
  }

  @Test
  public void testConnectionOnConnectionWrapped() throws SQLException {
    // verification done in the before() method already
  }

  @Test
  public void testPreparedStatementOnBeforeAddBatch() throws SQLException {
    wrappedPreparedStatement.addBatch();
    verify(mockedJdbcListener).onBeforeAddBatch(eq(preparedStatementInformation));
  }

  @Test
  public void testPreparedStatementOnAfterAddBatch() throws SQLException {
    testPreparedStatementOnBeforeAddBatch();
    verify(mockedJdbcListener).onAfterAddBatch(eq(preparedStatementInformation), anyLong(),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testStatementOnBeforeAddBatch() throws SQLException {
    wrappedStatement.addBatch(SQL);
    verify(mockedJdbcListener).onBeforeAddBatch(eq(statementInformation), eq(SQL));
  }

  @Test
  public void testStatementOnAfterAddBatch() throws SQLException {
    testStatementOnBeforeAddBatch();
    verify(mockedJdbcListener).onAfterAddBatch(eq(statementInformation), anyLong(), eq(SQL),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testPreparedStatementOnBeforeExecute() throws SQLException {
    wrappedPreparedStatement.execute();
    verify(mockedJdbcListener).onBeforeExecute(eq(preparedStatementInformation));
  }

  @Test
  public void testPreparedStatementOnAfterExecute() throws SQLException {
    testPreparedStatementOnBeforeExecute();
    verify(mockedJdbcListener).onAfterExecute(eq(preparedStatementInformation), anyLong(),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testStatementOnBeforeExecute() throws SQLException {
    wrappedStatement.execute(SQL);
    verify(mockedJdbcListener).onBeforeExecute(eq(statementInformation), eq(SQL));
  }

  @Test
  public void testStatementOnAfterExecute() throws SQLException {
    testStatementOnBeforeExecute();
    verify(mockedJdbcListener).onAfterExecute(eq(statementInformation), anyLong(), eq(SQL),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testPreparedStatementOnBeforeExecuteBatch() throws SQLException {
    wrappedPreparedStatement.executeBatch();
    verify(mockedJdbcListener).onBeforeExecuteBatch(eq(preparedStatementInformation));
  }

  @Test
  public void testPreparedStatementOnAfterExecuteBatch() throws SQLException {
    testPreparedStatementOnBeforeExecuteBatch();
    verify(mockedJdbcListener).onAfterExecuteBatch(eq(preparedStatementInformation), anyLong(),
        ArgumentMatchers.<int[]>isNull(), ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testStatementOnBeforeExecuteBatch() throws SQLException {
    wrappedStatement.executeBatch();
    verify(mockedJdbcListener).onBeforeExecuteBatch(eq(statementInformation));
  }

  @Test
  public void testStatementOnAfterExecuteBatch() throws SQLException {
    testStatementOnBeforeExecuteBatch();
    verify(mockedJdbcListener).onAfterExecuteBatch(eq(statementInformation), anyLong(),
        ArgumentMatchers.<int[]>isNull(), ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testPreparedStatementOnBeforeExecuteUpdate() throws SQLException {
    wrappedPreparedStatement.executeUpdate();
    verify(mockedJdbcListener).onBeforeExecuteUpdate(eq(preparedStatementInformation));
  }

  @Test
  public void testPreparedStatementOnAfterExecuteUpdate() throws SQLException {
    testPreparedStatementOnBeforeExecuteUpdate();
    verify(mockedJdbcListener).onAfterExecuteUpdate(eq(preparedStatementInformation), anyLong(), anyInt(),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testStatementOnBeforeExecuteUpdate() throws SQLException {
    wrappedStatement.executeUpdate(SQL);
    verify(mockedJdbcListener).onBeforeExecuteUpdate(eq(statementInformation), eq(SQL));
  }

  @Test
  public void testStatementOnAfterExecuteUpdate() throws SQLException {
    testStatementOnBeforeExecuteUpdate();
    verify(mockedJdbcListener).onAfterExecuteUpdate(eq(statementInformation), anyLong(), eq(SQL), anyInt(),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testPreparedStatementOnBeforeExecuteQuery() throws SQLException {
    wrappedPreparedStatement.executeQuery();
    verify(mockedJdbcListener).onBeforeExecuteQuery(eq(preparedStatementInformation));
  }

  @Test
  public void testPreparedStatementOnAfterExecuteQuery() throws SQLException {
    testPreparedStatementOnBeforeExecuteQuery();
    verify(mockedJdbcListener).onAfterExecuteQuery(eq(preparedStatementInformation), anyLong(),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testStatementOnBeforeExecuteQuery() throws SQLException {
    wrappedStatement.executeQuery(SQL);
    verify(mockedJdbcListener).onBeforeExecuteQuery(eq(statementInformation), eq(SQL));
  }

  @Test
  public void testStatementOnAfterExecuteQuery() throws SQLException {
    testStatementOnBeforeExecuteQuery();
    verify(mockedJdbcListener).onAfterExecuteQuery(eq(statementInformation), anyLong(), eq(SQL),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetNull() throws SQLException {
    wrappedPreparedStatement.setNull(0, 0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), any(),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetNullWithTypeName() throws SQLException {
    wrappedPreparedStatement.setNull(0, 0, "foo");
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), any(),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetBoolean() throws SQLException {
    wrappedPreparedStatement.setBoolean(0, false);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(false),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetByte() throws SQLException {
    wrappedPreparedStatement.setByte(0, (byte) 0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq((byte) 0),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetShort() throws SQLException {
    wrappedPreparedStatement.setShort(0, (short) 0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq((short) 0),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetInt() throws SQLException {
    wrappedPreparedStatement.setInt(0, 0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(0),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetLong() throws SQLException {
    wrappedPreparedStatement.setLong(0, 0L);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(0L),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetFloat() throws SQLException {
    wrappedPreparedStatement.setFloat(0, 0.0f);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(0.0f),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetDouble() throws SQLException {
    wrappedPreparedStatement.setDouble(0, 0.0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(0.0),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetBigDecimal() throws SQLException {
    wrappedPreparedStatement.setBigDecimal(0, new BigDecimal(0));
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(new BigDecimal(0)),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetString() throws SQLException {
    wrappedPreparedStatement.setString(0, "foo");
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq("foo"),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetBytes() throws SQLException {
    wrappedPreparedStatement.setBytes(0, new byte[0]);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(new byte[0]),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetDate() throws SQLException {
    final Date date = mock(Date.class);
    wrappedPreparedStatement.setDate(0, date);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(date),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetDateWithCalendar() throws SQLException {
    final Date date = mock(Date.class);
    final Calendar calendar = mock(Calendar.class);
    wrappedPreparedStatement.setDate(0, date, calendar);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(date),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetTime() throws SQLException {
    final Time time = mock(Time.class);
    wrappedPreparedStatement.setTime(0, time);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(time),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetTimeWithCalendar() throws SQLException {
    final Time time = mock(Time.class);
    final Calendar calendar = mock(Calendar.class);
    wrappedPreparedStatement.setTime(0, time, calendar);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(time),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetTimestamp() throws SQLException {
    final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    wrappedPreparedStatement.setTimestamp(0, timestamp);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(timestamp),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetTimestampWithCalendar() throws SQLException {
    final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    final Calendar calendar = mock(Calendar.class);
    wrappedPreparedStatement.setTimestamp(0, timestamp, calendar);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(timestamp),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetAsciiStream() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedPreparedStatement.setAsciiStream(0, is);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetAsciiStreamWithIntLength() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedPreparedStatement.setAsciiStream(0, is, 0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetAsciiStreamWithLongLength() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedPreparedStatement.setAsciiStream(0, is, 0L);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetUnicodeStream() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedPreparedStatement.setUnicodeStream(0, is, 0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetBinaryStream() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedPreparedStatement.setBinaryStream(0, is);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetBinaryStreamWithIntLength() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedPreparedStatement.setBinaryStream(0, is, 0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetBinaryStreamWithLongLength() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedPreparedStatement.setBinaryStream(0, is, 0L);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetObject() throws SQLException {
    final Object object = new Object();
    wrappedPreparedStatement.setObject(0, object);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(object),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetObjectWithTargetType() throws SQLException {
    final Object object = new Object();
    wrappedPreparedStatement.setObject(0, object, 0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(object),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetObjectWithTargetTypeAndScaleOrLenght() throws SQLException {
    final Object object = new Object();
    wrappedPreparedStatement.setObject(0, object, 0, 0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(object),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetCharacterStream() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedPreparedStatement.setCharacterStream(0, reader);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetCharacterStreamWithIntLength() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedPreparedStatement.setCharacterStream(0, reader, 0);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetCharacterStreamWithLongLength() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedPreparedStatement.setCharacterStream(0, reader, 0L);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetRef() throws SQLException {
    final Ref ref = mock(Ref.class);
    wrappedPreparedStatement.setRef(0, ref);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(ref),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetBlob() throws SQLException {
    final Blob blob = mock(Blob.class);
    wrappedPreparedStatement.setBlob(0, blob);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(blob),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetClob() throws SQLException {
    final Clob clob = mock(Clob.class);
    wrappedPreparedStatement.setClob(0, clob);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(clob),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetClobWithReader() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedPreparedStatement.setClob(0, reader);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetClobWithReaderAndLength() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedPreparedStatement.setClob(0, reader, 0L);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetArray() throws SQLException {
    final Array array = mock(Array.class);
    wrappedPreparedStatement.setArray(0, array);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(array),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetURL() throws SQLException, MalformedURLException {
    final URL url = new URL("http://google.com");
    wrappedPreparedStatement.setURL(0, url);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(url),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetRowId() throws SQLException {
    final RowId rowId = mock(RowId.class);
    wrappedPreparedStatement.setRowId(0, rowId);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(rowId),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetNString() throws SQLException {
    final RowId rowId = mock(RowId.class);
    wrappedPreparedStatement.setRowId(0, rowId);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(rowId),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetNCharacterStream() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedPreparedStatement.setNCharacterStream(0, reader);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetNCharacterStreamWithLength() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedPreparedStatement.setNCharacterStream(0, reader, 0L);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetNClob() throws SQLException {
    final NClob nClob = mock(NClob.class);
    wrappedPreparedStatement.setNClob(0, nClob);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(nClob),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetNClobWithReader() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedPreparedStatement.setNClob(0, reader);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetNClobWithReaderAndLength() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedPreparedStatement.setNClob(0, reader, 0L);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testPreparedStatementOnAfterPreparedStatementSetSQLXML() throws SQLException {
    final SQLXML reader = mock(SQLXML.class);
    wrappedPreparedStatement.setSQLXML(0, reader);
    verify(mockedJdbcListener).onAfterPreparedStatementSet(eq(preparedStatementInformation), eq(0), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetNull() throws SQLException {
    wrappedCallableStatement.setNull(null, 0);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), any(),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetNullWithTypeName() throws SQLException {
    wrappedCallableStatement.setNull(null, 0, "foo");
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), any(),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testCallableStatementOnAfterCallableStatementSetBoolean() throws SQLException {
    wrappedCallableStatement.setBoolean(null, false);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(false),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetByte() throws SQLException {
    wrappedCallableStatement.setByte(null, (byte) 0);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq((byte) 0),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetShort() throws SQLException {
    wrappedCallableStatement.setShort(null, (short) 0);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq((short) 0),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetInt() throws SQLException {
    wrappedCallableStatement.setInt(null, 0);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(0),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetLong() throws SQLException {
    wrappedCallableStatement.setLong(null, 0L);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(0L),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetFloat() throws SQLException {
    wrappedCallableStatement.setFloat(null, 0.0f);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(0.0f),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetDouble() throws SQLException {
    wrappedCallableStatement.setDouble(null, 0.0);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(0.0),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetBigDecimal() throws SQLException {
    wrappedCallableStatement.setBigDecimal(null, new BigDecimal(0));
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(new BigDecimal(0)),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetString() throws SQLException {
    wrappedCallableStatement.setString(null, "foo");
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq("foo"),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetBytes() throws SQLException {
    wrappedCallableStatement.setBytes(null, new byte[0]);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(new byte[0]),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetDate() throws SQLException {
    final Date date = mock(Date.class);
    wrappedCallableStatement.setDate(null, date);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(date),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetDateWithCalendar() throws SQLException {
    final Date date = mock(Date.class);
    final Calendar calendar = mock(Calendar.class);
    wrappedCallableStatement.setDate(null, date, calendar);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(date),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetTime() throws SQLException {
    final Time time = mock(Time.class);
    wrappedCallableStatement.setTime(null, time);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(time),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetTimeWithCalendar() throws SQLException {
    final Time time = mock(Time.class);
    final Calendar calendar = mock(Calendar.class);
    wrappedCallableStatement.setTime(null, time, calendar);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(time),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetTimestamp() throws SQLException {
    final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    wrappedCallableStatement.setTimestamp(null, timestamp);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(timestamp),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetTimestampWithCalendar() throws SQLException {
    final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    final Calendar calendar = mock(Calendar.class);
    wrappedCallableStatement.setTimestamp(null, timestamp, calendar);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(timestamp),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetAsciiStream() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedCallableStatement.setAsciiStream(null, is);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetAsciiStreamWithIntLength() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedCallableStatement.setAsciiStream(null, is, 0);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetAsciiStreamWithLongLength() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedCallableStatement.setAsciiStream(null, is, 0L);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetBinaryStream() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedCallableStatement.setBinaryStream(null, is);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetBinaryStreamWithIntLength() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedCallableStatement.setBinaryStream(null, is, 0);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetBinaryStreamWithLongLength() throws SQLException {
    final InputStream is = mock(InputStream.class);
    wrappedCallableStatement.setBinaryStream(null, is, 0L);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(is),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetObject() throws SQLException {
    final Object object = new Object();
    wrappedCallableStatement.setObject(null, object);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(object),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetObjectWithTargetType() throws SQLException {
    final Object object = new Object();
    wrappedCallableStatement.setObject(null, object, 0);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(object),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetObjectWithTargetTypeAndScaleOrLenght() throws SQLException {
    final Object object = new Object();
    wrappedCallableStatement.setObject(null, object, 0, 0);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(object),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetCharacterStream() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedCallableStatement.setCharacterStream(null, reader);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetCharacterStreamWithIntLength() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedCallableStatement.setCharacterStream(null, reader, 0);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetCharacterStreamWithLongLength() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedCallableStatement.setCharacterStream(null, reader, 0L);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetBlob() throws SQLException {
    final Blob blob = mock(Blob.class);
    wrappedCallableStatement.setBlob(null, blob);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(blob),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetClob() throws SQLException {
    final Clob clob = mock(Clob.class);
    wrappedCallableStatement.setClob(null, clob);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(clob),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetClobWithReader() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedCallableStatement.setClob(null, reader);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetClobWithReaderAndLength() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedCallableStatement.setClob(null, reader, 0L);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetURL() throws SQLException, MalformedURLException {
    final URL url = new URL("http://google.com");
    wrappedCallableStatement.setURL(null, url);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(url),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetRowId() throws SQLException {
    final RowId rowId = mock(RowId.class);
    wrappedCallableStatement.setRowId(null, rowId);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(rowId),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetNString() throws SQLException {
    final RowId rowId = mock(RowId.class);
    wrappedCallableStatement.setRowId(null, rowId);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(rowId),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetNCharacterStream() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedCallableStatement.setNCharacterStream(null, reader);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetNCharacterStreamWithLength() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedCallableStatement.setNCharacterStream(null, reader, 0L);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetNClob() throws SQLException {
    final NClob nClob = mock(NClob.class);
    wrappedCallableStatement.setNClob(null, nClob);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(nClob),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetNClobWithReader() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedCallableStatement.setNClob(null, reader);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }
  
  @Test
  public void testCallableStatementOnAfterCallableStatementSetNClobWithReaderAndLength() throws SQLException {
    final Reader reader = mock(Reader.class);
    wrappedCallableStatement.setNClob(null, reader, 0L);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testCallableStatementOnAfterCallableStatementSetSQLXML() throws SQLException {
    final SQLXML reader = mock(SQLXML.class);
    wrappedCallableStatement.setSQLXML(null, reader);
    verify(mockedJdbcListener).onAfterCallableStatementSet(eq(callableStatementInformation), ArgumentMatchers.<String>isNull(), eq(reader),
        ArgumentMatchers.<SQLException>isNull());
  }

  
  @Test
  public void testConnectionOnAfterConnectionClose() throws SQLException {
    wrappedConnection.close();
    verify(mockedJdbcListener).onAfterConnectionClose(eq(connectionInformation),
        ArgumentMatchers.<SQLException>isNull());
  }

  @Test
  public void testStatementOnAfterStatementClose() throws SQLException {
    wrappedStatement.close();
    verify(mockedJdbcListener).onAfterStatementClose(eq(statementInformation), ArgumentMatchers.<SQLException>isNull());
  }

  private ConnectionInformation connectionInformationWithConnection() {
    return argThat(new ArgumentMatcher<ConnectionInformation>() {
      @Override
      public boolean matches(ConnectionInformation connectionInformation) {
        return connectionInformation.getConnection() != null;
      }
    });
  }

  private ConnectionInformation connectionInformationWithoutConnection() {
    return argThat(new ArgumentMatcher<ConnectionInformation>() {
      @Override
      public boolean matches(ConnectionInformation connectionInformation) {
        return connectionInformation.getConnection() == null;
      }
    });
  }

}
