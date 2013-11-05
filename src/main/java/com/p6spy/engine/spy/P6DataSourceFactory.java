package com.p6spy.engine.spy;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

/**
 * Factory class for P6DataSource objects
 */

public class P6DataSourceFactory implements ObjectFactory {


  protected final String dataSourceClassName =
      "com.p6spy.engine.spy.P6DataSource";

  protected final String poolDataSourceName =
      "com.p6spy.engine.spy.P6ConnectionPoolDataSource";


  public Object getObjectInstance(Object refObj,
                                  Name nm,
                                  Context ctx,
                                  Hashtable env) throws Exception {

    Reference ref = (Reference) refObj;

    String className = ref.getClassName();

    if (className != null &&
        (className.equals(dataSourceClassName) ||
            className.equals(poolDataSourceName))) {

      P6DataSource dataSource;

      try {
        dataSource = (P6DataSource) Class.forName(className).newInstance();
      } catch (Exception ex) {
        throw new RuntimeException("Unable to create DataSource of " +
            "class '" + className + "': " + ex.toString());
      }
      // name of the real datasource
      dataSource.setRealDataSource((String) ref.get("dataSourceName").getContent());

      return dataSource;
    } else {
      // Who's class is this anyway, I ask ya!
      return null;
    }
  }
}
