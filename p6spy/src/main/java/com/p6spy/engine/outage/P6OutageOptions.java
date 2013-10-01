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

import com.p6spy.engine.common.*;

public class P6OutageOptions extends P6Options {
    
    public P6OutageOptions() { }
    
    protected static boolean outageDetection;
    protected static long outageDetectionInterval;
    protected static long outageMs;
            
    public static boolean getOutageDetection() {
        return outageDetection;
    }
    
    public static void setOutageDetection(String _outagedetection) {
        outageDetection = P6Util.isTrue(_outagedetection, false);
    }
    
    public static long getOutageDetectionInterval() {
        return outageDetectionInterval;
    }
    
    public static long getOutageDetectionIntervalMS() {
        return outageMs;
    }
    
    public static void setOutageDetectionInterval(String _outagedetectioninterval) {
        outageDetectionInterval = P6Util.parseLong(_outagedetectioninterval,-1l);
        outageMs = outageDetectionInterval * 1000l;
    }
    
}