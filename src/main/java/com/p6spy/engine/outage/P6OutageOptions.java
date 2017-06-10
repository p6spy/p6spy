/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
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

package com.p6spy.engine.outage;

import java.util.HashMap;
import java.util.Map;

import javax.management.StandardMBean;

import com.p6spy.engine.spy.P6ModuleManager;
import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6OutageOptions extends StandardMBean implements P6OutageLoadableOptions {

  public static final String OUTAGEDETECTIONINTERVAL = "outagedetectioninterval";
  public static final String OUTAGEDETECTION = "outagedetection";

  public static final Map<String, String> defaults;

  static {
    defaults = new HashMap<String, String>();
    defaults.put(OUTAGEDETECTION, Boolean.toString(false));
    defaults.put(OUTAGEDETECTIONINTERVAL, Long.toString(30));
  }

  private final P6OptionsRepository optionsRepository;

  public P6OutageOptions(final P6OptionsRepository optionsRepository) {
    super(P6OutageOptionsMBean.class, false);
    this.optionsRepository = optionsRepository;
  }

  @Override
  public Map<String, String> getDefaults() {
    return defaults;
  }

  @Override
  public void load(Map<String, String> options) {
    setOutageDetection(options.get(OUTAGEDETECTION));
    setOutageDetectionInterval(options.get(OUTAGEDETECTIONINTERVAL));
  }

  /**
   * Utility method, to make accessing options from app less verbose.
   * 
   * @return active instance of the {@link P6OutageLoadableOptions}
   */
  public static P6OutageLoadableOptions getActiveInstance() {
    return P6ModuleManager.getInstance().getOptions(P6OutageOptions.class);
  }

  // JMX exposed API

  @Override
  public boolean getOutageDetection() {
    return optionsRepository.get(Boolean.class, OUTAGEDETECTION);
  }

  @Override
  public void setOutageDetection(String outagedetection) {
    optionsRepository.set(Boolean.class, OUTAGEDETECTION, outagedetection);
  }

  @Override
  public void setOutageDetection(boolean outagedetection) {
    optionsRepository.set(Boolean.class, OUTAGEDETECTION, outagedetection);
  }

  @Override
  public long getOutageDetectionInterval() {
    return optionsRepository.get(Long.class, OUTAGEDETECTIONINTERVAL);
  }

  @Override
  public long getOutageDetectionIntervalMS() {
    // TODO should we move it to setter?
    // but we would then need to intorduce safety check there
    // not sure about that
    return getOutageDetectionInterval() * 1000L;
  }

  @Override
  public void setOutageDetectionInterval(String outagedetectioninterval) {
    optionsRepository.set(Long.class, OUTAGEDETECTIONINTERVAL, outagedetectioninterval);
  }

  @Override
  public void setOutageDetectionInterval(long outagedetectioninterval) {
    optionsRepository.set(Long.class, OUTAGEDETECTIONINTERVAL, outagedetectioninterval);
  }

}