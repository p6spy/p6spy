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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.logging.Category;

/**
 * This class is a singleton. Since P6Spy is normally loaded by the system
 * classpath, it is a normally a singleton across the JVM.
 * The instance will determine if a statement has been long running and log
 * the statement when such a statment is found. It accomplishes this by
 * spawning a daemon thread which will wake up at configurable intervals of
 * time (defined in seconds) and check if a statement is still executing since
 * the last time the thread was awake. This is accomplished by this instance
 * maintaining a list of active statements. The P6Statement and P6PreparedStatement
 * objects will reqister their call with the instance just before the SQL call
 * is delegated to the real driver. Once that statement finishes executing, the
 * statement object will unregister that call from list. If during that time
 * the thread in this instance sees the execute time exceed the threshold, it is
 * flagged as a long-running statement and logged. The statement will continue
 * to be logged if it appears in future iterations of the sleep/wake cycle.
 * This class is implemented with lazy thread sychronization. The list of
 * pending statements is a synchronized container, the rest is left open to
 * thread hazards to reduce the performance impact. So the logging results might be
 * slightly unreliable, but we aren't dealing with bank accounts here so thats
 * okay.
 */
public enum P6OutageDetector implements Runnable {
	
	INSTANCE;
	
    private ConcurrentMap<Object, InvocationInfo> pendingMessages;

    // flag that indicates that the thread should stop running
    private boolean haltThread;

    /** Creates new P6OutageDetector */
    private P6OutageDetector() {
    	pendingMessages = new ConcurrentHashMap<Object, InvocationInfo>();
    	
    	// create and run the auxilliary thread
        // make it a deamon thread so it won't prevent the server from
        // shutting down when it wants to.
    	ThreadGroup group = new ThreadGroup("P6SpyThreadGroup");
        group.setDaemon(true);
        Thread outageThread = new Thread(group, this, "P6SpyOutageThread");
        outageThread.start();
    	
        P6LogQuery.debug("P6Spy - P6OutageDetector has been invoked.");
        P6LogQuery.debug("P6Spy - P6OutageOptions.getOutageDetectionIntervalMS() = " + P6OutageOptions.getActiveInstance().getOutageDetectionIntervalMS());
    }

    /**
     * Method for running the auxillary thread.
     */
    public void run() {
        while (!haltThread) {
            detectOutage();

            try {
                // sleep for the configured interval
                // don't cache this value since the props file may be reloaded
                // and this value might change
                Thread.sleep(P6OutageOptions.getActiveInstance().getOutageDetectionIntervalMS());
            } catch (Exception e) {
            }
        }
    }

    /**
     * Tells the auxillary thread to stop executing. Thread will exit upon waking next.
     */
    public void shutdown() {
        haltThread = true;
    }

    /**
     * Registers the execution of a statement. This should be called just before the statement is
     * passed to the real driver.
     */
    public void registerInvocation(Object jdbcObject, long startTime, String category, String ps, String sql) {
        pendingMessages.put(jdbcObject, new InvocationInfo(startTime, category, ps, sql));
    }

    /**
     * Unregisters the execution of a statement. This should be called just after the statement is
     * passed to the real driver.
     */
    public void unregisterInvocation(Object jdbcObject) {
        pendingMessages.remove(jdbcObject);
    }

    private void detectOutage() {
        int listSize = pendingMessages.size();
        if (listSize == 0) {
            return;
        }

        P6LogQuery.debug("P6Spy - detectOutage.pendingMessage.size = " + listSize);

        long currentTime = System.nanoTime();
        long threshold = TimeUnit.MILLISECONDS.toNanos(P6OutageOptions.getActiveInstance().getOutageDetectionIntervalMS());

        for (Object jdbcObject : pendingMessages.keySet()) {
            // here is a thread hazard that we'll be lazy about. Another thread
            // might have already removed the entry from the messages map, so we
            // need to check if the result is null
            InvocationInfo ii = pendingMessages.get(jdbcObject);
            if (ii == null) {
                continue;
            }

            // has this statement exceeded the threshold?
            if ((currentTime - ii.startTime) > threshold) {
                P6LogQuery.debug("P6Spy - statement exceeded threshold - check log.");
                logOutage(ii);
            }
        }
    }

    private void logOutage(InvocationInfo ii) {
        P6LogQuery.logElapsed(-1, System.nanoTime() - ii.startTime, Category.OUTAGE, ii.preparedStmt, ii.sql);
    }

}

// inner class to hold the info about a specific statement invocation
class InvocationInfo {
    public long startTime;

    public String category;

    public String preparedStmt;

    public String sql;

    public InvocationInfo(long startTime, String category, String ps, String sql) {
        this.startTime = startTime;
        this.category = category;
        this.preparedStmt = ps;
        this.sql = sql;
    }
}
