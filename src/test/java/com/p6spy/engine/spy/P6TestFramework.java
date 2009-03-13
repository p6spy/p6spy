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
 * $Revision: 1.11 $
 * $Date: 2003/06/03 19:20:26 $
 *
 * $Id: P6TestFramework.java,v 1.11 2003/06/03 19:20:26 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestFramework.java,v $
 * $Log: P6TestFramework.java,v $
 * Revision 1.11  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.10  2003/04/09 16:44:00  jeffgoke
 * Added Jboss JMX support.  Updated documentation.  Added execution threshold property to only log queries taking longer than a specified time.
 *
 * Revision 1.9  2003/02/14 22:22:58  aarvesen
 * use a define for the property file
 *
 * Revision 1.8  2003/01/28 19:32:31  jeffgoke
 * fixed bug exposed by test framework where option reloading was having problems if options were manipulated before the driver was created.
 *
 * Revision 1.7  2003/01/28 17:59:12  jeffgoke
 * fixed test cases to use new options
 *
 * Revision 1.6  2003/01/23 00:43:37  aarvesen
 * Changed the module to be dot rather than underscore
 *
 * Revision 1.5  2003/01/03 21:19:24  aarvesen
 * use the new P6Util.forName
 *
 * Revision 1.4  2002/12/19 23:46:54  aarvesen
 * use factory rather than driver
 *
 * Revision 1.3  2002/12/18 01:03:03  aarvesen
 * Remove no-longer-used p6cache driver
 *
 * Revision 1.2  2002/10/06 18:24:04  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:30:46  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.4  2002/05/18 06:39:52  jeffgoke
 * Peter Laird added Outage detection.  Added junit tests for outage detection.
 * Fixed multi-driver tests.
 *
 * Revision 1.3  2002/05/16 04:58:40  jeffgoke
 * Viktor Szathmary added multi-driver support.
 * Rewrote P6SpyOptions to be easier to manage.
 * Fixed several bugs.
 *
 * Revision 1.2  2002/05/05 00:43:00  jeffgoke
 * Added Philip's reload code.
 *
 * Revision 1.1  2002/04/21 06:16:20  jeffgoke
 * added test cases, fixed batch bugs
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

public abstract class P6TestFramework extends TestCase {
    public static final String PROPERTY_FILE = "reloadtest.properties";
    
    public P6TestFramework(java.lang.String testName) {
        super(testName);
    }
    
    protected Connection connection = null;
    
    protected void setUp() {
        try {
            // we are going to use a testspy.forms file for these tests
            ArrayList forms = getDefaultSpyForms();
            writeFile("testspy.forms", forms);
            
            // we are going to use the reloadtest.properties file for all tests
            // this is a scratch file that won't hurt spy.properties
            HashMap tp = getDefaultPropertyFile();
            reloadProperty(tp);
            Properties props = loadProperties("P6Test.properties");
            String drivername = props.getProperty("p6driver");
            String user = props.getProperty("user");
            String password = props.getProperty("password");
            String url = props.getProperty("url");
            
	    P6Util.forName(drivername);
            Driver driver = DriverManager.getDriver(url);
            System.err.println("FRAMEWORK USING DRIVER == "+driver.getClass().getName()+" FOR URL "+url);
            connection = DriverManager.getConnection(url, user, password);
            
            printAllDrivers();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    protected Properties loadProperties(String filename) {
        if (filename == null) {
            System.err.println("No "+filename+" properties file specified.");
            System.exit(1);
        }
        
        Properties props = new Properties();
        
        try {
            FileInputStream fis = new FileInputStream(filename);
            if (fis == null) {
                System.err.println("Unable to find properties file: "+filename);
                System.exit(1);
            }
            props.load(fis);
            fis.close();
        }
        catch (IOException e) {
            System.err.println("Unable to read properties from properties file: "+filename+".  Exception: "+e.toString());
            System.exit(1);
        }
        return props;
    }
    
    protected void writeProperty(String filename, HashMap props) {
        try {
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
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    protected void writeFile(String filename, ArrayList entries) {
        try {
            File file = new File(filename);
            file.delete();
            
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));            
            
            for (int i = 0; i < entries.size(); i++) {
                String entry = (String)entries.get(i);
                out.println(entry);
            }
            
            out.close();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    protected ArrayList getDefaultSpyForms() {
        ArrayList formsLog = new ArrayList();
        formsLog.add("5 seconds; select count(*) from cache_test");
        formsLog.add("5 seconds; select col2 from cache_test where col1 != ? and col1 != ? and col1 like ?");
        return formsLog;
    }
    
    protected HashMap getDefaultPropertyFile() {
        
        Properties props = loadProperties("P6Test.properties");
        String realdrivername = props.getProperty("p6realdriver");
        
        Properties props2 = loadProperties("P6Test.properties");
        String realdrivername2 = props2.getProperty("p6realdriver2");
        
        HashMap tp = new HashMap();
        tp.put("module.outage","com.p6spy.engine.outage.P6OutageFactory");
        tp.put("module.log","com.p6spy.engine.logging.P6LogFactory");
        tp.put("realdriver",realdrivername);
        tp.put("realdriver2",realdrivername2);
        tp.put("filter","false");
        tp.put("executionthreshold","");
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
        tp.put("cache","true");
        tp.put("cachetrace","false");
        tp.put("clearcache","");
        tp.put("entries","");
        tp.put("forms","");
        tp.put("formsfile","testspy.forms");
        tp.put("formslog","testforms.log");
        tp.put("formstrace","true");
        return tp;
    }
    
    protected void reloadProperty(HashMap props) {
        try {
            writeProperty(PROPERTY_FILE, props);
            P6SpyProperties properties = new P6SpyProperties();
            properties.setSpyProperties(PROPERTY_FILE);
            OptionReloader.reload();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    protected void assertIsLastQuery(String query) {
        boolean isTrue = P6LogQuery.getLastEntry().indexOf(query) != -1;
        if (!isTrue) {
            System.err.println(query+" was not the last query, this was: "+P6LogQuery.getLastEntry());
        }
        assertTrue(isTrue);
    }
    
    protected void assertIsNotLastQuery(String query) {
        boolean isFalse = P6LogQuery.getLastEntry().indexOf(query) == -1;
        if (!isFalse) {
            System.err.println(query+" was the last query and should not have been");
        }
        assertTrue(isFalse);
    }
    
    protected static String getStackTrace(Exception e) {
        CharArrayWriter c = new CharArrayWriter();
        e.printStackTrace(new PrintWriter(c));
        return c.toString();
    }
    
    protected static void printAllDrivers() {
        for (Enumeration e = DriverManager.getDrivers() ; e.hasMoreElements() ;) {
            System.err.println("1 DRIVER FOUND == "+e.nextElement());
        }
    }
}
