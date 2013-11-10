# <a name="confusage">Configuration and Usage</a>

Configuration follows layered approach, where each layer overrides the values set by the lower ones (leaving those not provided unchanged):

* JMX set properties (please note, that these are reset on next reload)
* System properties
* Environment variables
* spy.properties
* defaults

For the full list of available options, see the section [Common Property File Settings](#settings). 
Please note that providing any of these via System properties/Environment variables is possible, using the particular property name following naming rule: p6spy.config.&lt;property name&gt;=&lt;property value&gt;

To enable full overriding capabilities, all those options considering lists (comma separated) values follow the rules:

* "-"&lt;property value&gt; - causes removal of particular value from the list
* &lt;property value&gt; - causes adding of particular value to the list

please be aware of the restriction. In fact this also means you need to be aware of values set by the lower configuration layers (including defaults) to properly override.modify those.

## <a name="settings">Common Property File Settings</a>

An example spy.properties file follows (please note default values mentioned as these reffer to defaults mentioned in section: [Configuration and Usage](#confusage)):

    #################################################################
    # P6Spy Options File                                            #
    # See documentation for detailed instructions                   #
    #################################################################

    #################################################################
	# MODULES                                                       #
	#                                                               #
	# Modulelist addapts the modular functionality of P6Spy.		#
	# Only modules listed are active 								#
	# Please note that the core module (P6SpyFactory) can't be		# 
	# deactivated											        #
	#################################################################
	#modulelist=com.p6spy.engine.logging.P6LogFactory,com.p6spy.engine.outage.P6OutageFactory,com.p6spy.engine.leak.P6LeakFactory

    ################################################################
    # P6LOG SPECIFIC PROPERTIES #
    ################################################################
    # no properties currently available

    ################################################################
    # P6LEAK SPECIFIC PROPERTIES #
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
    # (default is 0)
    #executionthreshold=

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
	# (default is false)
	#outagedetection=false
	# (default is 60)
	#outagedetectioninterval=30
	
    ################################################################
    # COMMON PROPERTIES #
    ################################################################

    # A comma separated list of JDBC drivers to load and register.
    # This is rarely needed!  Only use this when you the driver is not
    # getting loaded automatically.
    # (default is empty)
    #driverlist=

    # comma separated list of tables to include
    # (default is empty)
    #include =
    # comma separated list of tables to exclude
    # (default is empty)
    #exclude =

    # sql expression to evaluate if using regex
    # (default is empty)
	#sqlexpression = 

    # filter what is logged
	# (default is false)
	#filter=false

	# for flushing per statement
	# (default is false)
	#autoflush = false

    # sets the date format using Java's SimpleDateFormat routine. 
	# In case property is not set, miliseconds since 1.1.1970 (unix time) is used (default is empty)
	#dateformat=

    #list of categories to exclude: error, info, batch, debug, statement,
    #commit, rollback and result are valid values
    # (default is info,debug,result,resultset,batch)
    #excludecategories=info,debug,result,resultset,batch

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
    # Please note: reload means forgetting all the previously set
    # settings (even those set during runtime - via JMX)
    # and starting with the clean table 
	# (default is false)
	#reloadproperties=false
	# determines how often should be reloaded in seconds
	# (default is 60)
	#reloadpropertiesinterval=60

    #if=true then url must be prefixed with p6spy:
    useprefix=false

    #specifies the appender to use for logging
	# Please note: reload means forgetting all the previously set
	# settings (even those set during runtime - via JMX)
	# and starting with the clean table 
	# (only the properties read from the configuration file)
    # (default is com.p6spy.engine.spy.appender.FileLogger)
	#appender=com.p6spy.engine.spy.appender.Log4jLogger
	#appender=com.p6spy.engine.spy.appender.StdoutLogger
	#appender=com.p6spy.engine.spy.appender.FileLogger

    # name of logfile to use, note Windows users should make sure to use forward slashes in their pathname (e:/test/spy.log) (used for file logger only)
    # (default is spy.log)
    #logfile = spy.log

    # append to the p6spy log file. if this is set to false the
    # log file is truncated every time. (file logger only)
    append=true

    # class to use for formatting log messages (default is: com.p6spy.engine.spy.appender.SingleLineFormat)
    #logMessageFormat=com.p6spy.engine.spy.appender.SingleLineFormat

    #The following are for log4j logging only
    #Please note: The existing configuration is not cleared nor reset. It's rather iterative approach here
    #once you require different behavior, provide your own log4j configuration file (holding these properties) 
    #and make sure to load/reload it properly. 
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
	# JNDI DataSource lookup                                        #
	#                                                               #
	# If you are using the DataSource support outside of an app     #
	# server, you will probably need to define the JNDI Context     #
	# environment.                                                  #
	#                                                               #
	# If the P6Spy code will be executing inside an app server then #
	# do not use these properties, and the DataSource lookup will   #
	# use the naming context defined by the app server.             #
	#                                                               #
	# The two standard elements of the naming environment are	    #
	# jndicontextfactory and jndicontextproviderurl. If you need    #
	# additional elements, use the jndicontextcustom property.      #
	# You can define multiple properties in jndicontextcustom,      #
	# in name value pairs. Separate the name and value with a       #
	# semicolon, and separate the pairs with commas.                #
	#                                                               #
	# The example shown here is for a standalone program running on #
	# a machine that is also running JBoss, so the JDNI context     #
	# is configured for JBoss (3.0.4).                              #
	#                                                               #
	# (by default all these are empty)                              #
	#################################################################
    #jndicontextfactory=org.jnp.interfaces.NamingContextFactory
    #jndicontextproviderurl=localhost:1099
    #jndicontextcustom=java.naming.factory.url.pkgs;org.jboss.nameing:org.jnp.interfaces

    #jndicontextfactory=com.ibm.websphere.naming.WsnInitialContextFactory
    #jndicontextproviderurl=iiop://localhost:900


### modulelist

modulelist holds the list of p6spy modules activated. A module contains a group of functionality. If none are specified only core
p6spy framework will be activated (no logging,...). Still once reload of the properties happen, or these are set by JMX, modules would be
dynamically loaded/unloaded.

The following modules come with the p6spy by default:


    modulelist=com.p6spy.engine.logging.P6LogFactory,com.p6spy.engine.outage.P6OutageFactory,com.p6spy.engine.leak.P6LeakFactory


Where these are required:
 - com.p6spy.engine.logging.P6LogFactory - for the logging functionality, see [P6Log](#p6log).
 - com.p6spy.engine.outage.P6OutageFactory - for outage functionality, see [P6Outage](#p6outage).
 - com.p6spy.engine.leak.P6LeakFactory - for and leak functionality, see [P6Leak](#p6leak).

Please note to implement custom module have a look at the imlpementation of the any of the existing ones.

### driverlist

This is a comma separated list of JDBC driver classes to load and register with the DriverManager.  This is rarely used
since type 4 drivers are automatically loaded and registered.  Only use this option when needed.

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

        #appender=com.p6spy.engine.spy.appender.Log4jLogger
        #appender=com.p6spy.engine.spy.appender.StdoutLogger
        appender=com.p6spy.engine.spy.appender.FileLogger

        # name of logfile to use, note Windows users should make sure to use forward slashes in their pathname(e:/test/spy.log) (used for file logger only)
        logfile = spy.log

        # append to the p6spy log file. if this is set to false the
        # log file is truncated every time. (file logger only)
        append=true

* Using StdOut—Uncomment the StdoutLogger as follows and all output will be sent to stdout in a CSV format:

        #appender=com.p6spy.engine.spy.appender.Log4jLogger
        appender=com.p6spy.engine.spy.appender.StdoutLogger
        #appender=com.p6spy.engine.spy.appender.FileLogger

* Using Log4J—To output to log4j, make sure log4j is in your path, uncomment the log4j appender, and specify the desired log4j settings:

        appender=com.p6spy.engine.spy.appender.Log4jLogger
        #appender=com.p6spy.engine.spy.appender.StdoutLogger
        # appender=com.p6spy.engine.spy.appender.FileLogger

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

### dateformat

Setting a value for dateformat changes the date format value printed in the log file. No value prints the current time
in milliseconds (unix time), a useful feature for parsing the log. The date format engine is Java's SimpleDateFormat class.
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

### logMessageFormat

The log message format is selected by specifying the class to use to format the log messages.  The following
classes are available with P6Spy.

`com.p6spy.engine.spy.appender.SingleLineFormat`

`com.p6spy.engine.spy.appender.MultiLineFormat`

The MultiLineFormat might be better from a readability perspective.  Because it will place the effective SQL statement
on a separate line.  However, the SingleLineFormat might be better if you have a need to parse the log messages.
The default is SingleLineFormat for backward compatibility.

You can also supply your own log message formatter to customize the format.  Simply create a class which implements
the `com.p6spy.engine.spy.appender.MessageFormattingStrategy` interface and place it on the classpath.

## Command Line Options

Every parameter specified in the property file can be set and overriden at the command line using the Java -D flag.

An example follows:

    java -Dp6logfile=my.log -Dp6trace=true

In addition, you can set the default directory to look for spy.properties, as shown in the following example:

    java -Dp6.home=c:\orion\lib

## Log File Format

The log file format of spy.log follows:

  SingleLineFormat -

    current time|execution time|category|statement SQL String|effective SQL string

  MultiLineFormat -

    current time|execution time|category|statement SQL String
    effective SQL string

* current time—The current time is obtained through System.getCurrentTimeMillis() and represents
  the number of milliseconds that have passed since January 1, 1970 00:00:00.000 GMT.
  (Refer to the J2SE documentation for further details on System.getCurrentTimeMillis().)
  To change the format, use the dateformat property described in
  [Common Property File Settings](#settings).
* execution time—The time it takes for a particular method to execute. (This is
  not the total cost for the SQL statement.) For example, a statement
  `SELECT * FROM MYTABLE WHERE THISCOL = ?` might be executed as a prepared
  statement, in which the .execute() function will be measured. This is recorded as
  the statement category. Further, as you call .next() on the ResultSet, each .next()
  call is recorded in the result category.
* category—You can manage your log by including and excluding categories,
  which is described in [Common Property File Settings](#settings).
* statement SQL string—This is the SQL string passed to the statement object.
  If it is a prepared statement, it is the prepared statement that existed prior to
  the parameters being set. To see the complete statement, refer to effective SQL string.
* effective SQL string—If you are not using a prepared statement, this contains no
  value. Otherwise, it fills in the values of the Prepared Statement so you can see
  the effective SQL statement that is passed to the database. Of course, the database
  still sees the prepared statement, but this string is a convenient way to see the
  actual values being sent to the database.

## The JSP Application

P6Spy includes a JSP application. Use this application to view P6Spy configuration information and
to create a demarcation in the log file. To use the JSP application, complete the following steps:

1. Copy **p6spy-webcontrol.war** into the deployment directory of your
  application server. In JBoss, for example, the directory might be `C:\JBoss\server\web\deploy`.
1. Once **p6spy.war** is deployed, access the application by navigating
  to `http://machine:port/p6spy-webcontrol`.  For example, if you are running the application on your
  own machine, and using Tomcat as the servlet engine, navigate to
  `http://localhost:8080/p6spy-webcontrol`.

## The JBoss JMX Application

P6Spy includes a JMX application, tested with JBoss 2.4.x, that allows the P6Spy configuration to be managed via JMX. To use this

1. In spy.properties set reloadproperties=true
1. Open JBoss.jcml and insert the following after the "JMX Adaptors" section:

        <mbean code="com.p6spy.management.jboss.P6SpyManager" name=":service=P6SpyManager"/>

1. Access the application by using the default JMX port (http://localhost:8082/) and clicking on "service=P6SpyManager".

## Building the Source

1. Make sure to have Java 1.7 or later installed.
1. Download and install [Apache Maven](http://maven.apache.org) 3.0.4 or later.

The following are useful Maven commands:

to build binaries:

	mvn clean install

 to build the site:

    mvn site

 to run the JUnit tests Refer to the [Running the tests](#tests) section

 
## <a name="tests">Running the tests</a>

To run the JUnit tests against specific database(s):

1. Make sure to have Java installed.
1. Download and install [Apache Maven](http://maven.apache.org).
1. Please note, that PostgreSQL, MySQL and Firebird specific tests require to have the detabase servers running with the specific databases, users and permissions setup (see: [Integration tests-like environment with Vagrant](#vagrant) section).

By default, tests run against H2 database. To enable other databases, make sure to setup environment variable DB to one of the:

  * PostgreSQL
  * MySQL
  * H2 
  * HSQLDB
  * SQLite
  * Firebird
  * Derby
  * or comma separated list of these


### Running the tests in the command line

use the following maven command:

	mvn clean test -DDB=<DB_NAMES>

where &lt;DB_NAMES&gt; would hold the value of `DB` environment variable described before.

### Running the tests in the Eclipse

1. Make sure to have [m2e plugin](http://eclipse.org/m2e/) installed 
1. Import all the p6spy projects to eclipse (as Maven projects)
1. Right click the Class holding the test to run and choose: Run As -> JUnit Test

The `DB` environment variable can be set using Arguments tab -&gt; VM Argument of the JUnit Run Configuration.

### <a name="vagrant">Integration tests-like environment with Vagrant</a>

It might be tricky to run full batery of tests on developer machine (especially due to need of DB servers setup).
To make things easier, [Vagrant] (http://www.vagrantup.com/) is used to create environment close to the one running on our integration test servers ([travis-ci] (https://travis-ci.org/)).

To have tests running please follow these steps:

1. Install [Vagrant] (http://www.vagrantup.com/) in your environment with Virtualbox as provider
1. Install Vagrant plugins we use:

        vagrant plugin install vagrant-omnibus
        vagrant plugin install vagrant-berkshelf
        vagrant plugin install vagrant-cachier

1. To remotely debug the integration tests on your local machine run following:

        vagrant up
        vagrant ssh
        cd /vagrant
        mvn clean test -P travis -Dmaven.surefire.debug
  		
1. Use your favorite java IDE to remotelly debug the tests run.

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

* outagedetection - This feature detects long-running statements that may be indicative of a database outage
problem. When enabled, it logs any statement that surpasses the configurable time boundary during its execution.
No other statements are logged except the long-running statements.


* outagedetectioninterval - The interval property is the boundary time set in seconds. For example, if set to
2, any statement requiring at least 2 seconds is logged. The same statement will continue to be logged for as
long as it executes. So, if the interval is set to 2 and a query takes 11 seconds, it is logged 5 times (at
the 2, 4, 6, 8, 10-second intervals).

### <a name="p6leak">P6Leak</a>

P6Leak helps you to detect any JDBC resources which have not been properly closed.  This includes connections,
statements, and result sets.

Usage:

1. Uncomment the leak module in spy.properties.
1. Ensure that P6Spy is configured as per the [installation instructions](install.html)
1. Copy JDBCLeak.jsp (from distribution) to the root of your web application.
1. Start application and exercise it thoroughly for a while.
1. View JDBCLeak.jsp to view a stack trace for any JDBC leaks.
1. Halt the application and remove the leaks.
1. Repeat until no more leaks are detected.

The P6Leak module is disabled by default. Disable or enable the P6Outage module by editing the spy.properties
configuration file. If the module is commented out, it is not loaded, and the functionality is not available. If
the module is not commented out, the functionality is available.

The applicable portion of the spy.properties file follows:

    module.leak=com.p6spy.engine.leak.P6LeakFactory



