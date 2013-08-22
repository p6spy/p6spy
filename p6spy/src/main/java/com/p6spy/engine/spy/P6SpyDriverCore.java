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

/**
 * Description: Wrapper class for Driver
 *
 * $Author: cheechq $
 * $Revision: 1.15 $
 * $Date: 2003/06/03 19:20:26 $
 *
 * $Id: P6SpyDriverCore.java,v 1.15 2003/06/03 19:20:26 cheechq Exp $
 * $Log: P6SpyDriverCore.java,v $
 * Revision 1.15  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.14  2003/04/10 18:17:57  aarvesen
 * always check to see if there are drivers to dereg.  Then, either deregister them or warn that they'll prevent you from functioning properly
 *
 * Revision 1.13  2003/03/07 22:08:09  aarvesen
 * added deregistration code
 *
 * Revision 1.12  2003/02/24 17:56:28  dlukeparker
 * Removed debug output
 *
 * Revision 1.11  2003/02/24 17:45:05  dlukeparker
 * Clarified error reporting when spy.properties is not found
 *
 * Revision 1.10  2003/01/28 19:32:31  jeffgoke
 * fixed bug exposed by test framework where option reloading was having problems if options were manipulated before the driver was created.
 *
 * Revision 1.9  2003/01/28 17:01:13  jeffgoke
 * rewrote options to the ability for a module to have its own option set
 *
 * Revision 1.8  2003/01/18 00:26:35  jeffgoke
 * fixed a bug where new instances of the driver (not using driver manager but creating instances of the driver yourself) were causing the connection to be null
 *
 * Revision 1.7  2003/01/16 00:50:03  jeffgoke
 * changed Error call to use syntax compatible prior to 1.4
 *
 * Revision 1.6  2003/01/15 22:11:52  aarvesen
 * do some stronger error trapping and die on error
 *
 * Revision 1.5  2003/01/10 21:40:11  jeffgoke
 * changed to use new error handling facility
 *
 * Revision 1.4  2003/01/03 21:18:03  aarvesen
 * use the new P6Util.forName
 *
 * Revision 1.3  2002/12/20 00:04:09  aarvesen
 * New style of driver!
 *
 * Revision 1.2  2002/10/06 18:23:25  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:31:13  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.1  2002/05/16 04:58:40  jeffgoke
 * Viktor Szathmary added multi-driver support.
 * Rewrote P6SpyOptions to be easier to manage.
 * Fixed several bugs.
 *
 * Revision 1.6  2002/05/05 00:43:00  jeffgoke
 * Added Philip's reload code.
 *
 * Revision 1.5  2002/04/15 05:13:32  jeffgoke
 * Simon Sadedin added timing support.  Fixed bug where batch execute was not
 * getting logged.  Added result set timing.  Updated the log format to include
 * categories, and updated options to control the categories.  Updated
 * documentation.
 *
 * Revision 1.4  2002/04/10 06:49:26  jeffgoke
 * added more debug information and a new property for setting the log's date format
 *
 * Revision 1.3  2002/04/10 05:22:09  jeffgoke
 * included debug option and a message at driver initialization time
 *
 * Revision 1.2  2002/04/07 20:43:59  jeffgoke
 * fixed bug that caused null connection to return an empty connection instead of null.
 * added an option allowing the user to truncate.
 * added a release target to the build to create the release files.
 *
 * Revision 1.1.1.1  2002/04/07 04:52:25  jeffgoke
 * no message
 *
 * Revision 1.3  2001-08-02 07:52:44-05  andy
 * <>
 *
 * Revision 1.2  2001-07-30 23:37:33-05  andy
 * <>
 *
 * Revision 1.1  2001-07-30 23:03:31-05  andy
 * <>
 *
 * Revision 1.0  2001-07-30 17:46:23-05  andy
 * Initial revision
 *
 */

package com.p6spy.engine.spy;

