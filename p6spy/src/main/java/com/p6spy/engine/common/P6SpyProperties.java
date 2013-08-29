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

package com.p6spy.engine.common;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * this class contains a properties file and utility functions that enable this property file to be
 * set on any options class TODO: Error when spy.properties is in a jar Private: (?) No When the
 * properties file is in a jar, it is not read. It is because P6SpyProperties.java looks for
 * properties in a classpath directory. I suggest to use Class.getResourceAsStream() instead which
 * works even if the file is in a jar o zip.
 * 
 * @author jeff
 */
public class P6SpyProperties {

    // class variables dealing with the filename
    protected static final String OPTIONS_FILE_PROPERTY = "spy.properties";
    protected static final String DFLT_OPTIONS_FILE     = "spy.properties";
    protected static String SPY_PROPERTIES_FILE;
    protected static long propertiesLastModified = -1;
    protected static String propertiesPath;

    static {
        initMethod();
    }

    public static void initMethod() {
        P6SpyProperties.setSpyProperties(System.getProperty(OPTIONS_FILE_PROPERTY, DFLT_OPTIONS_FILE));
    }

    // instance variables dealing with an instance of the properties file we pass around
    public Properties properties;

    /** Creates a new instance of P6SpyProperties */
    public P6SpyProperties() {
        // we only reload the properties information if it has changed
        File propertiesFile = new File(propertiesPath);
        if(propertiesFile.exists()) {
            long lastModified = propertiesFile.lastModified();
            if(lastModified != propertiesLastModified) {
                propertiesLastModified = lastModified;
                properties = P6Util.loadProperties(SPY_PROPERTIES_FILE);
            } else {
                properties = null;
            }
        }
    }

    public boolean isNewProperties() {
        return (properties != null);
    }

    public static String getPropertiesPath() {
        return propertiesPath;
    }

    /* set the name of the property file */
    public static void setSpyProperties(String _properties) {
        SPY_PROPERTIES_FILE = _properties == null ? DFLT_OPTIONS_FILE : _properties;
        propertiesPath = P6SpyProperties.findPropertiesPath();
        propertiesLastModified = -1;
    }

    /* gets the full path, including filename, of the property file */
    protected static String findPropertiesPath() {
        String propertiesPath = P6Util.classPathFile(SPY_PROPERTIES_FILE);
        if (propertiesPath != null) {
            File propertiesFile = new File(propertiesPath);
            if (propertiesFile.exists()) {
                return propertiesPath;
            }
        }
        return null;
    }

    // save the file down
    public static void saveProperties() {
        Properties props = new Properties();
        for (P6Options opts : OptionReloader.options) {
            Method[] allMethods = opts.getClass().getDeclaredMethods();
            for (Method m : allMethods) {
                if (!m.getName().startsWith("get")) {
                    continue;
                }
                Class[] params = m.getParameterTypes();
                if (params.length != 0) {
                    continue;
                }
                String propertyName = m.getName().substring(3).toLowerCase();
                    Object rv;
                    try {
                        rv = m.invoke(opts);
                        if (rv != null) {
                            props.setProperty(propertyName, rv.toString());
                            P6LogQuery.info("added property '" + propertyName + "' with value of '" + rv + "'");
                        }
                    } catch (IllegalAccessException e ) {
                      throw new IllegalStateException(e);
                    } catch(IllegalArgumentException e ) {
                      throw new IllegalStateException(e);
                    } catch( InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    }
            }
        }

        // now your props object should have everything you want,
        // so open up the file and write out the data
        // NB I don't let you change the name of your properties... maybe
        // in the future

        //->JAW removed this line of code -- File propertiesFile = new File(propertiesPath);
        //since its only use was in a constructor that is only available in JDK1.4.
        //Also then changed the offending constructor and placed close into finally
        //block where it belongs.  Also fixed wording of error message.

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(propertiesPath, false);
            props.store(out, "P6Spy configuration");
            out.flush();
            P6LogQuery.info("successfully saved properties to file " + propertiesPath);
            //propertiesLastModified = propertiesFile.lastModified();
        } catch (Exception e) {
            P6LogQuery.error("Could not save to property file " + propertiesPath + " because of error " + e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
                P6LogQuery.error("Could not close property file " + propertiesPath + " because of error " + ioe);
            }
        }
        //->JAW
    }

    /*
     * this returns a refreshed version of the property file, regardless of whether anything has
     * changed or not.
     */
    public Properties forceReadProperties() {
        File propertiesFile = new File(propertiesPath);
        if (propertiesFile.exists()) {
            long lastModified = propertiesFile.lastModified();
            properties = P6Util.loadProperties(SPY_PROPERTIES_FILE);
        }
        return properties;
    }

    public void setClassValues(Class klass) {
        // only invoke this if the property file has been changed
        if (properties == null) {
            return;
        }

        // first load the properties from the property file
        List<String> allMethods = P6Util.findAllMethods(klass);
        for(String m : allMethods) {
            // lowercase and strip the end
            String methodName = m.substring(3);
            String value = (String)properties.get(methodName.toLowerCase());
            P6Util.dynamicSet(klass, "set"+methodName, value == null ? null : value.trim());
        }

        // next, check the environment and see if we should override any properties
        Collection<String> list = P6Util.findAllMethods(klass);

        P6LogQuery.info("Using properties file: "+propertiesPath);

        for (String opt : list) {
            String value = System.getProperty("p6" + opt);

            if (value != null) {
                P6LogQuery.info("Found value in environment: "+opt+", setting to value: "+value);
                P6Util.dynamicSet(klass, opt,value);
            } else {
                P6LogQuery.info("No value in environment for: "+opt+", using: "+P6Util.dynamicGet(klass,opt));
            }
        }
    }

    public List<String> getOrderedList(String prefix) {
        List<String> orderedList = new ArrayList<String>();
        List<KeyValue> list = P6Util.loadProperties(SPY_PROPERTIES_FILE, prefix);
        for (KeyValue keyValue : list) {
            String value = keyValue.getValue();
            orderedList.add(value);
        }
        return orderedList;
    }
}
