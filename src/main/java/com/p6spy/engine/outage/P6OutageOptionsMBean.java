package com.p6spy.engine.outage;

public interface P6OutageOptionsMBean {

  public boolean getOutageDetection();

  void setOutageDetection(boolean outagedetection);

  public long getOutageDetectionInterval();

  void setOutageDetectionInterval(long outagedetectioninterval);

}
