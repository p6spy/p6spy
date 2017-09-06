package com.p6spy.engine.spy;

import com.p6spy.engine.event.JdbcEventListener;

/**
 * Factory for creating the {@link JdbcEventListener}.
 * 
 * @author Peter Butkovic
 * @since 3.3.0
 */
public interface JdbcEventListenerFactory {

  JdbcEventListener createJdbcEventListener();
  
}
