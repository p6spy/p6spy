/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
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
package com.p6spy.engine.common;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

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

    public static boolean isTrue(String s, boolean defaultValue) {
        if (s == null) {
            return defaultValue;
        }
        return(s.equals("1") || s.trim().equalsIgnoreCase("true"));
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

    
    public static Map<String, String> getPropertiesMap(Properties properties) {
      if (null == properties) {
        return null;
      }
      
      return new HashMap<String, String>((Map) properties);
    }
    
    public static List<String> parseCSVList(String csv) {
      if (csv == null || csv.isEmpty()) {
        return null;
      }
      
      return new ArrayList<String>(Arrays.asList(csv.split(",")));
    }

    public static Properties getProperties(Map<String, String> map) {
      if (map == null) {
        return null;
      }
      
      final Properties properties = new Properties();
      properties.putAll(map);
      return properties;
    }
}

