/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2016 P6Spy
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
package com.p6spy.engine.wrapper;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.event.JdbcEventListener;

/**
 * Provides a convenient implementation of the PreparedStatement interface
 * that can be subclassed by developers wishing to adapt implementation.
 * <p>
 * This class implements the Wrapper or Decorator pattern. Methods default
 * to calling through to the wrapped request object.
 *
 * @see PreparedStatement
 */
public class PreparedStatementWrapper extends StatementWrapper implements PreparedStatement {

  private final PreparedStatement delegate;
  private final PreparedStatementInformation statementInformation;

  public static PreparedStatement wrap(PreparedStatement delegate, PreparedStatementInformation preparedStatementInformation, JdbcEventListener eventListener) {
    if (delegate == null) {
      return null;
    }
    return new PreparedStatementWrapper(delegate, preparedStatementInformation, eventListener);
  }

  protected PreparedStatementWrapper(PreparedStatement delegate, PreparedStatementInformation preparedStatementInformation, JdbcEventListener eventListener) {
    super(delegate, preparedStatementInformation, eventListener);
    this.delegate = delegate;
    statementInformation = preparedStatementInformation;
  }

  @Override
  public ResultSet executeQuery() throws SQLException {
    long start = System.nanoTime();
    try {
      eventListener.onBeforeExecuteQuery(statementInformation);
      return ResultSetWrapper.wrap(delegate.executeQuery(), new ResultSetInformation(statementInformation), eventListener);
    } finally {
      eventListener.onAfterExecuteQuery(statementInformation, System.nanoTime() - start);
    }
  }

  @Override
  public int executeUpdate() throws SQLException {
    long start = System.nanoTime();
    try {
      eventListener.onBeforeExecuteUpdate(statementInformation);
      return delegate.executeUpdate();
    } finally {
      eventListener.onAfterExecuteUpdate(statementInformation, System.nanoTime() - start);
    }
  }

  @Override
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    delegate.setNull(parameterIndex, sqlType);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, null);
  }

  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    delegate.setBoolean(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    delegate.setByte(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    delegate.setShort(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    delegate.setInt(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    delegate.setLong(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    delegate.setFloat(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    delegate.setDouble(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    delegate.setBigDecimal(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    delegate.setString(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    delegate.setBytes(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    delegate.setDate(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    delegate.setTime(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    delegate.setTimestamp(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    delegate.setAsciiStream(parameterIndex, x, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    delegate.setUnicodeStream(parameterIndex, x, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    delegate.setBinaryStream(parameterIndex, x, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void clearParameters() throws SQLException {
    delegate.clearParameters();
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
    delegate.setObject(parameterIndex, x, targetSqlType);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setObject(int parameterIndex, Object x) throws SQLException {
    delegate.setObject(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public boolean execute() throws SQLException {
    long start = System.nanoTime();
    try {
      eventListener.onBeforeExecute(statementInformation);
      return delegate.execute();
    } finally {
      eventListener.onAfterExecute(statementInformation, System.nanoTime() - start);
    }
  }

  @Override
  public void addBatch() throws SQLException {
    long start = System.nanoTime();
    try {
      eventListener.onBeforeAddBatch(statementInformation);
      delegate.addBatch();
    } finally {
      eventListener.onAfterAddBatch(statementInformation, System.nanoTime() - start);
    }
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
    delegate.setCharacterStream(parameterIndex, reader, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, reader);
  }

  @Override
  public void setRef(int parameterIndex, Ref x) throws SQLException {
    delegate.setRef(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setBlob(int parameterIndex, Blob x) throws SQLException {
    delegate.setBlob(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setClob(int parameterIndex, Clob x) throws SQLException {
    delegate.setClob(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setArray(int parameterIndex, Array x) throws SQLException {
    delegate.setArray(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    delegate.setDate(parameterIndex, x, cal);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    delegate.setTime(parameterIndex, x, cal);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    delegate.setTimestamp(parameterIndex, x, cal);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    delegate.setNull(parameterIndex, sqlType, typeName);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, null);
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {
    delegate.setURL(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    delegate.setRowId(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setNString(int parameterIndex, String value) throws SQLException {
    delegate.setNString(parameterIndex, value);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, value);
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
    delegate.setNCharacterStream(parameterIndex, value, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, value);
  }

  @Override
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    delegate.setNClob(parameterIndex, value);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, value);
  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    delegate.setClob(parameterIndex, reader, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, reader);
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    delegate.setBlob(parameterIndex, inputStream, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, inputStream);
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    delegate.setNClob(parameterIndex, reader, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, reader);
  }

  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    delegate.setSQLXML(parameterIndex, xmlObject);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, xmlObject);
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
    delegate.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    delegate.setAsciiStream(parameterIndex, x, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    delegate.setBinaryStream(parameterIndex, x, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    delegate.setCharacterStream(parameterIndex, reader, length);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, reader);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    delegate.setAsciiStream(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    delegate.setBinaryStream(parameterIndex, x);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, x);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    delegate.setCharacterStream(parameterIndex, reader);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, reader);
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    delegate.setNCharacterStream(parameterIndex, value);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, value);
  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    delegate.setClob(parameterIndex, reader);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, reader);
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    delegate.setBlob(parameterIndex, inputStream);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, inputStream);
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    delegate.setNClob(parameterIndex, reader);
    eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, reader);
  }

  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return delegate.getParameterMetaData();
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return delegate.getMetaData();
  }

}
