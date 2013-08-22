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
 * Description: Utility classes
 *
 * $Author: cheechq $
 * $Revision: 1.15 $
 * $Date: 2003/06/03 19:20:20 $
 *
 * $Id: P6Util.java,v 1.15 2003/06/03 19:20:20 cheechq Exp $
 * $Log: P6Util.java,v $
 * Revision 1.15  2003/06/03 19:20:20  cheechq
 * removed unused imports
 *
 * Revision 1.14  2003/04/15 02:49:09  cheechq
 * bug fix for jdk 1.2 support
 *
 * Revision 1.13  2003/04/15 02:10:54  cheechq
 * jdk 1.2 support mod
 *
 * Revision 1.12  2003/02/28 07:28:10  jeffgoke
 * added bug fix to enable jdk 1.2 support
 *
 * Revision 1.11  2003/02/12 15:32:14  jeffgoke
 * Fixed bug where space in the realdriver name was causing the system to crash
 *
 * Revision 1.10  2003/01/29 05:38:42  jeffgoke
 * added fix to not close reader if it is null
 *
 * Revision 1.9  2003/01/28 17:00:58  jeffgoke
 * rewrote options to the ability for a module to have its own option set
 *
 * Revision 1.8  2003/01/23 23:43:51  jeffgoke
 * try the thread and classloader system resources if we can't find the class loader
 *
 * Revision 1.7  2003/01/23 01:40:45  jeffgoke
 * added code to try to use the classpath loader first, and if that fails, try our manual method
 *
 * Revision 1.6  2003/01/15 22:09:41  aarvesen
 * Don't crap out if you can't find a context loader, press on
 *
 * Revision 1.5  2003/01/10 21:39:43  jeffgoke
 * removed p6util.warn and moved warn handling to logging.  this gives a consistent log file.
 *
 * Revision 1.4  2003/01/08 18:11:13  aarvesen
 * Trap the no more element exception to avoid a stupid crashing bug.
 *
 * Revision 1.3  2003/01/03 21:14:54  aarvesen
 * added the (unused) removeDots method
 * added the (widely used) forName method to implement better class loading
 *
 * Revision 1.2  2002/10/06 18:21:37  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:32:01  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.7  2002/05/18 06:39:52  jeffgoke
 * Peter Laird added Outage detection.  Added junit tests for outage detection.
 * Fixed multi-driver tests.
 *
 * Revision 1.6  2002/05/16 04:58:40  jeffgoke
 * Viktor Szathmary added multi-driver support.
 * Rewrote P6SpyOptions to be easier to manage.
 * Fixed several bugs.
 *
 * Revision 1.5  2002/05/05 00:43:00  jeffgoke
 * Added Philip's reload code.
 *
 * Revision 1.4  2002/04/15 05:13:32  jeffgoke
 * Simon Sadedin added timing support.  Fixed bug where batch execute was not
 * getting logged.  Added result set timing.  Updated the log format to include
 * categories, and updated options to control the categories.  Updated
 * documentation.
 *
 * Revision 1.3  2002/04/10 06:49:26  jeffgoke
 * added more debug information and a new property for setting the log's date format
 *
 * Revision 1.2  2002/04/07 20:43:59  jeffgoke
 * fixed bug that caused null connection to return an empty connection instead of null.
 * added an option allowing the user to truncate.
 * added a release target to the build to create the release files.
 *
 * Revision 1.1.1.1  2002/04/07 04:52:26  jeffgoke
 * no message
 *
 * Revision 1.4  2001-08-05 09:16:03-05  andy
 * version on the website
 *
 * Revision 1.3  2001-08-02 07:52:43-05  andy
 * <>
 *
 * Revision 1.2  2001-07-30 23:37:24-05  andy
 * <>
 *
 * Revision 1.1  2001-07-30 23:03:32-05  andy
 * <>
 *
 * Revision 1.0  2001-07-30 17:49:09-05  andy
 * Initial revision
 *
 */

