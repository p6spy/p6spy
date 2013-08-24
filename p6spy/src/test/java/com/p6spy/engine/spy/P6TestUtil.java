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
 * Description: Test class for statements
 *
 * $Author: cheechq $
 * $Revision: 1.10 $
 * $Date: 2003/06/03 19:20:26 $
 *
 * $Id: P6TestUtil.java,v 1.10 2003/06/03 19:20:26 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestUtil.java,v $
 * $Log: P6TestUtil.java,v $
 * Revision 1.10  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.9  2003/03/07 22:06:46  aarvesen
 * made some things public
 * added in deregisterdrivers = true
 *
 * Revision 1.8  2003/02/14 22:22:16  aarvesen
 * use a define for the property file
 *
 * Revision 1.7  2003/01/28 19:32:32  jeffgoke
 * fixed bug exposed by test framework where option reloading was having problems if options were manipulated before the driver was created.
 *
 * Revision 1.6  2003/01/28 17:59:14  jeffgoke
 * fixed test cases to use new options
 *
 * Revision 1.5  2003/01/23 00:43:37  aarvesen
 * Changed the module to be dot rather than underscore
 *
 * Revision 1.4  2003/01/03 21:19:24  aarvesen
 * use the new P6Util.forName
 *
 * Revision 1.3  2002/12/19 23:45:18  aarvesen
 * change to be factory rather than driver
 *
 * Revision 1.2  2002/12/12 01:39:01  jeffgoke
 * no message
 *
 * Revision 1.1  2002/10/06 18:24:04  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:30:46  jeffgoke
 * version 1 rewrite
 *
 *
 *
 */

package com.p6spy.engine.spy;

import junit.framework.*;
import java.sql.*;
import java.io.*;
import java.util.*;

import com.p6spy.engine.common.*;
import org.apache.log4j.Logger;

import static org.junit.Assert.assertTrue;

@Deprecated // T6TestFramework has most if not all of this.
public class P6TestUtil  {
  private static final Logger LOG = Logger.getLogger(P6TestUtil.class);

    protected P6TestUtil() {
    }

    protected static Properties loadProperties(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("No properties file specified.");
        }

        Properties props = new Properties();

        InputStream inputStream = P6TestUtil.class.getResourceAsStream(filename);
        if ( inputStream == null ) {
            inputStream = new FileInputStream(filename);
        }
        try {
            props.load(inputStream);
        } finally {
            try {
                inputStream.close();
            } catch(Exception e) {
                // so earlier exception is not shadowed.
            }
        }
        return props;
    }

    protected static void writeProperty(String filename, Map props) throws IOException {
        File reload = new File(filename);
        reload.delete();

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(reload)));

        Iterator i = props.keySet().iterator();
        while (i.hasNext()) {
            String key = (String)i.next();
            String value = (String)props.get(key);
            out.println(key+"="+value);
        }

        out.close();
    }

    protected static void writeFile(String filename, ArrayList entries) throws IOException {
        File file = new File(filename);
        file.delete();

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

        for (int i = 0; i < entries.size(); i++) {
            String entry = (String)entries.get(i);
            out.println(entry);
        }

        out.close();
    }

    protected static Map getDefaultPropertyFile() throws IOException {

        Properties props = loadProperties(P6TestFramework.P6_TEST_PROPERTIES);
        String realdrivername = props.getProperty("p6realdriver");
        String realdrivername2 = props.getProperty("p6realdriver2");

        HashMap tp = new HashMap();
        tp.put("module.outage","com.p6spy.engine.outage.P6OutageFactory");
        tp.put("module.log","com.p6spy.engine.logging.P6LogFactory");
        tp.put("realdriver",realdrivername);
        tp.put("realdriver2",realdrivername2);
        tp.put("filter","false");
        tp.put("include","");
        tp.put("exclude","");
        tp.put("trace","true");
        tp.put("autoflush","true");
        tp.put("logfile","spy.log");
        tp.put("append","true");
        tp.put("dateformat","");
        tp.put("includecategories","");
        tp.put("excludecategories","debug,result,batch");
        tp.put("stringmatcher","");
        tp.put("stacktrace","false");
        tp.put("stacktraceclass","");
        tp.put("reloadproperties","false");
        tp.put("reloadpropertiesinterval","1");
        tp.put("useprefix","false");
        tp.put("outagedetection", "false");
        tp.put("outagedetectioninterval", "");
        tp.put("entries","");
        tp.put("forms","");
        tp.put("formsfile","testspy.forms");
        tp.put("formslog","testforms.log");
        tp.put("formstrace","true");
        tp.put("deregisterdrivers","true");
        return tp;
    }

    protected static void reloadProperty(Map props) throws IOException {
        writeProperty(/*"target/test-classes/com/p6spy/engine/spy/" + */P6TestFramework.PROPERTY_FILE, props);

        P6SpyProperties properties = new P6SpyProperties();
        properties.setSpyProperties(P6TestFramework.PROPERTY_FILE);
        OptionReloader.reload();
    }

    protected static void assertIsLastQuery(String query) {
        boolean isTrue = P6LogQuery.getLastEntry().indexOf(query) != -1;
        if (!isTrue) {
          LOG.error(query+" was not the last query, this was: "+P6LogQuery.getLastEntry());
        }
        assertTrue(isTrue);
    }

    protected static void assertIsNotLastQuery(String query) {
        boolean isFalse = P6LogQuery.getLastEntry().indexOf(query) == -1;
        if (!isFalse) {
            LOG.error(query+" was the last query and should not have been");
        }
        assertTrue(isFalse);
    }

    protected static void printAllDrivers() {
        for (Enumeration e = DriverManager.getDrivers() ; e.hasMoreElements() ;) {
            LOG.info("2 DRIVER FOUND == "+e.nextElement());
        }
    }

    protected static void unloadDrivers() throws Exception {
        Properties props = loadProperties("P6Test.properties");
        String drivername = props.getProperty("p6driver");
        String user = props.getProperty("user");
        String password = props.getProperty("password");
        String url = props.getProperty("url");

        try {
            Driver driver;
            if ( url != null && url.length() > 0) {
                while ((driver = DriverManager.getDriver(url)) != null) {
                    LOG.info("Deregistering driver: "+driver.getClass().getName());
                    DriverManager.deregisterDriver(driver);
                }
            }
        } catch (Exception e) {
        }
    }

    public static Connection loadDrivers(String drivernameProperty) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Properties props = loadProperties(P6TestFramework.P6_TEST_PROPERTIES);
        String drivername = props.getProperty(drivernameProperty);
        String user = props.getProperty("user");
        String password = props.getProperty("password");
        String url = props.getProperty("url");

        LOG.info("UTIL REGISTERING DRIVER PROPERTY == "+drivernameProperty+" TO REGISTER DRIVER == "+drivername);
        Class<Driver> driverClass = P6Util.forName(drivername);
        DriverManager.setLogWriter(new PrintWriter(System.out, true));
        // if a type 4 JDBC driver is unregistered, you must register it before using it!
        DriverManager.registerDriver(driverClass.newInstance());
        Driver driver = DriverManager.getDriver(url);
        LOG.info("UTIL USING DRIVER == "+driver.getClass().getName()+" FOR URL "+url);
        Connection connection = DriverManager.getConnection(url, user, password);
        printAllDrivers();
        return connection;
    }

}
