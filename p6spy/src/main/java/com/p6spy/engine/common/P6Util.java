/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
