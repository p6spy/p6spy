# P6Spy Installation

This section will document the steps to install P6Spy on various application servers.  In additional, it 
contains generic instructions for applications servers not listed as well as applications that do not use
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

## JBoss

The following sections contain specific information on installing P6Spy on [JBoss 5.x](#jboss5.x)

### <a name="jboss5.x">JBoss 5.x</a>

The following instructions were tested with JBoss 5.2.0 EAP. For these instructions,
P6Spy assumes that you are using the default server residing in `$JBOSS_DIST\server\default`, where $JBOSS_DIST
is the directory in which JBoss is installed. 

1. Move the **p6spy.jar** file to the `$JBOSS_DIST\server\default\lib` directory.
1. Move the **spy.properties** file to the `$JBOSS_DIST\server\default\conf` directory.
1. Update the connection URL and driver class for your data source in `$JBOSS_DIST\server\default\deploy`.  This file 
   is normally called `?????-ds.xml'.   An example of the pertinent portions (not the complete XML file) follows:

        <jndi-name>MySqlDS</jndi-name>
        <connection-url>jdbc:p6spy:mysql://<hostname>:<port>/<database></connection-url>
        <driver-class>com.p6spy.engine.spy.P6SpyDriver</driver-class>

## Apache Tomcat 6.x

The following instructions were tested with Apache Tomcat 6.0.30.  For these instructions, it is assumed that $CATALINA_HOME
refers to the tomcat installation directory.  Please be aware that there are many ways to configure JNDI data sources
on tomcat.

1. Move the **p6spy.jar** file to the lib directory. An example of the path to your
   lib directory is `$CATALINA_HOME\lib\`.
1. Move the **spy.properties** file to the conf directory. An example of the path to your
   conf directory is `$CATALINA_HOME\conf\`.
1. Modify the JDBC connection URL and driver class for the data source.  Please be aware that there are several places
   where a JNDI data source may be defined. It is normally defined in a `<Resource/>` element in 
   `$CATALINA_BASE/conf/server.xml` or in the application specific `$CATALINA_BASE/conf/catalina/localhost/????.xml`.   
   See [Tomcat JNDI Resources](http://tomcat.apache.org/tomcat-6.0-doc/jndi-resources-howto.html#JDBC_Data_Sources)
   for specifics of where a data source is configured.  An example of the pertinent portions of the resource definition
   are shown below.
   
        <Resource name="jdbc/mydb"
                   type="javax.sql.DataSource"
                   ...
                   driverClassName="com.p6spy.engine.spy.P6SpyDriver"
                   url="jdbc:p6spy:mysql://<hostname>:<port>/<database>"
                   ...
                   />
   
## Apache Tomcat 7.x

The following instructions were tested with Apache Tomcat 7.0.47.  For these instructions, it is assumed that $CATALINA_HOME
refers to the tomcat installation directory.  Please be aware that there are many ways to configure JNDI data sources
on tomcat.

1. Move the **p6spy.jar** file to the lib directory. An example of the path to your
   lib directory is `$CATALINA_HOME\lib\`.
1. Move the **spy.properties** file to the conf directory. An example of the path to your
   conf directory is `$CATALINA_HOME\conf\`.
1. Configure the class name of the real JDBC driver in spy.properties
   
       driverlist=com.mysql.jdbc.Driver
           
1. Modify the JDBC connection URL and driver class for the data source.  Please be aware that there are several places
   where a JNDI data source may be defined. It is normally defined in a `<Resource/>` element in 
   `$CATALINA_BASE/conf/server.xml` or in the application specific `$CATALINA_BASE/conf/catalina/localhost/????.xml`.   
   See [Tomcat JNDI Resources](http://tomcat.apache.org/tomcat-7.0-doc/jndi-resources-howto.html#JDBC_Data_Sources)
   for specifics of where a data source is configured.  An example of the pertinent portions of the resource definition
   are shown below.
   
        <Resource name="jdbc/mydb"
                   type="javax.sql.DataSource"
                   ...
                   driverClassName="com.p6spy.engine.spy.P6SpyDriver"
                   url="jdbc:p6spy:mysql://<hostname>:<port>/<database>"
                   ...
                   />
   
## Apache Tomcat 8.x

The following instructions were tested with Apache Tomcat 8.0.0-RC5.  For these instructions, it is assumed that $CATALINA_HOME
refers to the tomcat installation directory.  Please be aware that there are many ways to configure JNDI data sources
on tomcat.

1. Move the **p6spy.jar** file to the lib directory. An example of the path to your
   lib directory is `$CATALINA_HOME\lib\`.
1. Move the **spy.properties** file to the conf directory. An example of the path to your
   conf directory is `$CATALINA_HOME\conf\`.
1. Configure the class name of the real JDBC driver in spy.properties
   
       driverlist=com.mysql.jdbc.Driver
           
1. Modify the JDBC connection URL and driver class for the data source.  Please be aware that there are several places
   where a JNDI data source may be defined. It is normally defined in a `<Resource/>` element in 
   `$CATALINA_BASE/conf/server.xml` or in the application specific `$CATALINA_BASE/conf/catalina/localhost/????.xml`.   
   See [Tomcat JNDI Resources](http://tomcat.apache.org/tomcat-8.0-doc/jndi-datasource-examples-howto.html)
   for specifics of where a data source is configured.  An example of the pertinent portions of the resource definition
   are shown below.
   
        <Resource name="jdbc/mydb"
                   type="javax.sql.DataSource"
                   ...
                   driverClassName="com.p6spy.engine.spy.P6SpyDriver"
                   url="jdbc:p6spy:mysql://<hostname>:<port>/<database>"
                   ...
                   />
   

## <a name="unspec">Generic Instructions</a>

The following installation instructions are intended for use with application servers not listed below and
applications that do not use application servers. To install P6Spy, complete the following steps:

1. Put the **p6spy.jar** file in your classpath.
1. Put **spy.properties** into a directory which is on the classpath.  Many application servers have a directory for
   configuration files which are accessible via the classpath.  Most applications which do not run an application
   server will have one as well.
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




