package com.p6spy.engine.spy.appender;

import com.google.gson.Gson;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.json.LogEvent;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * JSON logger that provides P6 logs in a format that can be easily parsed by log aggregation tools.
 * batch file that can be later "replayed" using a generic SQL client.
 * <p>
 * This tool uses the {@link CustomLineFormat} class arguments to determine what to log in a message
 * </p>
 */
public class JSONFormat implements MessageFormattingStrategy {

  private static final MessageFormattingStrategy FALLBACK_FORMATTING_STRATEGY = new SingleLineFormat();

  private Gson gson;
  private boolean includeStackTrace;

  public JSONFormat() {
    gson = new Gson();
    includeStackTrace = P6SpyOptions.getActiveInstance().getJSONStackTrace();
  }

  @Override
  public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {

    LogEvent.LogEventBuilder builder = LogEvent.builder();

    builder
      .withConnectionId(connectionId)
      .withTimestamp(Long.parseLong(now))
      .withExecutionTime(elapsed)
      .withCategory(category)
      .withSql(sql)
      .withConnectionUrl(url);

    if (includeStackTrace) {
      Exception e = new Exception();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      builder.withStackTrace(sw.toString());
    }

    return gson.toJson(builder.build());
  }
}