package com.p6spy.engine.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * [ 2496595 ] spy.properties file not found when path contains a space
You may monitor this Tracker item after you log in (register an account, if you do not already have one)
Submitted By:
David McMeans - dmcmeans    Date Submitted:
2009-01-09 22:20
Last Updated By:
dmcmeans - Attachment added     Date Last Updated:
2009-01-09 22:20
Number of Comments:
0   Number of Attachments:
1
Category: (?)
None    Group: (?)
None
Assigned To: (?)
Nobody/Anonymous    Priority: (?)
5
Status: (?)
Open    Resolution: (?)
None
Summary: (?)
spy.properties file not found when path contains a space    Private: (?)
No
p6spy fails to load when the path to the properties file contains a space.

E.g.
/E:/Program%20Files/apache-tomcat-5.5.26/webapps/myapp/WEB-INF/classes/spy.
properties

We narrowed the problem to classLoadPropertyFile() in P6Util.java. The
solution is to decode the URL path before passing it to the File
constructor:

public static File classLoadPropertyFile(java.net.URL purl) {
try {
if (purl != null) {
// modified by jayakumar for JDK 1.2 support
//return new File(purl.getPath());
// return new File(getPath(purl));

// modified by davidmcmeans to handle %20's, etc. in the
URL path
return new File( URLDecoder.decode( purl.getPath(), "UTF-8"
));

// end of modification
}
} catch (Exception e) {
// we ignore this, since JDK 1.2 does not support this method
}
return null;
}
 * @author patmoore
 *
 */
public class P6Util {

    public static int parseInt(String i, int defaultValue) {
        if (i == null || i.equals("")) {
            return defaultValue;
        }
        try {
            return (Integer.parseInt(i));
        }
        catch(NumberFormatException nfe) {
            P6LogQuery.error("NumberFormatException occured parsing value "+i);
            return defaultValue;
        }
    }

    public static long parseLong(String l, long defaultValue) {
        if (l == null || l.equals("")) {
            return defaultValue;
        }
        try {
            return (Long.parseLong(l));
        } catch(NumberFormatException nfe) {
            P6LogQuery.error("NumberFormatException occured parsing value "+l);
            return defaultValue;
        }
    }

    public static boolean isTrue(String s, boolean defaultValue) {
        if (s == null) {
            return defaultValue;
        }
        return(s.equals("1") || s.trim().equalsIgnoreCase("true"));
    }

    public static int atoi(Object s) {
        int i = 0;

        if (s != null) {
            String n = s.toString();
            int dot = n.indexOf('.');
            if (dot != -1) {
                n = n.substring(0,dot);
            }

            try {
                i = Integer.valueOf(n).intValue();
            } catch (NumberFormatException e) {
                i = 0;
            }
        }

        return(i);
    }

    public static Properties loadProperties(String file) {
        Properties props = new Properties();
        try {
            String path = classPathFile(file);
            if (path == null) {
                P6LogQuery.error("Can't find " + file + ". "+getCheckedPath());
            } else {
                FileInputStream in = new FileInputStream(path);
                props.load(in);
                //removeDots(props);
                in.close();
            }
        } catch (FileNotFoundException e1) {
            P6LogQuery.error("File not found " + file + " " + e1);
        } catch (IOException e2) {
            P6LogQuery.error("IO Error reading file " + file + " " + e2);
        }

        return props;
    }

    protected static void removeDots(Properties props) {
        Map<String, String> hash     = new HashMap<String, String>();
        boolean done     = false;

        for(Enumeration<Object> keys = props.keys(); keys.hasMoreElements();) {
            String key = (String) keys.nextElement();
            if (key.indexOf('.') != -1) {
                int len    = key.length();
                int newLen = 0;
                char[] car = new char[len];
                for (int i = 0; i < len; i++) {
                    char c = key.charAt(i);
                    if (c != '.') {
                        car[newLen++] = c;
                    }
                }

                String out = new String(car, 0, newLen);
                hash.put(out, props.getProperty(key));
            }
        }

        if (done) {
            props.putAll(hash);
        }
    }

