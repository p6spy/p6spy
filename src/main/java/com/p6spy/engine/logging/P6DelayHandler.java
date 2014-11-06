package com.p6spy.engine.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.P6Logger;
import org.objectweb.util.monolog.wrapper.p6spy.P6SpyLogger;

/**
 * Created by michaelmainguy on 11/5/14.
 */
public class P6DelayHandler {
    public static void delay(long startTime, StatementInformation statementInformation) {
        long delay = P6SpyOptions.getActiveInstance().getFixedDelay();
        if (delay > 0 && ((System.currentTimeMillis() - startTime) < delay)) {
            P6LogQuery.logElapsed(statementInformation.getConnectionId(), startTime, Category.STATEMENT, statementInformation);

            long time = System.currentTimeMillis() - startTime;
            //Close enough, can't be completely exact
            try {
                if ((delay - time) > 0) {
                    Thread.sleep(delay - time);
                }
            } catch(InterruptedException e){
                    P6LogQuery.error("ERROR");
            }

        }
    }

}
