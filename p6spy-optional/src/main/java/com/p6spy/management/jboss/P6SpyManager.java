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

package com.p6spy.management.jboss;

import com.p6spy.engine.common.P6SpyOptions;
import com.p6spy.engine.common.OptionReloader;
import com.p6spy.engine.outage.P6OutageOptions;

public class P6SpyManager extends org.jboss.util.ServiceMBeanSupport implements P6SpyManagerMBean {
    
    //
    // Administrative methods
    //
    
    /**
     * Getter for name.
     *
     * @return name
     */
    public String getName() {
        return getClass().getName();
    }
    
    //
    // P6Spy-specific attributes
    //
    
    public void setExecutionThreshold(long param) {
        P6SpyOptions.setExecutionThreshold(String.valueOf(param));
    }
    
    public long getExecutionThreshold() {
        return P6SpyOptions.getExecutionThreshold();
    }
    
    public void setUsePrefix(boolean param) {
        P6SpyOptions.setUsePrefix(String.valueOf(param));
    }
    
    public boolean getUsePrefix() {
        return P6SpyOptions.getUsePrefix();
    }
    
    public void setAutoflush(boolean param) {
        P6SpyOptions.setAutoflush(String.valueOf(param));
    }
    
    public boolean getAutoflush() {
        return P6SpyOptions.getAutoflush();
    }
    
    public void setExclude(String param) {
        P6SpyOptions.setExclude(param);
    }
    
    public String getExclude() {
        return P6SpyOptions.getExclude();
    }
    
    public void setExcludecategories(String param) {
        P6SpyOptions.setExcludecategories(param);
    }
    
    public String getExcludecategories() {
        return P6SpyOptions.getExcludecategories();
    }
    
    public void setFilter(boolean param) {
        P6SpyOptions.setFilter(String.valueOf(param));
    }
    
    public boolean getFilter() {
        return P6SpyOptions.getFilter();
    }
    
    public void setInclude(String param) {
        P6SpyOptions.setInclude(param);
    }
    
    public String getInclude() {
        return P6SpyOptions.getInclude();
    }
    
    public void setIncludecategories(String param) {
        P6SpyOptions.setIncludecategories(param);
    }
    
    public String getIncludecategories() {
        return P6SpyOptions.getIncludecategories();
    }
    
    public void setLogfile(String param) {
        P6SpyOptions.setLogfile(param);
    }
    
    public String getLogfile() {
        return P6SpyOptions.getLogfile();
    }
    
    public void setRealdriver(String param) {
        P6SpyOptions.setRealdriver(param);
    }
    
    public String getRealdriver() {
        return P6SpyOptions.getRealdriver();
    }
    
    public void setRealdriver2(String param) {
        P6SpyOptions.setRealdriver2(param);
    }
    
    public String getRealdriver2() {
        return P6SpyOptions.getRealdriver2();
    }
    
    public void setRealdriver3(String param) {
        P6SpyOptions.setRealdriver3(param);
    }
    
    public String getRealdriver3() {
        return P6SpyOptions.getRealdriver3();
    }
    
    public void setAppend(boolean param) {
        P6SpyOptions.setAppend(String.valueOf(param));
    }
    
    public boolean getAppend() {
        return P6SpyOptions.getAppend();
    }
    
    public void setSpydriver(String param) {
        P6SpyOptions.setSpydriver(param);
    }
    
    public String getSpydriver() {
        return P6SpyOptions.getSpydriver();
    }
            
    public void setDateformat(String param) {
        P6SpyOptions.setDateformat(param);
    }
    
    public String getDateformat() {
        return P6SpyOptions.getDateformat();
    }
    
    public void setStringmatcher(String param) {
        P6SpyOptions.setStringmatcher(param);
    }
    
    public String getStringmatcher() {
        return P6SpyOptions.getStringmatcher();
    }
    
    public boolean getStackTrace() {
        return P6SpyOptions.getStackTrace();
    }
    
    public void setStackTrace(boolean param) {
        P6SpyOptions.setStackTrace(String.valueOf(param));
    }
    
    public String getStackTraceClass() {
        return P6SpyOptions.getStackTraceClass();
    }
    
    public void setStackTraceClass(String param) {
        P6SpyOptions.setStackTraceClass(param);
    }
    
    public String getSQLExpression() {
        return P6SpyOptions.getSQLExpression();
    }
    
    public void setSQLExpression(String param) {
        P6SpyOptions.setSQLExpression(param);
    }
    
    public boolean getReloadProperties() {
        return P6SpyOptions.getReloadProperties();
    }
    
    public void setReloadProperties(boolean param) {
        P6SpyOptions.setReloadProperties(String.valueOf(param));
    }
    
    public long getReloadPropertiesInterval() {
        return P6SpyOptions.getReloadPropertiesInterval();
    }
    
    public void setReloadPropertiesInterval(long param) {
        P6SpyOptions.setReloadPropertiesInterval(String.valueOf(param));
    }
    
    public boolean getOutageDetection() {
        return P6OutageOptions.getOutageDetection();
    }
    
    public void setOutageDetection(boolean param) {
        P6OutageOptions.setOutageDetection(String.valueOf(param));
    }
    
    public long getOutageDetectionInterval() {
        return P6OutageOptions.getOutageDetectionInterval();
    }
    
    public void setOutageDetectionInterval(long param) {
        P6OutageOptions.setOutageDetectionInterval(String.valueOf(param));
    }
    
    //
    //
    // P6Spy-specific methods
    //
    
    public void reloadProperties() {
        OptionReloader.reload();
    }
    
}

