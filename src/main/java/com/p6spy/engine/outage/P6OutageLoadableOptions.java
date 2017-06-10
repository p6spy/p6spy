
package com.p6spy.engine.outage;

import com.p6spy.engine.spy.P6LoadableOptions;

public interface P6OutageLoadableOptions extends P6LoadableOptions, P6OutageOptionsMBean {

  public long getOutageDetectionIntervalMS();

  void setOutageDetection(String outagedetection);

  void setOutageDetectionInterval(String outagedetectioninterval);
}
