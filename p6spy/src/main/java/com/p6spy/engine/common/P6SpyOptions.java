/*
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

// Description: Class file for options
package com.p6spy.engine.common;

import java.util.*;
import java.text.SimpleDateFormat;

public class P6SpyOptions extends P6Options {

    /*
     * how to add a new property: attempted to make this as easy as possible. just treat this like a
     * normal bean and create a get/set method. the primary difference is: (1) the set method should
     * always accept a string [since we are reading from a property file], parse this as necessary.
     * use p6util.isTrue for boolean values. (2) make sure to set a default value, if you need one,
     * within the set method. the rest is handled automatically. introspection is used, but since it
     * is only incurred when a property file is loaded, which is at start time and if you change it
     * and have reload properties = true, the expected overhead is minimal
     */

    protected static Thread reloadThread;

    protected static OptionReloader reloader;


    public static final String DRIVER_PREFIX = "realdriver";

    public static final String MODULE_PREFIX = "module.";

    public static final String DEFAULT_DB_DATEFORMAT = "dd-MMM-yy";

    private static List modules;

    private static List<String> driverNames;

    private static boolean usePrefix;

    private static boolean autoflush;

    private static String exclude;

    private static boolean filter;

    private static String include;

    private static String logfile;

    private static String appender;

    private static String realdriver;

    private static String realdriver2;

    private static String realdriver3;

    private static String spydriver;

    private static boolean append;

    private static boolean deregister;

    private static String dateformat;


    private static String includecategories;

    private static String excludecategories;

    private static String sqlExpression;

    private static boolean stackTrace;

    private static String stackTraceClass;

    private static boolean reloadProperties;

    private static long reloadPropertiesInterval;

    private static long reloadMs;

    private static String jndicontextfactory;

    private static String jndicontextproviderurl;

    private static String jndicontextcustom;

    private static String realdatasource;

    private static String realdatasourceclass;

    private static String realdatasourceproperties;

    private static long executionThreshold;

    private static String databaseDialectDateFormat;

    public P6SpyOptions() {
    }
    public static void setExecutionThreshold(String _executionThreshold) {
        executionThreshold = P6Util.parseLong(_executionThreshold, 0);
    }

    public static long getExecutionThreshold() {
        return executionThreshold;
    }

    public static void setUsePrefix(String _usePrefix) {
        usePrefix = P6Util.isTrue(_usePrefix, false);
    }

    public static boolean getUsePrefix() {
        return usePrefix;
    }

    public static void setAutoflush(String _autoflush) {
        autoflush = P6Util.isTrue(_autoflush, false);
    }

    public static boolean getAutoflush() {
        return autoflush;
    }

    public static void setExclude(String _exclude) {
        exclude = _exclude;
    }

    public static String getExclude() {
        return exclude;
    }

    public static void setExcludecategories(String _excludecategories) {
        excludecategories = _excludecategories;
    }

    public static String getExcludecategories() {
        return excludecategories;
    }

    public static void setFilter(String _filter) {
        filter = P6Util.isTrue(_filter, false);
    }

    public static boolean getFilter() {
        return filter;
    }

    public static void setInclude(String _include) {
        include = _include;
    }

    public static String getInclude() {
        return include;
    }

    public static void setIncludecategories(String _includecategories) {
        includecategories = _includecategories;
    }

    public static String getIncludecategories() {
        return includecategories;
    }

    public static boolean getDeregisterDrivers() {
        return deregister;
    }

    public static void setDeregisterDrivers(String trueOrFalse) {
        deregister = P6Util.isTrue(trueOrFalse, false);
    }

    public static void setLogfile(String _logfile) {
        logfile = _logfile;
        if (logfile == null) {
            logfile = "spy.log";
        }
    }

    public static String getLogfile() {
        return logfile;
    }

    public static String getAppender() {
        return appender;
    }

    public static void setAppender(String className) {
        appender = className;
    }

    public static void setRealdriver(String _realdriver) {
        realdriver = _realdriver;
    }

    public static String getRealdriver() {
        return realdriver;
    }

    public static void setRealdriver2(String _realdriver2) {
        realdriver2 = _realdriver2;
    }

    public static String getRealdriver2() {
        return realdriver2;
    }

    public static void setRealdriver3(String _realdriver3) {
        realdriver3 = _realdriver3;
    }

    public static String getRealdriver3() {
        return realdriver3;
    }

    public static void setAppend(String _append) {
        append = P6Util.isTrue(_append, true);
    }

    public static boolean getAppend() {
        return append;
    }

    public static void setSpydriver(String _spydriver) {
        spydriver = _spydriver;
        if (spydriver == null) {
            spydriver = "com.p6spy.engine.spy.P6SpyDriver";
        }
    }

    public static String getSpydriver() {
        return spydriver;
    }

    public static void setDateformat(String _dateformat) {
        dateformat = _dateformat;
    }

    public static String getDateformat() {
        return dateformat;
    }

    public static SimpleDateFormat getDateformatter() {
        if (dateformat == null || dateformat.equals("")) {
            return null;
        } else {
            return new SimpleDateFormat(dateformat);
        }
    }

    public static boolean getStackTrace() {
        return stackTrace;
    }

    public static void setStackTrace(String _stacktrace) {
        stackTrace = P6Util.isTrue(_stacktrace, false);
    }

    public static String getStackTraceClass() {
        return stackTraceClass;
    }

    public static void setStackTraceClass(String stacktraceclass) {
        stackTraceClass = stacktraceclass;
    }

    public static String getSQLExpression() {
        return sqlExpression;
    }

    public static void setSQLExpression(String sqlexpression) {
        if (sqlexpression != null && sqlexpression.equals("")) {
            sqlexpression = null;
        }
        sqlExpression = sqlexpression;
    }

    public static boolean getReloadProperties() {
        return reloadProperties;
    }

    public static void setReloadProperties(String _reloadproperties) {
        reloadProperties = P6Util.isTrue(_reloadproperties, false);
    }

    public static long getReloadPropertiesInterval() {
        return reloadPropertiesInterval;
    }

    public static void setReloadPropertiesInterval(String _reloadpropertiesinterval) {
        reloadPropertiesInterval = P6Util.parseLong(_reloadpropertiesinterval, -1l);
        reloadMs = reloadPropertiesInterval * 1000l;
    }

    public static void setJNDIContextFactory(String _jndicontextfactory) {
        jndicontextfactory = _jndicontextfactory;
    }

    public static String getJNDIContextFactory() {
        return jndicontextfactory;
    }

    public static void setJNDIContextProviderURL(String _jndicontextproviderurl) {
        jndicontextproviderurl = _jndicontextproviderurl;
    }

    public static String getJNDIContextProviderURL() {
        return jndicontextproviderurl;
    }

    public static void setJNDIContextCustom(String _jndicontextcustom) {
        jndicontextcustom = _jndicontextcustom;
    }

    public static String getJNDIContextCustom() {
        return jndicontextcustom;
    }

    public static void setRealDataSource(String _realdatasource) {
        realdatasource = _realdatasource;
    }

    public static String getRealDataSource() {
        return realdatasource;
    }

    public static void setRealDataSourceClass(String _realdatasourceclass) {
        realdatasourceclass = _realdatasourceclass;
    }

    public static String getRealDataSourceClass() {
        return realdatasourceclass;
    }

    public static void setRealDataSourceProperties(String _realdatasourceproperties) {
        realdatasourceproperties = _realdatasourceproperties;
    }

    public static String getRealDataSourceProperties() {
        return realdatasourceproperties;
    }

    @Override
    public void reload(P6SpyProperties properties) {
        // first set the values on this class
        P6LogQuery.debug(this.getClass().getName() + " reloading properties");

        Collections.reverse(modules = properties.getOrderedList(MODULE_PREFIX));
        Collections.reverse(driverNames = properties.getOrderedList(DRIVER_PREFIX));
        properties.setClassValues(P6SpyOptions.class);
        configureReloadingThread();
        P6LogQuery.initMethod();
        P6LogQuery.info("reloadProperties() successful");
    }

    protected static void configureReloadingThread() {
        if (reloadProperties) {
            // check to see if the thread is running.  If so,
            // then change the sleep factor. if not, then
            // spawn a new thread, etc.
            if (reloader == null) {
                reloader = new OptionReloader(reloadMs);
                reloadThread = new Thread(reloader);
                reloadThread.setDaemon(true);
                reloadThread.start();
            } else {
                reloader.setRunning(true);
                reloader.setSleep(reloadMs);
            }
        } else {
            // if it's false, and you're currently reloading
            // then turn it off so the thread will die
            if (reloader != null) {
                reloader.setRunning(false);
                reloader = null;
            }
        }
    }

    // this should actually be getAllModules but to make it easier for others to add
    // methods we'll just use allMethods
    public static List allModules() {
        return modules;
    }

    public static List<String> allDriverNames() {
        return driverNames;
    }

    /**
     * Returns the databaseDialectDateFormat.
     *
     * @return String
     */
    public static String getDatabaseDialectDateFormat() {
        return databaseDialectDateFormat;
    }

    /**
     * Sets the databaseDialectDateFormat.
     *
     * @param _databaseDialectDateFormat The databaseDialectDateFormat to set
     */
    public static void setDatabaseDialectDateFormat(String _databaseDialectDateFormat) {
        databaseDialectDateFormat = _databaseDialectDateFormat;
        if (_databaseDialectDateFormat == null || _databaseDialectDateFormat.length() == 0) {
            databaseDialectDateFormat = DEFAULT_DB_DATEFORMAT;
        }
    }

}
