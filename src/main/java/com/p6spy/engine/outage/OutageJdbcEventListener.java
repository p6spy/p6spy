package com.p6spy.engine.outage;

import com.p6spy.engine.event.JdbcEventListener;

public class OutageJdbcEventListener extends JdbcEventListener {

  public static final OutageJdbcEventListener INSTANCE = new OutageJdbcEventListener();

  private OutageJdbcEventListener() {
  }
}
