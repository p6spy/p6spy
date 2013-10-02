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
package com.p6spy.engine.common;

import java.util.*;

public class OptionReloader implements Runnable {

    /*
     * this is our list of option classes we need to call, we use a set because we only want to call
     * each class once
     */
    protected static Set<P6Options> options = new HashSet<P6Options>();

    protected long sleepTime;

    protected boolean running;

    public OptionReloader(long sleep) {
        setSleep(sleep);
        setRunning(true);
    }

    public void setSleep(long sleep) {
        sleepTime = sleep;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    public boolean getRunning() {
        return running;
    }

    public void run() {
        while (running) {
            // this will always run its own thread,
            // so it should be all right to call sleep
            // on the current thread
            try {
                Thread.currentThread().sleep(sleepTime);
            } catch (InterruptedException e) {
                // nothing.
            }
            reload();
        }
    }

    public static void add(P6Options p6options, P6SpyProperties properties) {
        options.add(p6options);
        // when added make sure to deal with this
        if (!properties.isNewProperties()) {
            properties.forceReadProperties();
        }
        p6options.reload(properties);
    }

    public static void reload() {
        P6SpyProperties properties = new P6SpyProperties();
        // if nothing to reload, don't call the reload function
        if (!properties.isNewProperties()) {
            return;
        }
        for(P6Options option: options) {
            option.reload(properties);
        }
    }

}
