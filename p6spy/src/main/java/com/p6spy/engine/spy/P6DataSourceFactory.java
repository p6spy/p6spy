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
	    
	    P6DataSource dataSource = null;
	    
	    try {
		dataSource = (P6DataSource)Class.forName(className).newInstance();
	    } catch (Exception ex) {
		throw new RuntimeException("Unable to create DataSource of " +
					   "class '" + className + "': " + ex.toString());
	    }
	    // name of the real datasource
	    dataSource.setRealDataSource((String) ref.get("dataSourceName").getContent());
	    
	    return dataSource;
	} else	{ 
	    // Who's class is this anyway, I ask ya!
	    return null;
	}
    }
}
