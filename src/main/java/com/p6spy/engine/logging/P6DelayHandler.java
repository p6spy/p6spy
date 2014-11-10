/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2014 P6Spy
 * %%
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
 * #L%
 */
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
        if (delay > 0) {
            P6LogQuery.logElapsed(statementInformation.getConnectionId(), startTime, Category.STATEMENT, statementInformation);
            try {
                Thread.sleep(delay);
            } catch(InterruptedException e){
                P6LogQuery.error("ERROR");
            }

        }
    }

}
