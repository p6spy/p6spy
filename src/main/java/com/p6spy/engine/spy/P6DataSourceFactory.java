
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


  protected static final String DATASOURCE_CLASS_NAME = P6DataSource.class.getName();

  public Object getObjectInstance(Object refObj,
                                  Name nm,
                                  Context ctx,
                                  Hashtable env) throws Exception {

    final Reference ref = (Reference) refObj;
    final String className = ref.getClassName();

    if (className != null && className.equals(DATASOURCE_CLASS_NAME)) {

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
