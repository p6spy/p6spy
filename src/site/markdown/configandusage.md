# Configuration and Usage

## <a name="settings">Common Property File Settings</a>

An example spy.properties file follows:

    #################################################################
    # P6Spy Options File #
    # See documentation for detailed instructions #
    #################################################################

    #################################################################
    # MODULES #
    # #
    # Modules provide the P6Spy functionality. If a module, such #
    # as module_log is commented out, that functionality will not #
    # be available. If it is not commented out (if it is active), #
    # the functionality will be active. #
    # #
    # Values set in Modules cannot be reloaded using the #
    # reloadproperties variable. Once they are loaded, they remain #
    # in memory until the application is restarted. #
    # #
    #################################################################

    module.log=com.p6spy.engine.logging.P6LogFactory
    #module.outage=com.p6spy.engine.outage.P6OutageFactory

    #################################################################
    # REALDRIVER(s) #
    # #
    # In your application server configuration file you replace the #
    # "real driver" name with com.p6spy.engine.P6SpyDriver. This is #
    # where you put the name of your real driver P6Spy can find and #
    # register your real driver to do the database work. #
    # #
    # If your application uses several drivers specify them in #
    # realdriver2, realdriver3. See the documentation for more #
    # details. #
    # #
    # Values set in REALDRIVER(s) cannot be reloaded using the #
    # reloadproperties variable. Once they are loaded, they remain #
    # in memory until the application is restarted. #
    # #
    #################################################################

    # oracle driver
    # realdriver=oracle.jdbc.driver.OracleDriver

    # mysql Connector/J driver
    realdriver=com.mysql.jdbc.Driver

    # informix driver
    # realdriver=com.informix.jdbc.IfxDriver

    # ibm db2 driver
    # realdriver=COM.ibm.db2.jdbc.net.DB2Driver

    # the mysql open source driver
    # realdriver=org.gjt.mm.mysql.Driver

    #specifies another driver to use
    realdriver2=
    #specifies a third driver to use
    realdriver3=

    #the DriverManager class sequentially tries every driver that is
    #registered to find the right driver. In some instances, it's possible to
    #load up the realdriver before the p6spy driver, in which case your connections
    #will not get wrapped as the realdriver will "steal" the connection before
    #p6spy sees it. Set the following property to "true" to cause p6spy to
    #explicitily deregister the realdrivers
    deregisterdrivers=false

    ################################################################
    # P6LOG SPECIFIC PROPERTIES #
    ################################################################
    # no properties currently available

    ################################################################
    # EXECUTION THRESHOLD PROPERTIES #
    ################################################################
    # This feature applies to the standard logging of P6Spy. #
    # While the standard logging logs out every statement #
    # regardless of its execution time, this feature puts a time #
    # condition on that logging. Only statements that have taken #
    # longer than the time specified (in milliseconds) will be #
    # logged. This way it is possible to see only statements that #
    # have exceeded some high water mark. #
    # This time is reloadable. #
    #
    # executionthreshold=integer time (milliseconds)
    #
    executionthreshold=

    ################################################################
    # P6OUTAGE SPECIFIC PROPERTIES #
    ################################################################
    # Outage Detection
    #
    # This feature detects long-running statements that may be indicative of
    # a database outage problem. If this feature is turned on, it will log any
    # statement that surpasses the configurable time boundary during its execution.
    # When this feature is enabled, no other statements are logged except the long
    # running statements. The interval property is the boundary time set in seconds.
    # For example, if this is set to 2, then any statement requiring at least 2
    # seconds will be logged. Note that the same statement will continue to be logged
    # for as long as it executes. So if the interval is set to 2, and the query takes
    # 11 seconds, it will be logged 5 times (at the 2, 4, 6, 8, 10 second intervals).
    #
    # outagedetection=true|false
    # outagedetectioninterval=integer time (seconds)
    #
    outagedetection=false
    outagedetectioninterval=

    ################################################################
    # COMMON PROPERTIES #
    ################################################################

    # comma separated list of tables to include
    include =
    # comma separated list of tables to exclude
    exclude =

    # sql expression to evaluate if using regex
    sqlexpression =

    # filter what is logged
    filter=false

    # turn on tracing
    trace = true
    autoflush = true

    # sets the date format using Java's SimpleDateFormat routine
    dateformat=

    #list of categories to explicitly include
    includecategories=

    #list of categories to exclude: error, info, batch, debug, statement,
    #commit, rollback and result are valid values
    #excludecategories=
    excludecategories=info,debug,result,batch

    #allows you to use a regex engine or your own matching engine to determine
    #which statements to log
    #
    #stringmatcher=com.p6spy.engine.common.GnuRegexMatcher
    #stringmatcher=com.p6spy.engine.common.JakartaRegexMatcher
    stringmatcher=

    # prints a stack trace for every statement logged
    stacktrace=false
    # if stacktrace=true, specifies the stack trace to print
    stacktraceclass=

    # determines if property file should be reloaded
    reloadproperties=false
    # determines how often should be reloaded in seconds
    reloadpropertiesinterval=60

    #if=true then url must be prefixed with p6spy:
    useprefix=false

    #specifies the appender to use for logging
    #appender=com.p6spy.engine.logging.appender.Log4jLogger
    #appender=com.p6spy.engine.logging.appender.StdoutLogger
    appender=com.p6spy.engine.logging.appender.FileLogger

    # name of logfile to use, note Windows users should make sure to use forward slashes in their pathname (e:/test/spy.log) (used for file logger only)
    logfile = spy.log

    # append to the p6spy log file. if this is set to false the
    # log file is truncated every time. (file logger only)
    append=true

    #The following are for log4j logging only
    log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
    log4j.appender.STDOUT.layout=org.apache.log4j.PatternLayout
    log4j.appender.STDOUT.layout.ConversionPattern=p6spy - %m%n

    #log4j.appender.CHAINSAW_CLIENT=org.apache.log4j.net.SocketAppender
    #log4j.appender.CHAINSAW_CLIENT.RemoteHost=localhost
    #log4j.appender.CHAINSAW_CLIENT.Port=4445
    #log4j.appender.CHAINSAW_CLIENT.LocationInfo=true

    log4j.logger.p6spy=INFO,STDOUT

    #################################################################
    # DataSource replacement #
    # #
    # Replace the real DataSource class in your application server #
    # configuration with the name com.p6spy.engine.spy.P6DataSource,#
    # then add the JNDI name and class name of the real #
    # DataSource here #
    # #
    # Values set in this item cannot be reloaded using the #
    # reloadproperties variable. Once it is loaded, it remains #
    # in memory until the application is restarted. #
    # #
    #################################################################
    realdatasource=/RealMySqlDS
    realdatasourceclass=com.mysql.jdbc.jdbc2.optional.MysqlDataSource

    #################################################################
    # DataSource properties #
    # #
    # If you are using the DataSource support to intercept calls #
    # to a DataSource that requires properties for proper setup, #
    # define those properties here. Use name value pairs, separate #
    # the name and value with a semicolon, and separate the #
    # pairs with commas. #
    # #
    # The example shown here is for mysql #
    # #
    #################################################################
    realdatasourceproperties=port;3306,serverName;myhost,databaseName;jbossdb,foo;bar


    #################################################################
    # JNDI DataSource lookup #
    # #
    # If you are using the DataSource support outside of an app #
    # server, you will probably need to define the JNDI Context #
    # environment. #
    # #
    # If the P6Spy code will be executing inside an app server then #
    # do not use these properties, and the DataSource lookup will #
    # use the naming context defined by the app server. #
    # #
    # The two standard elements of the naming environment are #
    # jndicontextfactory and jndicontextproviderurl. If you need #
    # additional elements, use the jndicontextcustom property. #
    # You can define multiple properties in jndicontextcustom, #
    # in name value pairs. Separate the name and value with a #
    # semicolon, and separate the pairs with commas. #
    # #
    # The example shown here is for a standalone program running on #
    # a machine that is also running JBoss, so the JDNI context #
    # is configured for JBoss (3.0.4). #
    # #
    #################################################################
    #jndicontextfactory=org.jnp.interfaces.NamingContextFactory
    #jndicontextproviderurl=localhost:1099
    #jndicontextcustom=java.naming.factory.url.pkgs;org.jboss.nameing:org.jnp.interfaces

    #jndicontextfactory=com.ibm.websphere.naming.WsnInitialContextFactory
    #jndicontextproviderurl=iiop://localhost:900


