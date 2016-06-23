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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import com.p6spy.engine.common.CallableStatementInformation;
import com.p6spy.engine.event.JdbcEventListener;

/**
 * Provides a convenient implementation of the CallableStatement interface
 * that can be subclassed by developers wishing to adapt implementation.
 * <p>
 * This class implements the Wrapper or Decorator pattern. Methods default
 * to calling through to the wrapped request object.
 *
 * @see CallableStatement
 */
public class CallableStatementWrapper extends PreparedStatementWrapper implements CallableStatement {

  private final CallableStatement delegate;
  private final CallableStatementInformation statementInformation;

  public static CallableStatement wrap(CallableStatement delegate, CallableStatementInformation callableStatementInformation, JdbcEventListener eventListener) {
    if (delegate == null) {
      return null;
    }
    return new CallableStatementWrapper(delegate, callableStatementInformation, eventListener);
  }

  protected CallableStatementWrapper(CallableStatement delegate, CallableStatementInformation callableStatementInformation, JdbcEventListener eventListener) {
    super(delegate, callableStatementInformation, eventListener);
    this.delegate = delegate;
    statementInformation = callableStatementInformation;
  }

  @Override
  public URL getURL(int parameterIndex) throws SQLException {
    return delegate.getURL(parameterIndex);
  }

