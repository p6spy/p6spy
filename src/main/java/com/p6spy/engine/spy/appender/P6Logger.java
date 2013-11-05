package com.p6spy.engine.spy.appender;

public interface P6Logger {
    
    public void logSQL(int connectionId, String now, long elapsed, String category, String prepared, String sql);
    public void logException(Exception e);
    public void logText(String text);
    
}
