/**
 * P6Spy
 *
 * Copyright (C) 2002 P6Spy
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

import com.p6spy.engine.common.CallableStatementInformation;
import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompoundJdbcEventListener extends JdbcEventListener {
  private final List<JdbcEventListener> eventListeners;

  public CompoundJdbcEventListener() {
    eventListeners = new ArrayList<JdbcEventListener>();
  }

  public CompoundJdbcEventListener(List<JdbcEventListener> eventListeners) {
    this.eventListeners = eventListeners;
  }


  /**
   * Adds a listener to this {@link CompoundJdbcEventListener}
   *
   * @param listener The listener to add
   * @deprecated     This method name has a typo, please use {@link CompoundJdbcEventListener#addListener}
   */
  public void addListender(JdbcEventListener listener) {
    addListener(listener);
  }

  /**
   * Adds a listener to this {@link CompoundJdbcEventListener}
   *
   * @param listener The listener to add
   */
  public void addListener(JdbcEventListener listener) {
    eventListeners.add(listener);
  }

  /**
   * Returns a read only view of the registered {@link JdbcEventListener}s
   *
   * @return a read only view of the registered {@link JdbcEventListener}s
   */
  public List<JdbcEventListener> getEventListeners() {
    return Collections.unmodifiableList(eventListeners);
  }

  @Override
  public void onBeforeGetConnection(ConnectionInformation connectionInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeGetConnection(connectionInformation);
    }
  }

  @Override
  public void onAfterGetConnection(ConnectionInformation connectionInformation, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterGetConnection(connectionInformation, e);
    }
  }

  @Override
  @Deprecated
  public void onConnectionWrapped(ConnectionInformation connectionInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onConnectionWrapped(connectionInformation);
    }
  }

  @Override
  public void onBeforeAddBatch(PreparedStatementInformation statementInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeAddBatch(statementInformation);
    }
  }

  @Override
  public void onAfterAddBatch(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterAddBatch(statementInformation, timeElapsedNanos, e);
    }
  }

  @Override
  public void onBeforeAddBatch(StatementInformation statementInformation, String sql) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeAddBatch(statementInformation, sql);
    }
  }

  @Override
  public void onAfterAddBatch(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterAddBatch(statementInformation, timeElapsedNanos, sql, e);
    }
  }

  @Override
  public void onBeforeExecute(PreparedStatementInformation statementInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeExecute(statementInformation);
    }
  }

  @Override
  public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterExecute(statementInformation, timeElapsedNanos, e);
    }
  }

  @Override
  public void onBeforeExecute(StatementInformation statementInformation, String sql) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeExecute(statementInformation, sql);
    }
  }

  @Override
  public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterExecute(statementInformation, timeElapsedNanos, sql, e);
    }
  }

  @Override
  public void onBeforeExecuteBatch(StatementInformation statementInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeExecuteBatch(statementInformation);
    }
  }

  @Override
  public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, int[] updateCounts, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterExecuteBatch(statementInformation, timeElapsedNanos, updateCounts, e);
    }
  }

  @Override
  public void onBeforeExecuteUpdate(PreparedStatementInformation statementInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeExecuteUpdate(statementInformation);
    }
  }

  @Override
  public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos, int rowCount, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterExecuteUpdate(statementInformation, timeElapsedNanos, rowCount, e);
    }
  }

  @Override
  public void onBeforeExecuteUpdate(StatementInformation statementInformation, String sql) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeExecuteUpdate(statementInformation, sql);
    }
  }

  @Override
  public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql, int rowCount, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterExecuteUpdate(statementInformation, timeElapsedNanos, sql, rowCount, e);
    }
  }

  @Override
  public void onBeforeExecuteQuery(PreparedStatementInformation statementInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeExecuteQuery(statementInformation);
    }
  }

  @Override
  public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterExecuteQuery(statementInformation, timeElapsedNanos, e);
    }
  }

  @Override
  public void onBeforeExecuteQuery(StatementInformation statementInformation, String sql) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeExecuteQuery(statementInformation, sql);
    }
  }

  @Override
  public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterExecuteQuery(statementInformation, timeElapsedNanos, sql, e);
    }
  }

  @Override
  public void onAfterPreparedStatementSet(PreparedStatementInformation statementInformation, int parameterIndex, Object value, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterPreparedStatementSet(statementInformation, parameterIndex, value, e);
    }
  }

  @Override
  public void onAfterCallableStatementSet(CallableStatementInformation statementInformation, String parameterName, Object value, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterCallableStatementSet(statementInformation, parameterName, value, e);
    }
  }

  @Override
  public void onAfterGetResultSet(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterGetResultSet(statementInformation, timeElapsedNanos, e);
    }
  }

  @Override
  public void onBeforeResultSetNext(ResultSetInformation resultSetInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeResultSetNext(resultSetInformation);
    }
  }

  @Override
  public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterResultSetNext(resultSetInformation, timeElapsedNanos, hasNext, e);
    }
  }

  @Override
  public void onAfterResultSetClose(ResultSetInformation resultSetInformation, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterResultSetClose(resultSetInformation, e);
    }
  }

  @Override
  public void onAfterResultSetGet(ResultSetInformation resultSetInformation, String columnLabel, Object value, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterResultSetGet(resultSetInformation, columnLabel, value, e);
    }
  }

  @Override
  public void onAfterResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterResultSetGet(resultSetInformation, columnIndex, value, e);
    }
  }

  @Override
  public void onBeforeCommit(ConnectionInformation connectionInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeCommit(connectionInformation);
    }
  }

  @Override
  public void onAfterCommit(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterCommit(connectionInformation, timeElapsedNanos, e);
    }
  }

  @Override
  public void onAfterConnectionClose(ConnectionInformation connectionInformation, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterConnectionClose(connectionInformation, e);
    }
  }

  @Override
  public void onBeforeRollback(ConnectionInformation connectionInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeRollback(connectionInformation);
    }
  }

  @Override
  public void onAfterRollback(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterRollback(connectionInformation, timeElapsedNanos, e);
    }
  }
  
  @Override
  public void onAfterStatementClose(StatementInformation statementInformation, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterStatementClose(statementInformation, e);
    }
  }

  @Override
  public void onBeforeSetAutoCommit(ConnectionInformation connectionInformation, boolean newAutoCommit, boolean currentAutoCommit) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onBeforeSetAutoCommit(connectionInformation, newAutoCommit,currentAutoCommit);
    }
  }

  @Override
  public void onAfterSetAutoCommit(ConnectionInformation connectionInformation, boolean newAutoCommit, boolean oldAutoCommit, SQLException e) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAfterSetAutoCommit(connectionInformation, newAutoCommit,oldAutoCommit,e);
    }
  }

}