### module.xxx

module.xxx is a particular module loaded at system startup. A module contains a group of functionality. If a
module line is not commented out, it is loaded into memory, and will remain in memory until the application is
restarted. Modules can not be changed by using the reloadproperties function. If all modules are commented out,
then nothing except the wrapped database driver is loaded.

Currently the following modules are supported:

    module.log=com.p6spy.engine.logging.P6LogSpyDriver
    module.outage=com.p6spy.engine.outage.P6OutageSpyDriver

module.log is required for the logging functionality, see [P6Log](#p6log).
module.outage is required for the outage functionality, see [P6Outage](#p6outage).

### realdriver

realdriver is where you specify the wrapped database driver. P6Spy wraps around your existing driver, intercepts the incoming database requests, and outputs them to a log file. To achieve this without requiring any code changes, the P6Spy driver is listed with your application as the primary driver. P6Spy then intercepts and logs the requests, and passes the requests to realdriver, where it is processed as usual. An example follows:

    realdriver = oracle.jdbc.driver.OracleDriver

### realdriver2, realdriver3

If you have multiple database drivers, you need a way to specify these drivers. If you are using the same database driver with multiple connection strings, you only need to specify the driver once. For example, if you have two MySQL databases, mydb and testdb, you want to connect to both databases and log their activity. Specify com.p6spy.engine.spy.P6SpyDriver as the database driver for both of these and set realdriver to the real MySQL JDBC driver name. realdriver2, realdriver3 are only intended for use when you are connecting with two different drivers, for example Oracle versus MySQL.

### deregisterdrivers

The DriverManager class sequentially tries every driver that is registered to find the right driver. In some instances, it's possible to load up the realdriver before the p6spy driver, in which case your connections will not get wrapped as the realdriver will "steal" the connection before p6spy sees it. Set the following property to "true" to cause p6spy to explicitily deregister the realdrivers

### filter, include, exclude

P6Spy allows you to monitor specific tables or specific statement types. By setting filter=true, P6Spy will perform string matching on each statement to determine if it should be written to the log file.  include accepts a comma-delimited list of expressions which is required to appear in a statement before it can appear in the log. exclude accepts a comma-delimited list to exclude. By default, string matching is performed using a basic substring match. However, RegExp matching can also be used (see stringmatcher (Custom Filtering) below). Exclusion overrides inclusion, so that a statement matching both an include string and an exclude string is excluded.

An example showing capture of all select statements, except the orders table follows:

    filter = true
    # comma separated list of tables to include
    include = select
    # comma separated list of tables to exclude
    exclude = orders

An example showing only capture statements against order, order_details, price, and price_history follows:

    filter = true
    # comma separated list of tables to include
    include = order,order_details,price,price_history
    # comma separated list of tables to exclude
    exclude =

An example showing the capture of all statements, except statements against the order table follows:

    filter = false
    # comma separated list of tables to include
    include =
    # comma separated list of tables to exclude
    exclude = order

### filter, sqlexpression

If you plan on using a RegExp engine, a simple alternative to exclude and include is to use sqlexpression. An example follows:

    filter = true
    sqlexpression = your expression

If your expression matches the SQL string, it is logged. If the expression does not match, it is not logged. If you use sqlexpression, any values set in include and exclude are ignored.

### stringmatcher (Custom Filtering)

If you want to apply more intelligence to what is logged or not logged by P6Spy, you can specify a custom matching engine. P6Spy comes with support for several RegExp engines, though it is trivial to introduce another engine by implementing the stringmatcher interface. If a stringmatcher engine is specified, P6Spy will pass each statement to the class specified to determine if that statement should be logged.

* Using GNU RegExp—P6Spy comes with support for a regular expression matcher based on the GNU RegExp library. In order to do this, download the GNU Java RexExp library and add it to your classpath so that P6Spy can find it. The following is an example of the implementation:
        # Use GNU Regex Matching for Filtering
        stringmatcher =com.p6spy.engine.common.GnuRegexMatcher


* Using Jakarta RegExp—P6Spy also includes support for the Apache Jakarta RegExp library. In order to do this, download the Apache Jarkarta RegExp library and add it to your classpath so that P6Spy can find it. The following is an example of the implementation:
        # Use Apache Jakarta Regex Matching for Filtering
        stringmatcher =com.p6spy.engine.common.JakartaRegexMatcher

### autoflush

For standard development, set the autoflush value to true. When set to true, every time a statement is intercepted, it
is immediately written to the log file. In some cases, however, instant feedback on every statement is not a
requirement. In those cases, the system performs slightly faster with autoflush set to false.

An example follows:

    autoflush = true

### appender

Appenders allow you to specify where and how log information is output. Appenders are a flexible architecture
allowing anyone to write their own output class for P6Spy. To use an appender, specify the classname of the
appender to use. The current release comes with three options which are log4j, stdout, and logging to a CSV
text file (default).

* Using a CSV File—To output to a file, uncomment the FileLogger appender and specify a logfile and
  whether or not to append to the file or to clear the file each time:

        #appender=com.p6spy.engine.logging.appender.Log4jLogger
        #appender=com.p6spy.engine.logging.appender.StdoutLogger
        appender=com.p6spy.engine.logging.appender.FileLogger

        # name of logfile to use, note Windows users should make sure to use forward slashes in their pathname(e:/test/spy.log) (used for file logger only)
        logfile = spy.log

        # append to the p6spy log file. if this is set to false the
        # log file is truncated every time. (file logger only)
        append=true

* Using StdOut—Uncomment the StdoutLogger as follows and all output will be sent to stdout in a CSV format:

        #appender=com.p6spy.engine.logging.appender.Log4jLogger
        appender=com.p6spy.engine.logging.appender.StdoutLogger
        #appender=com.p6spy.engine.logging.appender.FileLogger

* Using Log4J—To output to log4j, make sure log4j is in your path, uncomment the log4j appender, and specify the desired log4j settings:

        appender=com.p6spy.engine.logging.appender.Log4jLogger
        #appender=com.p6spy.engine.logging.appender.StdoutLogger
        # appender=com.p6spy.engine.logging.appender.FileLogger

        #The following are for log4j logging only
        log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
        log4j.appender.STDOUT.layout=org.apache.log4j.PatternLayout
        log4j.appender.STDOUT.layout.ConversionPattern=p6spy - %m%n

        #log4j.appender.CHAINSAW_CLIENT=org.apache.log4j.net.SocketAppender
        #log4j.appender.CHAINSAW_CLIENT.RemoteHost=localhost
        #log4j.appender.CHAINSAW_CLIENT.Port=4445
        #log4j.appender.CHAINSAW_CLIENT.LocationInfo=true

        log4j.logger.p6spy=INFO,STDOUT

    If for some reason log4j cannot be initialized, the logging will go to a file called log4jaux.log.

### excludecategories

The log includes category information that describes the type of statement. This property excludes the listed categories. Valid options include the following:

* error includes P6Spy errors. (It is recommended that you include this category.)
* info includes driver startup information and property file information.
* debug is only intended for use when you cannot get your driver to work properly, because it writes everything.
* statement includes Statements, PreparedStatements, and CallableStatements.
* batch includes calls made to the addBatch() JDBC API.
* commit includes calls made to the commit() JDBC API.
* rollback includes calls made to the rollback() JDBC API.
* result includes statements generated by ResultSet.

Enter a comma-delimited list of categories to exclude from your log file. See filter, include, exclude for more details on how this process works.

### includecategories

includecategories includes category information that describes the type of statement. This property is a
comma-delimited list of categories to include. See excludecategories for a valid list of categories.

### dateformat

Setting a value for dateformat changes the date format value printed in the log file. No value prints the current time
in milliseconds, a useful feature for parsing the log. The date format engine is Java's SimpleDateFormat class.
Refer to the SimpleDateFormat class in the JavaDocs for information on setting this value. An example follows:

    dateformat=MM-dd-yy HH:mm:ss:SS

### stacktrace

If stacktrace is set, the log prints out the stack trace for each SQL statement logged.

### stacktraceclass

Limits the stack traces printed to those that contain the value set in stacktraceclass. For example, specifying stacktraceclass=com.mycompany.myclass limits the printing of stack traces to the specified class value. The stack trace is converted to a String and string.indexOf(stacktraceclass) is performed.

### reloadproperties and reloadpropertiesinterval

If reloadproperties is set to true, the property file is reloaded every n seconds, where n is defined by the value set by reloadpropertiesinterval. For example, if reloadproperties=true and reloadpropertiesinterval=10, the system checks the File.lastModified() property of the property file every 10 seconds, and if the file has been modified, it will be reloaded.

If you set append=true, the log will be suddenly truncated when you change your properties. This is because using reloadproperties is intended to be the equivalent of restarting your application server. Restarting your application server truncates your log file.

reloadproperties will not reload any driver information (such as realdriver, realdriver2, and realdriver3) and will not change the modules that are in memory.

### useprefix

Setting useprefix to true requires you to prefix your URLs with p6spy:. The default setting is false.

## Command Line Options

Every parameter specified in the property file can be set and overriden at the command line using the Java -D flag.

An example follows:

    java -Dp6logfile=my.log -Dp6trace=true

In addition, you can set the default directory to look for spy.properties, as shown in the following example:

    java -Dp6.home=c:\orion\lib

## Log File Format

The log file format of spy.log follows:

    current time|execution time|category|statement SQL String|effective SQL string

* current time—The current time is obtained through System.getCurrentTimeMillis() and represents the number of milliseconds that have passed since January 1, 1970 00:00:00.000 GMT. (Refer to the J2SE documentation for further details on System.getCurrentTimeMillis().) To change the format, use the dateformat property described in [Common Property File Settings](#settings).
* execution time—The time it takes for a particular method to execute. (This is not the total cost for the SQL statement.) For example, a statement SELECT * FROM MYTABLE WHERE THISCOL = ? might be executed as a prepared statement, in which the .execute() function will be measured. This is recorded as the statement category. Further, as you call .next() on the ResultSet, each .next() call is recorded in the result category.
* category—You can manage your log by including and excluding categories, which is described in [Common Property File Settings](#settings).
* statement SQL string—This is the SQL string passed to the statement object. If it is a prepared statement, it is the prepared statement that existed prior to the parameters being set. To see the complete statement, refer to effective SQL string.
* effective SQL string—If you are not using a prepared statement, this contains no value. Otherwise, it fills in the values of the Prepared Statement so you can see the effective SQL statement that is passed to the database. Of course, the database still sees the prepared statement, but this string is a convenient way to see the actual values being sent to the database.

## The JSP Application

P6Spy includes a JSP application. Use this application to view P6Spy configuration information and to create a demarcation in the log file. To use the JSP application, complete the following steps:

1. Copy p6spy.war into the deployment directory of your application server. In JBoss, for example, the directory might be C:\JBoss-2.4.4_Tomcat-4.0.1\jboss\deploy.
1. Once p6spy.war is deployed, access the application by navigating to http://machine:port/p6spy. For example, if you are running the application on your own machine, and using Tomcat as the servlet engine, navigate to http://localhost:8080/p6spy.

## The JBoss JMX Application

P6Spy includes a JMX application, tested with JBoss 2.4.x, that allows the P6Spy configuration to be managed via JMX. To use this

1. In spy.properties set reloadproperties=true
1. Open JBoss.jcml and insert the following after the "JMX Adaptors" section:

        <mbean code="com.p6spy.management.jboss.P6SpyManager" name=":service=P6SpyManager"/>

1. Access the application by using the default JMX port (http://localhost:8082/) and clicking on "service=P6SpyManager".

## Building the Source

To build the source, complete the following steps:

1. Download Jakarta Ant.
1. Install Jakarta Ant.
1, You must also download some required libraries. Running Ant the first time will display a message listing all required libraries and locations where they can be downloaded.
1, Copy these libraries into the lib directory, which is a subdirectory of your main directory (the directory with the source code).

The following are useful Ant targets:

* ant creates a p6spy.jar file in the dist directory.
* ant clean cleans the directory of build files and tool-generated backup files.
* ant release creates the Javadocs, the .war file, and all distribution .zip and .jar files.
* ant test runs the standard JUnit tests. Refer to the JUnit test instructions below.
* ant perform runs the performance specific JUnit tests.

To run the JUnit tests, complete the following steps:

1. Download JUnit.
1. Install JUnit
1. Edit the P6Test.properties file and specify two databases. The configuration is set up for Oracle and MySQL. You must change the Oracle URL, at minimum.
1. Copy the vendor database JDBC drivers' JAR files (of the two databases) into the lib directory.

## P6Spy Modules

P6Spy consists of two modules that provide various types of functionality which can be modified to suit your needs. These modules, P6Log and P6Outage, are explained in this section of the documentation. Though they have distinct functions, they share some [Common Property File Settings](#settings) that allow you to specify which tables to log, the log file name, the log file location, whether to show the stacktrace (where the JDBC statement is being executed), and more. Refer to the [Common Property File Settings](#settings) documentation for details.

### <a name="p6log">P6Log</a>

P6Log is an open-source application included in the P6Spy distribution that intercepts and logs the database statements of any application that uses JDBC. This application monitors the SQL statements produced by EJB servers, enabling developers to write code that achieves maximum efficiency on the server. The P6Log module is enabled by default. Disable or enable the P6Log module by editing the spy.properties configuration file. If the module is commented out, it is not loaded, and the functionality is not available. If the module is not commented out, the functionality is available. The applicable portion of the spy.properties file follows:

    #################################################################
    # MODULES
    #
    # Modules provide the P6Spy functionality. If a module, such
    # as module_log is commented out, that functionality will not
    # be available. If it is not commented out (if it is active),
    # the functionality will be active.
    #
    # Values set in Modules cannot be reloaded using the
    # reloadproperties variable. Once they are loaded, they remain
    # in memory until the application is restarted.
    #
    #################################################################
    module.log=com.p6spy.engine.logging.P6LogSpyDriver

    #module.outage=com.p6spy.engine.outage.P6OutageSpyDriver


### <a name="p6outage">P6Outage</a>

P6Outage is an open-source application included in the P6Spy distribution. P6Outage minimizes any logging performance overhead by logging only long-running statements. The P6Outage module is disabled by default. Disable or enable the P6Outage module by editing the spy.properties configuration file. If the module is commented out, it is not loaded, and the functionality is not available. If the module is not commented out, the functionality is available.

The applicable portion of the spy.properties file follows:

    #################################################################
    # MODULES
    #
    # Modules provide the P6Spy functionality. If a module, such
    # as module_log is commented out, that functionality will not
    # be available. If it is not commented out (if it is active),
    # the functionality will be active.
    #
    # Values set in Modules cannot be reloaded using the
    # reloadproperties variable. Once they are loaded, they remain
    # in memory until the application is restarted.
    #
    #################################################################
    #module.log=com.p6spy.engine.logging.P6LogSpyDriver

    module.outage=com.p6spy.engine.outage.P6OutageSpyDriver


The following are P6Outage-specific properties:

* outagedetection - This feature detects long-running statements that may be indicative of a database outage problem. When enabled, it logs any statement that surpasses the configurable time boundary during its execution. No other statements are logged except the long-running statements.


* outagedetectioninterval - The interval property is the boundary time set in seconds. For example, if set to 2, any statement requiring at least 2 seconds is logged. The same statement will continue to be logged for as long as it executes. So, if the interval is set to 2 and a query takes 11 seconds, it is logged 5 times (at the 2, 4, 6, 8, 10-second intervals).





