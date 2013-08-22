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

public interface P6SpyManagerMBean extends org.jboss.util.ServiceMBean {
    //
    // Administrative methods
    //
    
    /**
     * Getter for name.
     *
     * @return name
     */
    public String getName();
    
    //
    // P6Spy-specific attributes
    //
    
    public void setExecutionThreshold(long param); 
    
    public long getExecutionThreshold();
        
    public void setUsePrefix(boolean param);
    
    public boolean getUsePrefix();
    
    public void setAutoflush(boolean param);
    
    public boolean getAutoflush();
    
    public void setExclude(String param);
    
    public String getExclude();
    
    public void setExcludecategories(String param);
    
    public String getExcludecategories();
    
    public void setFilter(boolean param);
    
    public boolean getFilter();
    
    public void setInclude(String param);
    
    public String getInclude();
    
    public void setIncludecategories(String param);
    
    public String getIncludecategories();
    
    public void setLogfile(String param);
    
    public String getLogfile();
    
    public void setRealdriver(String param);
    
    public String getRealdriver();
    
    public void setRealdriver2(String param);
    
    public String getRealdriver2();
    
    public void setRealdriver3(String param);
    
    public String getRealdriver3();
    
    public void setAppend(boolean param);
    
    public boolean getAppend();
    
    public void setSpydriver(String param);
    
    public String getSpydriver();
        
    public void setDateformat(String param);
    
    public String getDateformat();
    
    public void setStringmatcher(String param);
    
    public String getStringmatcher();
    
    public boolean getStackTrace();
    
    public void setStackTrace(boolean param);
    
    public String getStackTraceClass();
    
    public void setStackTraceClass(String param);
    
    public String getSQLExpression();
    
    public void setSQLExpression(String param);
    
    public boolean getReloadProperties();
    
    public void setReloadProperties(boolean param);
    
    public long getReloadPropertiesInterval();
    
    public void setReloadPropertiesInterval(long param);
    
    public boolean getOutageDetection();
    
    public void setOutageDetection(boolean param);
    
    public long getOutageDetectionInterval();
    
    public void setOutageDetectionInterval(long param);
    
    //
    //
    // P6Spy-specific methods
    //
    
    public void reloadProperties();
    
}