    protected static String getCheckedPath() {
        String checkedPath = "\n\nClassloader via thread: <"+getClassPathAsString(Thread.currentThread().getContextClassLoader())+">\n\n";
        checkedPath += "Classloader via Class: <"+getClassPathAsString(P6Util.class.getClassLoader())+">\n\n";
        checkedPath += "java.class.path: <"+System.getProperty("java.class.path")+">\n\n";
        return checkedPath;
    }

    protected static String getClassPathAsString(ClassLoader classLoader) {
        String path = "";
        try {
            URL[] urls = ((URLClassLoader)classLoader).getURLs();
            for (URL url : urls) {
                if (path != "") {
                    path += ";";
                }
                path += url.toString();
            }
        } catch(ClassCastException e) {
        }
        return path;
    }

    // this is our own version, which we need to do to ensure the order is kept
    // in the property file
    public static List<KeyValue> loadProperties(String file, String prefix) {
        List<KeyValue> props = new ArrayList<KeyValue>();
        FileReader in = null;
        BufferedReader reader = null;

        try {
            String path = classPathFile(file);

            if (path == null) {
                P6LogQuery.error("Can't find " + file + ". "+getCheckedPath());
            } else {
                in = new FileReader(path);
                // read the file
                reader = new BufferedReader(in);
                String line;
                while ((line = reader.readLine()) != null) {
                    if ((line.trim()).startsWith(prefix)) {
                        StringTokenizer st = new StringTokenizer(line, "=");
                        try {
                            String name = st.nextToken();
                            String value = st.nextToken();
                            KeyValue kv = new KeyValue(name.trim(), value.trim());
                            props.add(kv);
                        } catch (NoSuchElementException e) {
                            // ignore; means that you have
                            // something like:
                            // realdriver2=
                        }
                    }
                }
            }
        } catch (FileNotFoundException e1) {
            P6LogQuery.error("File not found " + file + " " + e1);
        } catch (IOException e2) {
            P6LogQuery.error("IO Error reading file " + file + " " + e2);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
        return props;
    }

    /**
     * Here we attempt to find the file in the current dir and the classpath
     * If we can't find it then we return null
     */
    public static String classPathFile(String file) {
        File fp             = null;
        String path         = null;
        String separator    = System.getProperty("path.separator");
        String slash        = System.getProperty("file.separator");
        String classpath    = "." + separator + System.getProperty("java.class.path");
        String local        = System.getProperty("p6.home");

        // first try to load options via the classloader
        try {
            // If p6.home is specified, just look there
            if (local != null) {
                fp = new File(local, file);
            } else {
                // try to get the classloader via the current thread
                fp = classLoadPropertyFile(Thread.currentThread().getContextClassLoader().getResource(file));

                if (fp == null) {
                    // next try the current class
                    fp = classLoadPropertyFile(P6Util.class.getClassLoader().getResource(file));
                }

                if (fp == null) {
                    // now the ClassLoader system resource
                    classLoadPropertyFile(ClassLoader.getSystemResource(file));
                }
            }

            if (fp.exists()) {
                return fp.getCanonicalPath();
            }
        } catch (Exception exc) {
        }

        // if that failed, see what we can do on our own
        StringTokenizer tok = new StringTokenizer(classpath, separator);

        do {
            String dir = tok.nextToken();
            path = dir.equals(".") ? file : dir + slash + file;
            fp = new File(path);
        } while (!fp.exists() && tok.hasMoreTokens());

        return fp.exists() ? path : null;
    }

    public static File classLoadPropertyFile(java.net.URL purl) {
        try {
            if (purl != null) {
            	// modified by jayakumar for JDK 1.2 support
                //return new File(purl.getPath());
                return new File(getPath(purl));
                // end of modification
            }
        } catch (Exception e) {
            // we ignore this, since JDK 1.2 does not suppport this method
        }
        return null;
    }

    public static java.util.Date timeNow() {
        return(new java.util.Date());
    }

    public static PrintStream getPrintStream(String file, boolean append) throws IOException {
        FileOutputStream  fw  = new FileOutputStream(file, append);
        PrintStream stream = new PrintStream(fw, P6SpyOptions.getAutoflush());
        return(stream);
    }

    public static String timeTaken(java.util.Date start, String msg) {
        double t = (double) elapsed(start) / (double) 1000;
        return "Time: " + msg + ": " + t;
    }

    public static long elapsed(java.util.Date start) {
        return(start == null) ? 0 : (timeNow().getTime() - start.getTime());
    }

    /**
     * A utility for using the current class loader (rather than the
     * system class loader) when instantiating a new class.
     * <p>
     * The idea is that the thread's current loader might have an
     * obscure notion of what your class path is (e.g. an app server) that
     * will not be captured properly by the system class loader.
     * <p>
     * taken from http://sourceforge.net/forum/message.php?msg_id=1720229
     *
     * @param name class name to load
     * @return the newly loaded class
     */
    public static Class forName(String name) throws ClassNotFoundException {
        ClassLoader ctxLoader = null;
        try {
            ctxLoader = Thread.currentThread().getContextClassLoader();
            return Class.forName(name, true, ctxLoader);

        } catch(ClassNotFoundException ex) {
            // try to fall through and use the default
            // Class.forName
            //if(ctxLoader == null) { throw ex; }
        } catch(SecurityException ex) {
        }
        return Class.forName(name);
    }

    /** A utility for dynamically setting the value of a given static class
     * method */
    public static void dynamicSet(Class klass, String property, String value) {
        try {
            P6Util.set(klass, property, new String[] {value});
        } catch (IllegalAccessException e) {
            P6LogQuery.error("Could not set property "+property+" due to IllegalAccessException");
        } catch (NoSuchMethodException e) {
            // we are avoid this because it is perfectly okay for there to be get methods
            // we do not really want to set
        } catch (InvocationTargetException e) {
            P6LogQuery.error("Could not set property "+property+" due to InvoicationTargetException");
        }
    }

    public static void set(Class klass, String method, Object[] args) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method m = klass.getDeclaredMethod(method, new Class[] {String.class});
        m.invoke(null,args);
    }

