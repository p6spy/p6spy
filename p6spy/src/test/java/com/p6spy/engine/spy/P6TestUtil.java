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
package com.p6spy.engine.spy;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.test.P6TestOptions;


@Deprecated // T6TestFramework has most if not all of this.
public class P6TestUtil {
  
  private static final Logger LOG = Logger.getLogger(P6TestUtil.class);
//
//  protected P6TestUtil() {
//  }
//
//  protected static Properties loadProperties(String filename) throws IOException {
//    if (filename == null) {
//      throw new IllegalArgumentException("No properties file specified.");
//    }
//
//    Properties props = new Properties();
//
//    InputStream inputStream = P6TestUtil.class.getResourceAsStream(filename);
//    if (inputStream == null) {
//      inputStream = new FileInputStream(filename);
//    }
//    try {
//      props.load(inputStream);
//    } finally {
//      try {
//        inputStream.close();
//      } catch (Exception e) {
//        // so earlier exception is not shadowed.
//      }
//    }
//    return props;
//  }

//  protected static void writeProperty(String filename, Map props) throws IOException {
//    File reload = new File(filename);
//    reload.delete();
//
//    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(reload)));
//
//    Iterator i = props.keySet().iterator();
//    while (i.hasNext()) {
//      String key = (String) i.next();
//      String value = (String) props.get(key);
//      out.println(key + "=" + value);
//    }
//
//    out.close();
//  }
//
//  protected static void writeFile(String filename, ArrayList entries) throws IOException {
//    File file = new File(filename);
//    file.delete();
//
//    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
//
//    for (int i = 0; i < entries.size(); i++) {
//      String entry = (String) entries.get(i);
//      out.println(entry);
//    }
//
//    out.close();
//  }
//
//  protected static Map getDefaultPropertyFile(String p6TestProperties) throws IOException {
//
//    Properties props = loadProperties(p6TestProperties);
//    String realdrivername = props.getProperty("p6realdriver");
//    String realdrivername2 = props.getProperty("p6realdriver2");
//
//    HashMap tp = new HashMap();
//    tp.put("module.outage", "com.p6spy.engine.outage.P6OutageFactory");
//    tp.put("module.log", "com.p6spy.engine.logging.P6LogFactory");
//    tp.put("realdriver", realdrivername);
//    tp.put("realdriver2", realdrivername2);
//    tp.put("filter", "false");
//    tp.put("include", "");
//    tp.put("exclude", "");
//    tp.put("trace", "true");
//    tp.put("autoflush", "true");
//    tp.put("logfile", "spy.log");
//    tp.put("append", "true");
//    tp.put("dateformat", "");
//    tp.put("includecategories", "");
//    tp.put("excludecategories", "debug,result,batch");
//    tp.put("stringmatcher", "");
//    tp.put("stacktrace", "false");
//    tp.put("stacktraceclass", "");
//    tp.put("reloadproperties", "false");
//    tp.put("reloadpropertiesinterval", "1");
//    tp.put("useprefix", "false");
//    tp.put("outagedetection", "false");
//    tp.put("outagedetectioninterval", "");
//    tp.put("entries", "");
//    tp.put("forms", "");
//    tp.put("formsfile", "testspy.forms");
//    tp.put("formslog", "testforms.log");
//    tp.put("formstrace", "true");
//    tp.put("deregisterdrivers", "true");
//    return tp;
//  }

//  protected static void reloadProperty(Map props) throws IOException {
//    writeProperty(/*"target/test-classes/com/p6spy/engine/spy/" + */P6TestFramework.PROPERTY_FILE_PATH, props);
//
//    P6SpyProperties properties = new P6SpyProperties();
//    properties.setSpyProperties(P6TestFramework.PROPERTY_FILE_PATH);
//    OptionReloader.reload();
//  }

  protected static void printAllDrivers() {
    for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
      LOG.info("DRIVER FOUND: " + e.nextElement());
    }
  }

  public static Connection loadDrivers(String drivername)
      throws SQLException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
    String user = P6TestOptions.getActiveInstance().getUser();
    String password = P6TestOptions.getActiveInstance().getPassword();
    String url = P6TestOptions.getActiveInstance().getUrl();

    if (drivername != null) {
      LOG.info("UTIL REGISTERING DRIVER == " + drivername);
      Class<Driver> driverClass = P6Util.forName(drivername);
      DriverManager.setLogWriter(new PrintWriter(System.out, true));
      DriverManager.registerDriver(driverClass.newInstance());
    }
    Driver driver = DriverManager.getDriver(url);
    LOG.info("UTIL USING DRIVER == " + driver.getClass().getName() + " FOR URL " + url);
    Connection connection = DriverManager.getConnection(url, user, password);
    printAllDrivers();
    return connection;
  }


}
