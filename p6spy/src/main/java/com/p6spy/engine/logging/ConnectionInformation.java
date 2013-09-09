package com.p6spy.engine.logging;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class ConnectionInformation {

  private static int counter = 0;
  private final int connectionId = counter++;

  public int getConnectionId() {
    return connectionId;
  }
}
