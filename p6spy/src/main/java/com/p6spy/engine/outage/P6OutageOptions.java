/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.p6spy.engine.outage;

import java.util.Properties;

import com.p6spy.engine.common.P6ModuleManager;
import com.p6spy.engine.common.P6Util;

public class P6OutageOptions implements P6OutageLoadableOptions {
    
    protected boolean outageDetection;
    protected long outageDetectionInterval;
    protected long outageMs;
    
    @Override
    public void load(Properties properties) {
      setOutageDetection(properties.getProperty("outagedetection"));
      setOutageDetectionInterval(properties.getProperty("outagedetectioninterval"));
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
        return outageDetection;
    }
    
    @Override
    public void setOutageDetection(String outagedetection) {
        outageDetection = P6Util.isTrue(outagedetection, false);
    }
    
    @Override
    public long getOutageDetectionInterval() {
        return outageDetectionInterval;
    }
    
    @Override
    public long getOutageDetectionIntervalMS() {
        return outageMs;
    }

    // not JMX exposed API
    
    @Override
    public void setOutageDetectionInterval(String _outagedetectioninterval) {
        outageDetectionInterval = P6Util.parseLong(_outagedetectioninterval,-1l);
        outageMs = outageDetectionInterval * 1000l;
    }

}