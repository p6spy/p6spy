package com.p6spy.engine.spy.appender.json;

import com.google.gson.annotations.SerializedName;

/**
 * POJO for serialization of Log events into JSON
 *
 * This class uses GSON annotations to translate some property names into friendlier names for
 * aggregation into logging systems.
 * <p>
 * Use the {@link LogEventBuilder} class to instantiate the LogEvent as necessary.
 * </p>
 * GSON ignores null fields by default when serializing the POJO.
 */
public class LogEvent {

  private int connectionId;
  private long timestamp;
  private long executionTime;
  private String category;
  private String sql;
  private String preparedSql;
  private String connectionUrl;
  @SerializedName(value = "class")
  private String className;
  @SerializedName(value = "method")
  private String methodName;
  private String stackTrace;

  public int getConnectionId() {
    return connectionId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public long getExecutionTime() {
    return executionTime;
  }

  public String getCategory() {
    return category;
  }

  public String getSql() {
    return sql;
  }

  public String getPreparedSql() {
    return preparedSql;
  }

  public String getConnectionUrl() {
    return connectionUrl;
  }

  public String getClassName() {
    return className;
  }

  public String getMethodName() {
    return methodName;
  }

  public static LogEventBuilder builder() {
    return new LogEventBuilder();
  }

  public String getStackTrace() {
    return stackTrace;
  }

  public static final class LogEventBuilder {
    private int connectionId;
    private long timestamp;
    private long executionTime;
    private String category;
    private String sql;
    private String preparedSql;
    private String connectionUrl;
    private String className;
    private String methodName;
    private String stackTrace;

    private LogEventBuilder() {
    }

    public LogEventBuilder withConnectionId(int connectionId) {
      this.connectionId = connectionId;
      return this;
    }

    public LogEventBuilder withTimestamp(long timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public LogEventBuilder withExecutionTime(long executionTime) {
      this.executionTime = executionTime;
      return this;
    }

    public LogEventBuilder withCategory(String category) {
      this.category = category;
      return this;
    }

    public LogEventBuilder withSql(String sql) {
      this.sql = sql;
      return this;
    }

    public LogEventBuilder withPreparedSql(String preparedSql) {
      this.preparedSql = preparedSql;
      return this;
    }

    public LogEventBuilder withConnectionUrl(String url) {
      this.connectionUrl = url;
      return this;
    }

    public LogEventBuilder withClassName(String className) {
      this.className = className;
      return this;
    }

    public LogEventBuilder withMethodName(String methodName) {
      this.methodName = methodName;
      return this;
    }

    public LogEventBuilder withStackTrace(String stackTrace) {
      this.stackTrace = stackTrace;
      return this;
    }

    public LogEvent build() {
      LogEvent logEvent = new LogEvent();
      logEvent.timestamp = this.timestamp;
      logEvent.connectionId = this.connectionId;
      logEvent.sql = this.sql;
      logEvent.preparedSql = this.preparedSql;
      logEvent.className = this.className;
      logEvent.category = this.category;
      logEvent.connectionUrl = this.connectionUrl;
      logEvent.methodName = this.methodName;
      logEvent.executionTime = this.executionTime;
      logEvent.stackTrace = this.stackTrace;
      return logEvent;
    }
  }
}