    /** A utility for dynamically getting the value of a given static class
     * method */
    public static String dynamicGet(Class klass, String property) {
        try {
            Object value = P6Util.get(klass, property);
            return value == null ? null : value.toString();
        } catch (IllegalAccessException e) {
            P6LogQuery.error("Could not get property "+property+" due to IllegalAccessException");
        } catch (NoSuchMethodException e) {
            P6LogQuery.error("Could not get property "+property+" due to NoSuchMethodException");
        } catch (InvocationTargetException e) {
            P6LogQuery.error("Could not get property "+property+" due to InvoicationTargetException");
        }
        return null;
    }

    public static Object get(Class klass, String method) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method m = klass.getDeclaredMethod(method, null);
        return m.invoke(null);
    }

    public static List<String> findAllMethods(Class<?> klass) {
        List<String> list = new ArrayList<String>();

        Method[] methods = klass.getDeclaredMethods();

        for(int i=0; methods != null && i < methods.length; i++) {
            Method method = methods[i];
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                list.add(methodName);
            }
        }
        return list;
    }

    // method add by jayakumar for JDK1.2 support for URL.getPath()
    public static String getPath(URL theURL) {
     	String file = theURL.getFile();
     	String path = null;
     	if (file != null) {
			int q = file.lastIndexOf('?');
	       	if (q != -1) {
	         path = file.substring(0, q);
			} else {
	       		path = file;
	     	}
   		}
     	return path;
     }
     // end of support method
}
