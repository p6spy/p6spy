package com.p6spy.engine.event;

import java.util.ArrayList;
import java.util.List;

import com.p6spy.engine.common.CallableStatementInformation;
import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;

public class CompoundJdbcEventListener extends JdbcEventListener {
  private final List<JdbcEventListener> eventListeners;

  public CompoundJdbcEventListener() {
    eventListeners = new ArrayList<JdbcEventListener>();
  }

  public CompoundJdbcEventListener(List<JdbcEventListener> eventListeners) {
    this.eventListeners = eventListeners;
  }

  public void addListender(JdbcEventListener listener) {
    eventListeners.add(listener);
  }

  @Override
  public void onAddBatch(StatementInformation statementInformation, String sql) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onAddBatch(statementInformation, sql);
    }
  }

  @Override
  public void onExecute(StatementInformation statementInformation, long timeElapsedNanos) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onExecute(statementInformation, timeElapsedNanos);
    }
  }

  @Override
  public void onExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onExecute(statementInformation, timeElapsedNanos, sql);
    }
  }

  @Override
  public void onExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onExecuteBatch(statementInformation, timeElapsedNanos);
    }
  }

  @Override
  public void onExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onExecuteUpdate(statementInformation, timeElapsedNanos);
    }
  }

  @Override
  public void onExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onExecuteUpdate(statementInformation, timeElapsedNanos, sql);
    }
  }

  @Override
  public void onExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onExecuteQuery(statementInformation, timeElapsedNanos);
    }
  }

  @Override
  public void onExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onExecuteQuery(statementInformation, timeElapsedNanos, sql);
    }
  }

  @Override
  public void onPreparedStatementSet(PreparedStatementInformation statementInformation, int parameterIndex, Object value) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onPreparedStatementSet(statementInformation, parameterIndex, value);
    }
  }

  @Override
  public void onCallableStatementSet(CallableStatementInformation statementInformation, String parameterName, Object value) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onCallableStatementSet(statementInformation, parameterName, value);
    }
  }

  @Override
  public void onGetResultSet(StatementInformation statementInformation, long timeElapsedNanos) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onGetResultSet(statementInformation, timeElapsedNanos);
    }
  }

  @Override
  public void onResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onResultSetNext(resultSetInformation, timeElapsedNanos, hasNext);
    }
  }

  @Override
  public void onResultSetClose(ResultSetInformation resultSetInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onResultSetClose(resultSetInformation);
    }
  }

  @Override
  public void onResultSetGet(ResultSetInformation resultSetInformation, String columnLabel, Object value) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onResultSetGet(resultSetInformation, columnLabel, value);
    }
  }

  @Override
  public void onResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onResultSetGet(resultSetInformation, columnIndex, value);
    }
  }

  @Override
  public void onCommit(ConnectionInformation connectionInformation, long timeElapsedNanos) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onCommit(connectionInformation, timeElapsedNanos);
    }
  }

  @Override
  public void onConnectionClose(ConnectionInformation connectionInformation) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onConnectionClose(connectionInformation);
    }
  }

  @Override
  public void onRollback(ConnectionInformation connectionInformation, long timeElapsedNanos) {
    for (JdbcEventListener eventListener : eventListeners) {
      eventListener.onRollback(connectionInformation, timeElapsedNanos);
    }
  }
}
