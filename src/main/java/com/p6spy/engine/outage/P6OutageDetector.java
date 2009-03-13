/*
 *
 * ====================================================================
 *
 * The P6Spy Software License, Version 1.1
 *
 * This license is derived and fully compatible with the Apache Software
 * license, see http://www.apache.org/LICENSE.txt
 *
 * Copyright (c) 2001-2002 Andy Martin, Ph.D. and Jeff Goke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement:
 * "The original concept and code base for P6Spy was conceived
 * and developed by Andy Martin, Ph.D. who generously contribued
 * the first complete release to the public under this license.
 * This product was due to the pioneering work of Andy
 * that began in December of 1995 developing applications that could
 * seamlessly be deployed with minimal effort but with dramatic results.
 * This code is maintained and extended by Jeff Goke and with the ideas
 * and contributions of other P6Spy contributors.
 * (http://www.p6spy.com)"
 * Alternately, this acknowlegement may appear in the software itself,
 * if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "P6Spy", "Jeff Goke", and "Andy Martin" must not be used
 * to endorse or promote products derived from this software without
 * prior written permission. For written permission, please contact
 * license@p6spy.com.
 *
 * 5. Products derived from this software may not be called "P6Spy"
 * nor may "P6Spy" appear in their names without prior written
 * permission of Jeff Goke and Andy Martin.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */


// Description: Class file for detecting long-running statements that may indicate
//              a database outage problem.

package com.p6spy.engine.outage;

import com.p6spy.engine.common.*;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;

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
public class P6OutageDetector implements Runnable {
    // sychronized container
    private Hashtable pendingMessages = null;
    
    // flag that indicates that the thread should stop running
    private boolean haltThread = false;
    
    // singleton contruct
    private static P6OutageDetector instance = null;
    
    // flag to turn on debug output
    private static final boolean debug = true;
    
    /** Creates new P6OutageDetector */
    protected P6OutageDetector() {
        pendingMessages = new Hashtable();
        
        P6LogQuery.logDebug("P6Spy - P6OutageDetector has been invoked.");
        P6LogQuery.logDebug("P6Spy - P6OutageOptions.getOutageDetectionIntervalMS() = "+
        P6OutageOptions.getOutageDetectionIntervalMS());
    }
    
    /**
     * Gets the instance of the detector. A side effect of the first call to
     * this method is that the auxillary thread will be kicked off here.
     *
     * @return  the P6OutageDetector instance
     */
    static public synchronized P6OutageDetector getInstance() {
        if (instance == null) {
            instance = new P6OutageDetector();
            
            // create and run the auxilliary thread
            // make it a deamon thread so it won't prevent the server from
            // shutting down when it wants to.
            ThreadGroup group = new ThreadGroup("P6SpyThreadGroup");
            group.setDaemon(true);
            Thread outageThread = new Thread(group,instance,"P6SpyOutageThread");
            outageThread.start();
        }
        return instance;
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
                Thread.sleep(P6OutageOptions.getOutageDetectionIntervalMS());
            }
            catch (Exception e) {}
        }
    }
    
    /**
     * Tells the auxillary thread to stop executing. Thread will exit upon waking
     * next.
     */
    public void shutdown() {
        haltThread = true;
    }
    
    /**
     * Registers the execution of a statement. This should be called just before
     * the statement is passed to the real driver.
     */
    public void registerInvocation(Object jdbcObject, long startTime,
    String category, String ps, String sql) {
        pendingMessages.put(jdbcObject, new InvocationInfo(startTime, category,
        ps, sql));
    }
    
    /**
     * Unregisters the execution of a statement. This should be called just after
     * the statement is passed to the real driver.
     */
    public void unregisterInvocation(Object jdbcObject) {
        pendingMessages.remove(jdbcObject);
    }
    
    private void detectOutage() {
        int listSize = pendingMessages.size();
        if (listSize == 0) return;
        
        P6LogQuery.logDebug("P6Spy - detectOutage.pendingMessage.size = "+listSize);
        
        long currentTime = System.currentTimeMillis();
        long threshold = P6OutageOptions.getOutageDetectionIntervalMS();
        
        Set keys = pendingMessages.keySet();
        Iterator keyIter = keys.iterator();
        
        while (keyIter.hasNext()) {
            // here is a thread hazard that we'll be lazy about. Another thread
            // might have already removed the entry from the messages map, so we
            // need to check if the result is null
            InvocationInfo ii = (InvocationInfo)pendingMessages.get(keyIter.next());
            if (ii == null) continue;
            
            // has this statement exceeded the threshold?
            if ((currentTime-ii.startTime) > threshold) {
                P6LogQuery.logDebug("P6Spy - statement exceeded threshold - check log.");
                logOutage(ii);
            }
        }
    }
    
    private void logOutage(InvocationInfo ii) {
        P6LogQuery.logElapsed(-1, ii.startTime, "OUTAGE", ii.preparedStmt, ii.sql);
    }
    
}

// inner class to hold the info about a specifc statement invocation
class InvocationInfo {
    public long startTime = 0;
    public String category = null;
    public String preparedStmt = null;
    public String sql = null;
    
    public InvocationInfo(long startTime, String category, String ps, String sql) {
        this.startTime = startTime;
        this.category = category;
        this.preparedStmt = ps;
        this.sql = sql;
    }
}