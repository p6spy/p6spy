# P6Spy Installation

This section will document the steps to install P6Spy on various application servers.  In additional, it 
contains [Generic Instructions](#generic-instructions) for applications servers not listed as well as applications that do not use
an application server.  If you create instructions for other application servers, 
[send us a copy](http://p6spy.github.io/p6spy/mail-lists.html) for possible publication in the documentation.

The instructions for all application servers make the following assumptions.

1. The operating system is \*nix. For Windows installations, the steps are the same but the syntax will be 
   a little different (environment variables, path separators, etc).

1. MySQL is the database being used. If you are using a different database, just substitute the JDBC connection URL and driver class 
   appropriate for your database.
   
1. Database connections are being obtained from a JNDI DataSource configured on the application server.
   If the application does not use a JNDI DataSource, the instructions for modifying the data source
   configuration will be incorrect.  You will need to use the instructions as guidelines for modifying
   the application specific database configuration. The instructions for adding the p6spy jar file and 
   spy.properties will still be correct.
   
1. You have already downloaded the [P6Spy distribution](https://github.com/p6spy/p6spy/wiki/Download) and 
   extracted the contents to a temporary directory. Throughout the rest of the instructions, the files included 
   in this temporary directory will be referenced by name only.  
   
1. Your application is running on Java 1.6 or later.  For earlier versions of Java, you will need to 
   use [P6Spy 1.3](../1.3/install.html)
   
After you have completed the installation, a log file called **spy.log** will be created in the current working
directory when the application runs.  This log file will contain a list of the various database statements 
executed.  You can alter the location of this log file as well as what gets logged by editing **spy.properties**.  See
[Common Property File Settings](configandusage.html#settings) for the various configuration options available.

Application Servers:
    
* [JBoss/WildFly](#jboss-wildfly)    
* [Apache Tomcat and Apache TomEE](#apache-tomcat-and-apache-tomee)    
* [Glassfish and Payara](#glassfish-and-payara)    
* [Weblogic](#weblogic)
* [Generic Instructions](#generic-instructions)    

## JBoss/WildFly

The following sections contain specific information on installing P6Spy on [JBoss 4.2.x, 5.1.x, 6.1.x and JBoss 5.x EAP](#jboss-4-2-x-5-1-x-6-1-x-and-jboss-5-x-eap) and [JBoss 7.1.x, WildFly 8.x](#jboss-7-1-x-wildfly-8-x)

Please note **XA Datasource proxying IS NOT supported** for these. 

### JBoss 4.2.x, 5.1.x, 6.1.x and JBoss 5.x EAP

The following instructions were tested with JBoss 4.2.3.GA, 5.1.0.GA, 6.1.0.Final and JBoss 5.2.0 EAP. For these instructions,
P6Spy assumes that you are using the default server residing in `$JBOSS_DIST\server\default`, where $JBOSS_DIST
is the directory in which JBoss is installed.

1. Move the **p6spy.jar** file to the `$JBOSS_DIST\server\default\lib` directory.
1. Move the **spy.properties** file to the `$JBOSS_DIST\server\default\conf` directory.
1. Update the connection URL and driver class for your data source in `$JBOSS_DIST\server\default\deploy`.  This file 
   is normally called `?????-ds.xml`.   An example of the pertinent portions (not the complete XML file) follows:

        <jndi-name>MySqlDS</jndi-name>
        <connection-url>jdbc:p6spy:mysql://<hostname>:<port>/<database></connection-url>
        <driver-class>com.p6spy.engine.spy.P6SpyDriver</driver-class>

### JBoss 7.1.x, WildFly 8.x, WildFly 10.x
The following instructions were tested with JBoss 7.1.0, WildFly 8.1.Final and WildFly 10.0.0.Final (works with p6spy version 2.1.0 or higher). For these instructions,
P6Spy assumes that you are using the standalone and $JBOSS_DIST is the directory in which JBoss/WildFly is installed. 

1. Deploy **p6spy.jar** as a module:
	* via moving it to to the `$JBOSS_DIST\modules\system\layers\base\com\p6spy\main` (for Wildfly) or to `$JBOSS_DIST\modules\com\p6spy\main` (for JBoss 7.1) directory
	* and via providing `module.xml` in the same directory with the contents: 
		
        <module xmlns="urn:jboss:module:1.0" name="com.p6spy">
		    <resources>
		        <resource-root path="p6spy-2.0.3.jar"/>
		    </resources>
		    <dependencies>
		        <module name="javax.api"/>
		        <module name="javax.transaction.api"/>
		        <!-- make sure to refer to module holding real driver -->
		        <module name="com.h2database.h2"/>
		    </dependencies>
		</module>
		
	please note, that p6spy-2.0.3 version jar is used in the sample configuration. Moreover the reference to module holding the real (proxied) jdbc driver has to be provided (in the sample case is h2 one used). 

1. Move the **spy.properties** file to the `$JBOSS_DIST\bin` directory.
1. Update the connection URL and driver section in your `<datasources>` in `$JBOSS_DIST\standalone\configuration\standalone.xml`. An example of the pertinent portions (not the complete XML file) follows:

        <datasources>
	        <datasource jndi-name="java:/jdbc/p6spy" enabled="true" use-java-context="true" pool-name="p6spyPool">
				<connection-url>jdbc:p6spy:h2:tcp://<hostname>:<port>/<database></connection-url>
	            <driver>p6spy</driver>
				...
	        </datasource>

			<drivers>
		        <driver name="p6spy" module="com.p6spy">
	                <driver-class>com.p6spy.engine.spy.P6SpyDriver</driver-class>
		        </driver>
		        <!-- make sure that you also include the real driver -->
		        <driver name="h2" module="com.h2database.h2">
                <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
            </driver>
            </drivers>
        </datasources>

	Please note that you have to include both drivers in the corresponding section, the real driver (here the H2 driver) and the p6spy driver,
	although only the p6spy driver is referenced after the connection URL.

## Apache Tomcat and Apache TomEE

The following sections contain specific information on installing P6Spy on [Tomcat 6.x, 7.x, 8.x and TomEE 1.6.x](#apache-tomcat-6-x-7-x-8-x-and-tomee-1-6-x).

### Apache Tomcat 6.x, 7.x, 8.x and TomEE 1.6.x

The following instructions were tested with Apache Tomcat versions: 6.0.34, 7.0.54 and 8.0.15 as well as Apache TomEE 1.6.0.2 Webprofile and Apache TomEE 1.6.0.2 Plus. For these instructions, it is assumed that $CATALINA_HOME
refers to the tomcat/tomee installation directory.  Please be aware that there are many ways to configure JNDI data sources
on tomcat/tomee.

1. Move the **p6spy.jar** file to the lib directory. An example of the path to your
   lib directory is `$CATALINA_HOME\lib\`.
1. Move the **spy.properties** file to the lib directory. An example of the path to your
   lib directory is `$CATALINA_HOME\lib\`.
1. Configure the class name of the real JDBC driver in **spy.properties**
   
        driverlist=com.mysql.jdbc.Driver
           
1. Modify the JDBC connection URL and driver class for the data source.  Please be aware that there are several places
   where a JNDI data source may be defined. It is normally defined in a `<Resource/>` element in 
   `$CATALINA_BASE/conf/server.xml` or in the application specific `$CATALINA_BASE/conf/catalina/localhost/????.xml`.   
   See [Tomcat 6 JNDI Resources](http://tomcat.apache.org/tomcat-6.0-doc/jndi-resources-howto.html#JDBC_Data_Sources)/[Tomcat 7 JNDI Resources](http://tomcat.apache.org/tomcat-7.0-doc/jndi-resources-howto.html#JDBC_Data_Sources)/[Tomcat 8 JNDI Resources](http://tomcat.apache.org/tomcat-8.0-doc/jndi-datasource-examples-howto.html)
   for specifics of where a data source is configured.  An example of the pertinent portions of the resource definition
   are shown below.
   
        <Resource name="jdbc/mydb"
                   type="javax.sql.DataSource"
                   ...
                   driverClassName="com.p6spy.engine.spy.P6SpyDriver"
                   url="jdbc:p6spy:mysql://<hostname>:<port>/<database>"
                   ...
                   />

## Glassfish and Payara

The following section contains specific information on installing P6Spy on [Glassfish 3.1.2.2, 4.0 and Payara 4.1.144](#glassfish-3-1-2-2-4-0-and-payara-4-1-144) (works with p6spy version 2.1.0 or higher).

Please note **XA Datasource proxying IS supported** for these. 

### Glassfish 3.1.2.2, 4.0 and Payara 4.1.144

The provided instructions were tested with Glassfish OSE 3.1.2.2, Glassfish OSE 4.0 and Payara 4.1.144. 
In later section is `$GLASSFISH_HOME` the directory where Glassfish/Payara is installed and `$DOMAIN_X` is the domain name used for deployment (for example, can be: `domain1`).

1. Move the **p6spy.jar** file to the `$GLASSFISH_HOME/domains/$DOMAIN_X/lib/ext` directory.
1. Move the **spy.properties** file to the `$GLASSFISH_HOME/domains/$DOMAIN_X/config` directory.
1. Configure new datasource. Please note there are 3 configuration options available:

	* updating JDBC Url if using `java.sql.Driver`
	
	using command line:
			
	        # create jdbc connection pool			
	        asadmin create-jdbc-connection-pool --driverclassname=com.p6spy.engine.spy.P6SpyDriver --restype=java.sql.Driver --property=URL='jdbc:p6spy:h2:tcp://<hostname>:<port>/<database>':User='<username>':Password='<password>' p6spyPool
	        		        			
	        # ping the pool to prove it works (optionally)
	        asadmin ping-connection-pool p6spyPool
	        
	        # create jdbc resource
	        asadmin --user=<asadmin_user> --passwordfile=<sample_passworfile.properties> create-jdbc-resource --connectionpoolid=p6spyPool jdbc/p6Spy

	or directly by editing `$GLASSFISH_HOME/domains/$DOMAIN_X/config/domain.xml` (please note the previous commands would lead to similar added to your config file):
			
	        <jdbc-connection-pool driver-classname="com.p6spy.engine.spy.P6SpyDriver" res-type="java.sql.Driver" name="p6spyPool">
	        	<property name="URL" value="jdbc:p6spy:h2:tcp://<hostname>:<port>/<database>"></property>
	      		<property name="Password" value=""></property>
		    	<property name="User" value="sa"></property>
		    </jdbc-connection-pool>
		    <jdbc-resource pool-name="p6spyPool" jndi-name="jdbc/p6spy"></jdbc-resource>
				
	* `javax.sql.ConnectionPoolDataSource` proxying (via additional datasource)
	
	using command line:
			
			# create jdbc connection pool
			asadmin create-jdbc-connection-pool --datasourceclassname=com.p6spy.engine.spy.P6DataSource --restype=javax.sql.ConnectionPoolDataSource --property=realDataSource='<realDSJndi>':User='<username>':Password='<password>' p6spyPool
			
			# ping the pool to prove it works (optionally)
			asadmin ping-connection-pool p6spyPool
			
			# create jdbc resource			
			asadmin --user=<asadmin_user> --passwordfile=<sample_passworfile.properties> create-jdbc-resource --connectionpoolid=p6spyPool jdbc/p6Spy

	or directly by editing `$GLASSFISH_HOME/domains/$DOMAIN_X/config/domain.xml` (please note the previous commands would lead to similar added to your config file):
	
			<jdbc-connection-pool datasource-classname="com.p6spy.engine.spy.P6DataSource" res-type="javax.sql.ConnectionPoolDataSource" name="p6spyPool">
		      <property name="realDataSource" value="jdbc/<realDSJndi>"></property>
		      <property name="Password" value=""></property>
		      <property name="User" value="sa"></property>
		    </jdbc-connection-pool>
			<jdbc-resource pool-name="p6spyPool" jndi-name="jdbc/p6spy"></jdbc-resource>		
			
	* `javax.sql.XADataSource` proxying (via additional datasource)
	
	using command line:
			
			# create jdbc connection pool
			asadmin create-jdbc-connection-pool --datasourceclassname=com.p6spy.engine.spy.P6DataSource --restype=javax.sql.XADataSource --property=realDataSource='<realDSJndi>':User='<username>':Password='<password>' p6spyPool
			
			# ping the pool to prove it works (optionally)
			asadmin ping-connection-pool p6spyPool
			
			# create jdbc resource			
			asadmin --user=<asadmin_user> --passwordfile=<sample_passworfile.properties> create-jdbc-resource --connectionpoolid=p6spyPool jdbc/p6Spy

	or directly by editing `$GLASSFISH_HOME/domains/$DOMAIN_X/config/domain.xml` (please note the previous commands would lead to similar added to your config file):
	
			<jdbc-connection-pool datasource-classname="com.p6spy.engine.spy.P6DataSource" res-type="javax.sql.XADataSource" name="p6spyPool">
		      <property name="realDataSource" value="jdbc/<realDSJndi>"></property>
		      <property name="Password" value=""></property>
		      <property name="User" value="sa"></property>
		    </jdbc-connection-pool>
			<jdbc-resource pool-name="p6spyPool" jndi-name="jdbc/p6spy"></jdbc-resource>		

	Please note, you need to replace following:
	
	* `<asadmin_user>` - asadmin user name (by default `asadmin`)
	* `<sample_passworfile.properties>` - is a properties file, that should hold your asadmin password (by default should hold: `AS_ADMIN_ADMINPASSWOD=adminadmin`)
	* `<username>` - username to be used for the DB access
	* `<password>` - password to be used for DB access
	* `<hostname>` - DB server hostname
	* `<port>` - DB server port
	* `<database>` - DB server database 
	* `<realDSJndi>` - jndi-name of the real datasource to be proxied
		
	And the jndi name of the created jndi resource in the sample configurations is: `jdbc/p6spy`

## Weblogic

The following section contains specific information on installing P6Spy on [Weblogic 12.1.3](#weblogic-12-1-3) (works with p6spy version 2.1.0 or higher).

### Weblogic 12.1.3

The provided instructions were tested with Weblogic 12.1.3 (for developers). 
In later section is `$WLS_HOME` the directory where Weblogic is installed and `$DOMAIN_X` is the domain name used for deployment (for example, can be: `mydomain`).

1. Move the **p6spy.jar** file to the `$WLS_HOME/user_projects/domains/$DOMAIN_X/lib` directory.
1. Move the **spy.properties** file to the `$WLS_HOME/user_projects/domains/$DOMAIN_X` directory.
1. Update JDBC URL in the datasource to something like:

        jdbc:p6spy:mysql://<hostname>:<port>/<database>

1. Change driver in the datasource to:

        com.p6spy.engine.spy.P6SpyDriver


## Generic Instructions

The following installation instructions are intended for use with other application servers and
applications that do not use application servers. To install P6Spy, complete the following steps:

1. Put the **p6spy.jar** file in your classpath.
1. Put **spy.properties** into a directory which is on the classpath.  Many application servers have a directory for
   configuration files which are accessible via the classpath.  Most applications which do not run an application
   server will have one as well.
1. Configure the class name of the real JDBC driver in **spy.properties**
   
        driverlist=com.mysql.jdbc.Driver
           
1. Configure the data source.

If the JNDI DataSource is configured using a driver class (implements javax.sql.Driver), then you should modify the 
JDBC connection URL to include 'p6spy:' and update the driver class to `com.p6spy.engine.spy.P6SpyDriver`.  Example 
URL including p6spy: `jdbc:p6spy:mysql://<hostname>:<port>/<database>`.

If the JNDI DataSource is configured using a data source class (implements javax.sql.DataSource) then you will need
to create a 'proxy' data source using the following instructions.  
 
1. Rename the JNDI name of the current data source to something else.  For example, if the current name is 'jdbc/myds', 
then change it to 'jdbc/myds-real'.
1. Create a new JNDI DataSource with the original name of the real data source.  Continuing with the example from 
above, the JNDI name should be 'jdbc/myds'.
1. Create one property for the data source called 'RealDataSource'.  The value of this property should be 'jdbc/myds-real'
1. Set the class or implementation to `com.p6spy.engine.spy.P6DataSource`.
1. If the application server requires a classpath for the datasource, it should include p6spy.jar and spy.properties.  