import com.p6spy.engine.common.OptionReloader;
import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6Options;
import com.p6spy.engine.common.P6SpyOptions;
import com.p6spy.engine.common.P6SpyProperties;
import com.p6spy.engine.common.P6Util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public abstract class P6SpyDriverCore implements Driver {

    protected Driver passthru;

    protected static boolean initialized;

    protected static List<P6Factory> factories;

    protected static List<Driver> realDrivers = new CopyOnWriteArrayList<Driver>();

    protected static boolean foundSpyProperties;

    /*
     * This core class serves to purposes
     *
     * (1) it acts as a bootstap class the first time
     * it is invoked and it loads not itself, but the first driver on the stack.  This is
     * important because P6SpyDriver, P6SpyDriver2, etc. extend this class and performs
     * the initial bootstrap
     *
     * (2) when connect or acceptURL are invoked it ensures it has a passthru driver.
     *
     *
     */

    public synchronized static void initMethod(String spydriver) {
        // this is the *only* p6 driver
        // we need to build two lists here:
        // one of the modules that are loaded, and one of the
        // realdriver(s) that we need

        // these are defined outside the try block for error messaging

        if (initialized) {
            return;
        }

        // first thing we want to do is load the core options file, but
        // we don't know where it is, so find out, then set up to throw
        // an exception from the constructor if it cannot be found.
        String path = P6SpyProperties.getPropertiesPath();
        if (path == null) {
            foundSpyProperties = false;
            return;
        }

        foundSpyProperties = true;

        P6SpyProperties properties = new P6SpyProperties();
        P6SpyOptions coreOptions = new P6SpyOptions();
        OptionReloader.add(coreOptions, properties);

        // now register the core options file with the reloader

        String className = "no class";
        String classType = "driver";
        try {
            List<String> driverNames = P6SpyOptions.allDriverNames();
            List modules = P6SpyOptions.allModules();

            boolean hasModules = modules.size() > 0;


            // register drivers and wrappers
            classType = "driver";
            for (String driverName : driverNames) {
                P6SpyDriver spy = null;
                // register P6 first if you are using it
                if (hasModules) {
                    spy = new P6SpyDriver();
                    DriverManager.registerDriver(spy);
                }

                // this is bogus, but we need to make *sure*
                // that p6 is registered before your real drivers.  Otherwise
                // the real driver will intercept the call before p6 gets it.
                // so, deregister the driver if nec.
                className = driverName;
                deregister(className);
                Driver realDriver = (Driver) P6Util.forName(className).newInstance();
                if (P6SpyOptions.getDeregisterDrivers()) {
                    // just in case you had to deregister
                    DriverManager.registerDriver(realDriver);
                }

                // now wrap your realDriver in the spy
                if (hasModules) {
                    spy.setPassthru(realDriver);
                    realDrivers.add(realDriver);
                }

                P6LogQuery.debug("Registered driver: " + className + ", realdriver: " + realDriver);
            }

            // instantiate the factories, if nec.
            if (hasModules) {
                factories = new CopyOnWriteArrayList<P6Factory>();
                classType = "factory";

                for (Iterator<String> i = modules.iterator();i.hasNext();) {
                    className = i.next();
                    P6Factory factory = (P6Factory) P6Util.forName(className).newInstance();
                    factories.add(factory);

                    P6Options options = factory.getOptions();
                    if (options != null) {
                        OptionReloader.add(options, properties);
                    }

                    P6LogQuery.debug("Registered factory: " + className + " with options: " + options);
                }
            }

            initialized = true;

            for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
                P6LogQuery.debug("Driver manager reporting driver registered: " + e.nextElement());
            }

        } catch (Exception e) {
            String err = "Error registering " + classType + "  [" + className + "]\nCaused By: " + e.toString();
            P6LogQuery.error(err);
            throw new P6DriverNotFoundError(err);
        }

    }

    static void deregister(String className) throws SQLException {
        List<Driver> dereg = new ArrayList<Driver>();
        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
            Driver driver = e.nextElement();

            // once you reach a P6 driver, you can jump out
            if (driver instanceof P6SpyDriver) {
                break;
            }

            // now you have to be careful of concurrent update
            // exceptions here, so save the drivers for later
            // deregistration
            if (driver.getClass().getName().equals(className)) {
                dereg.add(driver);
            }
        }

        // if you found any drivers let's dereg them now
        int size = dereg.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Driver driver = dereg.get(i);
                if (P6SpyOptions.getDeregisterDrivers()) {
                    P6LogQuery.info("deregistering driver " + driver.getClass().getName());
                    DriverManager.deregisterDriver(driver);
                } else {
                    P6LogQuery
                        .error("driver "
                            + driver.getClass().getName()
                            + " is a real driver in spy.properties, but it has been loaded before p6spy.  p6spy will not wrap these connections.  Either prevent the driver from loading, or try setting 'deregisterdrivers' to true in spy.properties");
                }
            }
        }

    }

    public P6SpyDriverCore(String _spydriver, P6Factory _p6factory) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            SQLException {
        // if we couldn't find the spy.properties file, complain here
        if (!foundSpyProperties) {
            throw (new InstantiationException("spy.properties not found in classpath"));
        }
        // should really change the constructor here :)
    }

    // these methods are the secret sauce here
    public static Connection wrapConnection(Connection realConnection) throws SQLException {
        Connection con = realConnection;
        if (factories != null) {
            for (P6Factory factory : factories) {
                con = factory.getConnection(con);
            }
        }
        return con;
    }

    public Driver getPassthru() {
        return passthru;
    }

    public void setPassthru(Driver inVar) {
        passthru = inVar;
    }

    private String getRealUrl(String url) {
        if (P6SpyOptions.getUsePrefix()) {
            return url.startsWith("p6spy:") ? url.substring("p6spy:".length()) : null;
        } else {
            return url;
        }
    }

    // the remaining methods are for the Driver interface
    public Connection connect(String p0, java.util.Properties p1) throws SQLException {
        String realUrl = this.getRealUrl(p0);
        // if there is no url, we have problems
        if (realUrl == null) {
            throw new SQLException("realURL is null, needs the p6spy prefix: " + p0);
        }

        // lets try to find the driver from the multiple divers in spy.properties
        findPassthru(realUrl);
        // if we can't find one, it may not be defined
        if (passthru == null) {
            throw new SQLException("Unable to find a driver that accepts " + realUrl);
        }

        P6LogQuery.debug("this is " + this + " and passthru is " + passthru);
        if (passthru == null) {
            findPassthru(realUrl);
        }

        Connection conn = passthru.connect(realUrl, p1);

        if (conn != null) {
            conn = wrapConnection(conn);
        }
        return conn;
    }

    protected void findPassthru(String url) {
        for (Driver driver : realDrivers) {
            try {
                if (driver.acceptsURL(url)) {
                    passthru = driver;
                    P6LogQuery.debug("found new driver " + driver);
                    break;
                }
            } catch (SQLException e) {
            }
        }
    }

    /**
     * for some reason the passthru is null, go create one
     */
    public boolean acceptsURL(String p0) throws SQLException {
        String realUrl = this.getRealUrl(p0);
        boolean accepts = false;

        // somehow we get initilized but no driver is created,
        // lets try findPassthru
        if (passthru == null && initialized) {
            // we should have some drivers
            if (realDrivers.size() == 0) {
                throw new SQLException("P6 has no drivers registered");
            } else {
                findPassthru(realUrl);
                // if we are still null, we have issues
                if (passthru == null) {
                    throw new SQLException("P6 can't find a driver to accept url (" + realUrl + ") from the " + realDrivers.size()
                        + " drivers P6 knows about. The current driver is null");
                }
            }
        }

        if (realUrl != null) {
            accepts = passthru.acceptsURL(realUrl);
        }
        return accepts;
    }

    public DriverPropertyInfo[] getPropertyInfo(String p0, java.util.Properties p1) throws SQLException {
        return (passthru.getPropertyInfo(p0, p1));
    }

    public int getMajorVersion() {
        return (passthru.getMajorVersion());
    }

    public int getMinorVersion() {
        return (passthru.getMinorVersion());
    }

    public boolean jdbcCompliant() {
        return (passthru.jdbcCompliant());
    }

    // since 1.7
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return passthru.getParentLogger();
    }
}