  @Override
  public void setURL(String parameterName, URL val) throws SQLException {
    delegate.setURL(parameterName, val);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, val);
  }

  @Override
  public void setNull(String parameterName, int sqlType) throws SQLException {
    delegate.setNull(parameterName, sqlType);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, null);
  }

  @Override
  public void setBoolean(String parameterName, boolean x) throws SQLException {
    delegate.setBoolean(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setByte(String parameterName, byte x) throws SQLException {
    delegate.setByte(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setShort(String parameterName, short x) throws SQLException {
    delegate.setShort(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setInt(String parameterName, int x) throws SQLException {
    delegate.setInt(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setLong(String parameterName, long x) throws SQLException {
    delegate.setLong(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setFloat(String parameterName, float x) throws SQLException {
    delegate.setFloat(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setDouble(String parameterName, double x) throws SQLException {
    delegate.setDouble(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
    delegate.setBigDecimal(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setString(String parameterName, String x) throws SQLException {
    delegate.setString(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setBytes(String parameterName, byte[] x) throws SQLException {
    delegate.setBytes(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setDate(String parameterName, Date x) throws SQLException {
    delegate.setDate(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setTime(String parameterName, Time x) throws SQLException {
    delegate.setTime(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
    delegate.setTimestamp(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
    delegate.setAsciiStream(parameterName, x, length);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
    delegate.setBinaryStream(parameterName, x, length);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
    delegate.setObject(parameterName, x, targetSqlType, scale);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
    delegate.setObject(parameterName, x, targetSqlType);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setObject(String parameterName, Object x) throws SQLException {
    delegate.setObject(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
    delegate.setCharacterStream(parameterName, reader, length);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, reader);
  }

  @Override
  public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
    delegate.setDate(parameterName, x, cal);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
    delegate.setTime(parameterName, x, cal);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
    delegate.setTimestamp(parameterName, x, cal);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
    delegate.setNull(parameterName, sqlType, typeName);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, null);
  }

  @Override
  public void setRowId(String parameterName, RowId x) throws SQLException {
    delegate.setRowId(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setNString(String parameterName, String value) throws SQLException {
    delegate.setNString(parameterName, value);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, value);
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
    delegate.setNCharacterStream(parameterName, value, length);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, value);
  }

  @Override
  public void setNClob(String parameterName, NClob value) throws SQLException {
    delegate.setNClob(parameterName, value);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, value);
  }

  @Override
  public void setClob(String parameterName, Reader reader, long length) throws SQLException {
    delegate.setClob(parameterName, reader, length);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, reader);
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
    delegate.setBlob(parameterName, inputStream, length);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, inputStream);
  }

  @Override
  public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
    delegate.setNClob(parameterName, reader, length);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, reader);
  }

  @Override
  public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
    delegate.setSQLXML(parameterName, xmlObject);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, xmlObject);
  }

  @Override
  public void setBlob(String parameterName, Blob x) throws SQLException {
    delegate.setBlob(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setClob(String parameterName, Clob x) throws SQLException {
    delegate.setClob(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
    delegate.setAsciiStream(parameterName, x, length);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
    delegate.setBinaryStream(parameterName, x, length);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
    delegate.setCharacterStream(parameterName, reader, length);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, reader);
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
    delegate.setAsciiStream(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
    delegate.setBinaryStream(parameterName, x);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, x);
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
    delegate.setCharacterStream(parameterName, reader);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, reader);
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
    delegate.setNCharacterStream(parameterName, value);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, value);
  }

  @Override
  public void setClob(String parameterName, Reader reader) throws SQLException {
    delegate.setClob(parameterName, reader);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, reader);
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
    delegate.setBlob(parameterName, inputStream);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, inputStream);
  }

  @Override
  public void setNClob(String parameterName, Reader reader) throws SQLException {
    delegate.setNClob(parameterName, reader);
    eventListener.onAfterCallableStatementSet(statementInformation, parameterName, reader);
  }

  @Override
  public NClob getNClob(int parameterIndex) throws SQLException {
    return delegate.getNClob(parameterIndex);
  }

  @Override
  public NClob getNClob(String parameterName) throws SQLException {
    return delegate.getNClob(parameterName);
  }

  @Override
  public SQLXML getSQLXML(int parameterIndex) throws SQLException {
    return delegate.getSQLXML(parameterIndex);
  }

  @Override
  public SQLXML getSQLXML(String parameterName) throws SQLException {
    return delegate.getSQLXML(parameterName);
  }

  @Override
  public String getNString(int parameterIndex) throws SQLException {
    return delegate.getNString(parameterIndex);
  }

  @Override
  public String getNString(String parameterName) throws SQLException {
    return delegate.getNString(parameterName);
  }

  @Override
  public Reader getNCharacterStream(int parameterIndex) throws SQLException {
    return delegate.getNCharacterStream(parameterIndex);
  }

  @Override
  public Reader getNCharacterStream(String parameterName) throws SQLException {
    return delegate.getNCharacterStream(parameterName);
  }

  @Override
  public Reader getCharacterStream(int parameterIndex) throws SQLException {
    return delegate.getCharacterStream(parameterIndex);
  }

  @Override
  public Reader getCharacterStream(String parameterName) throws SQLException {
    return delegate.getCharacterStream(parameterName);
  }

  @Override
  public String getString(String parameterName) throws SQLException {
    return delegate.getString(parameterName);
  }

  @Override
  public boolean getBoolean(String parameterName) throws SQLException {
    return delegate.getBoolean(parameterName);
  }

  @Override
  public byte getByte(String parameterName) throws SQLException {
    return delegate.getByte(parameterName);
  }

  @Override
  public short getShort(String parameterName) throws SQLException {
    return delegate.getShort(parameterName);
  }

  @Override
  public int getInt(String parameterName) throws SQLException {
    return delegate.getInt(parameterName);
  }

  @Override
  public long getLong(String parameterName) throws SQLException {
    return delegate.getLong(parameterName);
  }

  @Override
  public float getFloat(String parameterName) throws SQLException {
    return delegate.getFloat(parameterName);
  }

  @Override
  public double getDouble(String parameterName) throws SQLException {
    return delegate.getDouble(parameterName);
  }

  @Override
  public byte[] getBytes(String parameterName) throws SQLException {
    return delegate.getBytes(parameterName);
  }

  @Override
  public Date getDate(String parameterName) throws SQLException {
    return delegate.getDate(parameterName);
  }

  @Override
  public Time getTime(String parameterName) throws SQLException {
    return delegate.getTime(parameterName);
  }

  @Override
  public Timestamp getTimestamp(String parameterName) throws SQLException {
    return delegate.getTimestamp(parameterName);
  }

  @Override
  public Object getObject(String parameterName) throws SQLException {
    return delegate.getObject(parameterName);
  }

  @Override
  public BigDecimal getBigDecimal(String parameterName) throws SQLException {
    return delegate.getBigDecimal(parameterName);
  }

  @Override
  public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
    return delegate.getObject(parameterName, map);
  }

  @Override
  public Ref getRef(String parameterName) throws SQLException {
    return delegate.getRef(parameterName);
  }

  @Override
  public Blob getBlob(String parameterName) throws SQLException {
    return delegate.getBlob(parameterName);
  }

  @Override
  public Clob getClob(String parameterName) throws SQLException {
    return delegate.getClob(parameterName);
  }

  @Override
  public Array getArray(String parameterName) throws SQLException {
    return delegate.getArray(parameterName);
  }

  @Override
  public Date getDate(String parameterName, Calendar cal) throws SQLException {
    return delegate.getDate(parameterName, cal);
  }

  @Override
  public Time getTime(String parameterName, Calendar cal) throws SQLException {
    return delegate.getTime(parameterName, cal);
  }

  @Override
  public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
    return delegate.getTimestamp(parameterName, cal);
  }

  @Override
  public URL getURL(String parameterName) throws SQLException {
    return delegate.getURL(parameterName);
  }

  @Override
  public RowId getRowId(int parameterIndex) throws SQLException {
    return delegate.getRowId(parameterIndex);
  }

  @Override
  public RowId getRowId(String parameterName) throws SQLException {
    return delegate.getRowId(parameterName);
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
    delegate.registerOutParameter(parameterIndex, sqlType);
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
    delegate.registerOutParameter(parameterIndex, sqlType, scale);
  }

  @Override
  public boolean wasNull() throws SQLException {
    return delegate.wasNull();
  }

  @Override
  public String getString(int parameterIndex) throws SQLException {
    return delegate.getString(parameterIndex);
  }

  @Override
  public boolean getBoolean(int parameterIndex) throws SQLException {
    return delegate.getBoolean(parameterIndex);
  }

  @Override
  public byte getByte(int parameterIndex) throws SQLException {
    return delegate.getByte(parameterIndex);
  }

  @Override
  public short getShort(int parameterIndex) throws SQLException {
    return delegate.getShort(parameterIndex);
  }

  @Override
  public int getInt(int parameterIndex) throws SQLException {
    return delegate.getInt(parameterIndex);
  }

  @Override
  public long getLong(int parameterIndex) throws SQLException {
    return delegate.getLong(parameterIndex);
  }

  @Override
  public float getFloat(int parameterIndex) throws SQLException {
    return delegate.getFloat(parameterIndex);
  }

  @Override
  public double getDouble(int parameterIndex) throws SQLException {
    return delegate.getDouble(parameterIndex);
  }

  @Override
  public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
    return delegate.getBigDecimal(parameterIndex, scale);
  }

  @Override
  public byte[] getBytes(int parameterIndex) throws SQLException {
    return delegate.getBytes(parameterIndex);
  }

  @Override
  public Date getDate(int parameterIndex) throws SQLException {
    return delegate.getDate(parameterIndex);
  }

  @Override
  public Time getTime(int parameterIndex) throws SQLException {
    return delegate.getTime(parameterIndex);
  }

  @Override
  public Timestamp getTimestamp(int parameterIndex) throws SQLException {
    return delegate.getTimestamp(parameterIndex);
  }

  @Override
  public Object getObject(int parameterIndex) throws SQLException {
    return delegate.getObject(parameterIndex);
  }

  @Override
  public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
    return delegate.getBigDecimal(parameterIndex);
  }

  @Override
  public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
    return delegate.getObject(parameterIndex, map);
  }

  @Override
  public Ref getRef(int parameterIndex) throws SQLException {
    return delegate.getRef(parameterIndex);
  }

  @Override
  public Blob getBlob(int parameterIndex) throws SQLException {
    return delegate.getBlob(parameterIndex);
  }

  @Override
  public Clob getClob(int parameterIndex) throws SQLException {
    return delegate.getClob(parameterIndex);
  }

  @Override
  public Array getArray(int parameterIndex) throws SQLException {
    return delegate.getArray(parameterIndex);
  }

  @Override
  public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
    return delegate.getDate(parameterIndex, cal);
  }

  @Override
  public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
    return delegate.getTime(parameterIndex, cal);
  }

  @Override
  public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
    return delegate.getTimestamp(parameterIndex, cal);
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
    delegate.registerOutParameter(parameterIndex, sqlType, typeName);
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
    delegate.registerOutParameter(parameterName, sqlType);
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
    delegate.registerOutParameter(parameterName, sqlType, scale);
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
    delegate.registerOutParameter(parameterName, sqlType, typeName);
  }

  @Override
  public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
    return delegate.getObject(parameterIndex, type);
  }

  @Override
  public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
    return delegate.getObject(parameterName, type);
  }

}
