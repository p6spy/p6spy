# Configuration and Usage

Configuration follows **layered approach**, where **each layer overrides the values set by the lower ones**
(leaving those not provided unchanged):

* JMX set properties (please note, that these are reset on next reload)
* System properties
* Environment variables
* spy.properties
* defaults

For the full list of available options, see the section [Common Property File Settings](#common-property-file-settings).
Please note that providing any of these via System properties/Environment variables is possible, using the particular
property name following naming rule: `p6spy.config.<property name><property value>`;

Please be aware of the restriction. In fact this also means you need to be aware of values set by the lower
configuration layers (including defaults) to properly override/modify those.

There are **two cases one needs to distinguish when overriding**:

* don't override the property on the current level (can be achieved by specifying neither key nor value) and
* clear the property value (can be achieved by specifying the key empty string value, could be for specified in `spy.properties` like this: `excludecategories=`)

The `spy.properties` configuration file can be located in various places.  The following locations are searched
to locate the file.

1. The file name configured in the system property "spy.properties" (can include path)
1. The current working directory (for relative path) or any directory (for absolute path)
1. The classpath

## Properties exposal via JMX

Please note that all the properties are exposed via JMX. So you can use your tool of choice (e.g.,JConsole) to view/change them.
Moreover reload operation is exposed as well. To provide on-demand reload option.

In the JConsole p6spy related JMX attributes might look like this:

![JConsole](/img/jconsole.png)

## Command Line Options

Every parameter specified in the property file can be set and overriden at the command line using the Java -D flag (system property), adding the the prefix:

    p6spy.config.

An example follows:

    java -Dp6spy.config.logfile=my.log -Dp6spy.config.append=true

Moreover to set different file to be used as the properties file (as an example: another_spy.properties), it should be specified using system property "spy.properties" as:

    java -Dspy.properties=c:\jboss\lib\another_spy.properties

## Common Property File Settings

An example `spy.properties` file follows (please note default values mentioned as these refer to defaults mentioned
in section: [Configuration and Usage](#configuration-and-usage)):

    #################################################################
    # P6Spy Options File                                            #
    # See documentation for detailed instructions                   #
    # http://p6spy.github.io/p6spy/2.0/configandusage.html          #
    #################################################################

    #################################################################
    # MODULES                                                       #
    #                                                               #
    # Module list adapts the modular functionality of P6Spy.        #
    # Only modules listed are active.                               #
    # (default is com.p6spy.engine.logging.P6LogFactory and         #
    # com.p6spy.engine.spy.P6SpyFactory)                            #
    # Please note that the core module (P6SpyFactory) can't be      #
    # deactivated.                                                  #
    # Unlike the other properties, activation of the changes on     #
    # this one requires reload.                                     #
    #################################################################
    #modulelist=com.p6spy.engine.spy.P6SpyFactory,com.p6spy.engine.logging.P6LogFactory,com.p6spy.engine.outage.P6OutageFactory

    ################################################################
    # CORE (P6SPY) PROPERTIES                                      #
    ################################################################

    # A comma separated list of JDBC drivers to load and register.
    # (default is empty)
    #
    # Note: This is normally only needed when using P6Spy in an
    # application server environment with a JNDI data source or when
    # using a JDBC driver that does not implement the JDBC 4.0 API
    # (specifically automatic registration).
    #driverlist=

    # for flushing per statement
    # (default is false)
    #autoflush=false

    # sets the date format using Java's SimpleDateFormat routine.
    # In case property is not set, milliseconds since 1.1.1970 (unix time) is used (default is empty)
    #dateformat=

    # prints a stack trace for every statement logged
    #stacktrace=false
    # if stacktrace=true, specifies the stack trace to print
    #stacktraceclass=

    # determines if property file should be reloaded
    # Please note: reload means forgetting all the previously set
    # settings (even those set during runtime - via JMX)
    # and starting with the clean table
    # (default is false)
    #reloadproperties=false

    # determines how often should be reloaded in seconds
    # (default is 60)
    #reloadpropertiesinterval=60

    # specifies the appender to use for logging
    # Please note: reload means forgetting all the previously set
    # settings (even those set during runtime - via JMX)
    # and starting with the clean table
    # (only the properties read from the configuration file)
    # (default is com.p6spy.engine.spy.appender.FileLogger)
    #appender=com.p6spy.engine.spy.appender.Slf4JLogger
    #appender=com.p6spy.engine.spy.appender.StdoutLogger
    #appender=com.p6spy.engine.spy.appender.FileLogger

    # name of logfile to use, note Windows users should make sure to use forward slashes in their pathname (e:/test/spy.log)
    # (used for com.p6spy.engine.spy.appender.FileLogger only)
    # (default is spy.log)
    #logfile=spy.log

    # append to the p6spy log file. if this is set to false the
    # log file is truncated every time. (file logger only)
    # (default is true)
    #append=true

    # class to use for formatting log messages (default is: com.p6spy.engine.spy.appender.SingleLineFormat)
    #logMessageFormat=com.p6spy.engine.spy.appender.SingleLineFormat

    # Custom log message format used ONLY IF logMessageFormat is set to com.p6spy.engine.spy.appender.CustomLineFormat
    # default is %(currentTime)|%(executionTime)|%(category)|connection%(connectionId)|%(sqlSingleLine)
    # Available placeholders are:
    #   %(connectionId)            the id of the connection
    #   %(currentTime)             the current time expressing in milliseconds
    #   %(executionTime)           the time in milliseconds that the operation took to complete
    #   %(category)                the category of the operation
    #   %(effectiveSql)            the SQL statement as submitted to the driver
    #   %(effectiveSqlSingleLine)  the SQL statement as submitted to the driver, with all new lines removed
    #   %(sql)                     the SQL statement with all bind variables replaced with actual values
    #   %(sqlSingleLine)           the SQL statement with all bind variables replaced with actual values, with all new lines removed
    #customLogMessageFormat=%(currentTime)|%(executionTime)|%(category)|connection%(connectionId)|%(sqlSingleLine)

    # format that is used for logging of the date/time/... (has to be compatible with java.text.SimpleDateFormat)
    # (default is dd-MMM-yy)
    #databaseDialectDateFormat=dd-MMM-yy
    
    # format that is used for logging booleans, possible values: boolean, numeric
    # (default is boolean)
    #databaseDialectBooleanFormat=boolean

    # whether to expose options via JMX or not
    # (default is true)
    #jmx=true

    # if exposing options via jmx (see option: jmx), what should be the prefix used?
    # jmx naming pattern constructed is: com.p6spy(.<jmxPrefix>)?:name=<optionsClassName>
    # please note, if there is already such a name in use it would be unregistered first (the last registered wins)
    # (default is none)
    #jmxPrefix=

    # if set to true, the execution time will be measured in nanoseconds as opposed to milliseconds
    # (default is false)
    #useNanoTime=false

    #################################################################
    # DataSource replacement                                        #
    #                                                               #
    # Replace the real DataSource class in your application server  #
    # configuration with the name com.p6spy.engine.spy.P6DataSource #
    # (that provides also connection pooling and xa support).       #
    # then add the JNDI name and class name of the real             #
    # DataSource here                                               #
    #                                                               #
    # Values set in this item cannot be reloaded using the          #
    # reloadproperties variable. Once it is loaded, it remains      #
    # in memory until the application is restarted.                 #
    #                                                               #
    #################################################################
    #realdatasource=/RealMySqlDS
    #realdatasourceclass=com.mysql.jdbc.jdbc2.optional.MysqlDataSource

    #################################################################
    # DataSource properties                                         #
    #                                                               #
    # If you are using the DataSource support to intercept calls    #
    # to a DataSource that requires properties for proper setup,    #
    # define those properties here. Use name value pairs, separate  #
    # the name and value with a semicolon, and separate the         #
    # pairs with commas.                                            #
    #                                                               #
    # The example shown here is for mysql                           #
    #                                                               #
    #################################################################
    #realdatasourceproperties=port;3306,serverName;myhost,databaseName;jbossdb,foo;bar

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
    # The two standard elements of the naming environment are       #
    # jndicontextfactory and jndicontextproviderurl. If you need    #
    # additional elements, use the jndicontextcustom property.      #
    # You can define multiple properties in jndicontextcustom,      #
    # in name value pairs. Separate the name and value with a       #
    # semicolon, and separate the pairs with commas.                #
    #                                                               #
    # The example shown here is for a standalone program running on #
    # a machine that is also running JBoss, so the JNDI context     #
    # is configured for JBoss (3.0.4).                              #
    #                                                               #
    # (by default all these are empty)                              #
    #################################################################
    #jndicontextfactory=org.jnp.interfaces.NamingContextFactory
    #jndicontextproviderurl=localhost:1099
    #jndicontextcustom=java.naming.factory.url.pkgs;org.jboss.naming:org.jnp.interfaces

    #jndicontextfactory=com.ibm.websphere.naming.WsnInitialContextFactory
    #jndicontextproviderurl=iiop://localhost:900

    ################################################################
    # P6 LOGGING SPECIFIC PROPERTIES                               #
    ################################################################

    # filter what is logged
    # please note this is a precondition for usage of: include/exclude/sqlexpression
    # (default is false)
    #filter=false

    # comma separated list of strings to include
    # please note that special characters escaping (used in java) has to be done for the provided regular expression
    # (default is empty)
    #include=
    # comma separated list of strings to exclude
    # (default is empty)
    #exclude=

    # sql expression to evaluate if using regex
    # please note that special characters escaping (used in java) has to be done for the provided regular expression
    # (default is empty)
    #sqlexpression=

    #list of categories to exclude: error, info, batch, debug, statement,
    #commit, rollback, result and resultset are valid values
    # (default is info,debug,result,resultset,batch)
    #excludecategories=info,debug,result,resultset,batch

    #whether the binary values (passed to DB or retrieved ones) should be logged with placeholder: [binary] or not.
    # (default is false)
    #excludebinary=false

    # Execution threshold applies to the standard logging of P6Spy.
    # While the standard logging logs out every statement
    # regardless of its execution time, this feature puts a time
    # condition on that logging. Only statements that have taken
    # longer than the time specified (in milliseconds) will be
    # logged. This way it is possible to see only statements that
    # have exceeded some high water mark.
    # This time is reloadable.
    #
    # executionThreshold=integer time (milliseconds)
    # (default is 0)
    #executionThreshold=

    ################################################################
    # P6 OUTAGE SPECIFIC PROPERTIES                                #
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

### modulelist

modulelist holds the list of p6spy modules activated. A module contains a group of functionality. If none are specified only core
p6spy framework will be activated (no logging,...). Still once reload of the properties happen, or these are set by JMX, modules would be
dynamically loaded/unloaded.

The following modules come with the p6spy by default:


    modulelist=com.p6spy.engine.logging.P6LogFactory,com.p6spy.engine.outage.P6OutageFactory


Where these are required:

* com.p6spy.engine.logging.P6LogFactory - for the logging functionality and
* com.p6spy.engine.outage.P6OutageFactory - for outage functionality.

Please note to implement custom module have a look at the implementation of the any of the existing ones.

### driverlist

This is a comma separated list of JDBC driver classes to load and register with DriverManager. You should list
the classname(s) of the JDBC driver(s) that you want to proxy with P6Spy if any of the following conditions are met.

1. The JDBC driver does not implement the JDBC 4.0 API
1. You are using a JNDI Data Source - Some application servers will prevent the automatic registration feature from working.

### autoflush

For standard development, set the autoflush value to true. When set to true, every time a statement is intercepted, it
is immediately written to the log file. In some cases, however, instant feedback on every statement is not a
requirement. In those cases, the system performs slightly faster with autoflush set to false.

An example follows:

    autoflush=true

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

### appender

Appenders allow you to specify where and how log information is output. Appenders are a flexible architecture
allowing anyone to write their own output class for P6Spy. To use an appender, specify the classname of the
appender to use. The current release comes with three options which are slf4j, stdout,
and logging to a file (default). Please note, that all of these output in the CSV format (where separator is: "|").

* **Using the File output**: Uncomment the `FileLogger` appender and specify a `logfile` and
  whether or not to `append` to the file or to clear the file each time:

        #appender=com.p6spy.engine.spy.appender.Slf4JLogger
        #appender=com.p6spy.engine.spy.appender.StdoutLogger
        appender=com.p6spy.engine.spy.appender.FileLogger

        # name of logfile to use, note Windows users should make sure to use forward slashes in their pathname (e:/test/spy.log)
        # (used for com.p6spy.engine.spy.appender.FileLogger only)
        # (default is spy.log)
        #logfile=spy.log

        # append to the p6spy log file. if this is set to false the
        # log file is truncated every time. (file logger only)
        append=true

* **Using StdOut**: Uncomment the `StdoutLogger` as follows:

        #appender=com.p6spy.engine.spy.appender.Slf4JLogger
        appender=com.p6spy.engine.spy.appender.StdoutLogger
        #appender=com.p6spy.engine.spy.appender.FileLogger

* **Using SLF4J**: Uncomment the `Slf4JLogger` as follows:

        appender=com.p6spy.engine.spy.appender.Slf4JLogger
        #appender=com.p6spy.engine.spy.appender.StdoutLogger
        #appender=com.p6spy.engine.spy.appender.FileLogger

    In general you need to slf4j-api and the appropriate bridge to the actual logging
implementation as well as the logging implementation itself on your classpath. To simplify setup for those not having any of the additional dependencies already
on classpath following `*-nodep.jar` bundles are provided as part of p6spy distribution:

    * `p6spy-<version>-log4j-nodep.jar` - having [log4j](http://logging.apache.org/log4j/1.2/) included,
    * `p6spy-<version>-log4j2-nodep.jar` - having [log4j2](http://logging.apache.org/log4j/2.x/) included and
    * `p6spy-<version>-logback-nodep.jar` - having [logback](http://logback.qos.ch/) included.

    Mapping to SLF4J levels is provided in the following way:

    <table>
    <tr><th>P6Spy category</th><th>SLF4J level</th></tr>
    <tr><td>error</td><td>error</td></tr>
    <tr><td>warn</td><td>warn</td></tr>
    <tr><td>debug</td><td>debug</td></tr>
    <tr><td>info/any other category</td><td>info</td></tr>
    </table>

    Internally is Slf4j Logger is retrieved for the: `p6spy`, keep this in mind when configuring your logging implementation. So for example for the `log4j` following could be used to restrict the p6spy logging (if using xml-based configuration) to `INFO` level only:

          <category name="p6spy">
            <priority value="INFO" />
          </category>

    For further instructions on configuring SLF4J, see the [SLF4J documentation](http://www.slf4j.org/manual.html).

### logMessageFormat

The log message format is selected by specifying the class to use to format the log messages.  The following
classes are available with P6Spy.

* `com.p6spy.engine.spy.appender.SingleLineFormat` which results in log messages in format:

      current time|execution time|category|connection id|statement SQL String|effective SQL string

* `com.p6spy.engine.spy.appender.CustomLineFormat`, which allows log messages to be full customized, in a separate
    property called `customLogMessageFormat`. See below for details.

* `com.p6spy.engine.spy.appender.MultiLineFormat`, which results in log messages in format:

      current time|execution time|category|connection id|statement SQL String
      effective SQL string

Where:

* `current time` - the current time is obtained through System.getCurrentTimeMillis() and represents
  the number of milliseconds that have passed since January 1, 1970 00:00:00.000 GMT.
  (Refer to the J2SE documentation for further details on System.getCurrentTimeMillis().)
  To change the format, use the dateformat property described in
  [Common Property File Settings](#common-property-file-settings).
* `execution time` - the time it takes in milliseconds for a particular method to execute. (This is
  not the total cost for the SQL statement.) For example, a statement
  `SELECT * FROM MYTABLE WHERE THISCOL = ?` might be executed as a prepared
  statement, in which the .execute() function will be measured. This is recorded as
  the statement category. Further, as you call .next() on the ResultSet, each .next()
  call is recorded in the result category.
* `category` - You can manage your log by including and excluding categories,
  which is described in [Common Property File Settings](#common-property-file-settings).
* `connection id` - Indicates the connection on which the activity was logged.  The connection id is a sequentially
  generated identifier.
* `statement SQL string` - This is the SQL string passed to the statement object.
  If it is a prepared statement, it is the prepared statement that existed prior to
  the parameters being set. To see the complete statement, refer to effective SQL string.
* `effective SQL string` - If you are not using a prepared statement, this contains no
  value. Otherwise, it fills in the values of the Prepared Statement so you can see
  the effective SQL statement that is passed to the database. Of course, the database
  still sees the prepared statement, but this string is a convenient way to see the
  actual values being sent to the database.

The `com.p6spy.engine.spy.appender.MultiLineFormat` might be better from a readability perspective.  Because it will place the effective SQL statement
on a separate line.  However, the SingleLineFormat might be better if you have a need to parse the log messages.
The default is `com.p6spy.engine.spy.appender.SingleLineFormat` for backward compatibility.

You can also supply your own log message formatter to customize the format.  Simply create a class which implements
the `com.p6spy.engine.spy.appender.MessageFormattingStrategy` interface and place it on the classpath.

### customLogMessageFormat

The custom log message format to use when 'logMessageFormat' is set to `com.p6spy.engine.spy.appender.CustomLineFormat`

The message is build out of the format string, with the all the Java special characters supported (`\n`, `\t` etc)
and the following placeholders being resolved to the appropriate values:

* `%(connectionId)`           the id of the connection
* `%(currentTime)`                    the current time expressing in milliseconds
* `%(executionTime)`          the time in milliseconds that the operation took to complete
* `%(category)`               the category of the operation
* `%(effectiveSql)`           the SQL statement as submitted to the driver
* `%(effectiveSqlSingleLine)` the SQL statement as submitted to the driver, with all new lines removed
* `%(sql)`                    the SQL statement with all bind variables replaced with actual values
* `%(sqlSingleLine)`          the SQL statement with all bind variables replaced with actual values, with all new lines removed

### filter, include, exclude

P6Spy allows you to filter SQL queries by specific strings to be present (`includes` property value) or not present (`excludes` property value).
As a precondition, setting `filter=true` has to be provided.
P6Spy will perform string matching on each statement to determine if it should be written to the log file.
`include` accepts a comma-delimited list of expressions which is required to appear in a statement before it can appear in the log. `exclude` accepts a comma-delimited list to exclude.
Exclusion overrides inclusion, so that a statement matching both an include string and an exclude string is excluded.

Please note that matching mode used in the underlying regex is (achieved via prefix `(?mis)`):

* [multiline](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#MULTILINE),
* [dotall](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#DOTALL) and
* [case insensitive](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#CASE_INSENSITIVE).

An example showing capture of all statements having select, except those having order follow:

    filter=true
    # comma separated list of strings to include
    include=select
    # comma separated list of strings to exclude
    exclude=order

Please note, that internally following regex would be used for particular expression matching: `(?mis)^(?!.*(order).*)(.*(select).*)$`

An example showing only capture statements having any of: order_details, price, and price_history follows:

    filter=true
    # comma separated list of strings to include
    include=order,order_details,price,price_history
    # comma separated list of strings to exclude
    exclude=

Please note, that internally following regex would be used for particular expression matching: `(?mis)^(.*(order|order_details|price|price_history).*)$`

An example showing the capture of all statements, except statements order string in them follows:

    filter=false
    # comma separated list of strings to include
    include=
    # comma separated list of strings to exclude
    exclude=order

Please note, that internally following regex would be used for particular expression matching: `(?mis)^(?!.*(order).*)(.*)$`

As you can use full regex syntax, capture of statements having: pri[cz]e follows:

    filter=true
    # comma separated list of strings to include
    include=pri[cz]e
    # comma separated list of strings to exclude
    exclude=

Please note, that internally following regex would be used for particular expression matching: `(?mis)^(.*(pri[cz]e).*)$`

Moreover, please note, that special characters escaping (used in java) has to be done for the provided regular expression.
As an example, matching for:

    from\scustomers

would mean, that following should be specified (please note doubled backslash):

    filter=true
    include=from\\scustomers

### filter, sqlexpression

If you need more control over regular expression for matching, SQL string property `sqlexpression` is to be used as an alternative to `exclude` and `include`.
An example follows:

    filter=true
    sqlexpression=your expression

If your expression matches the SQL string, it is logged. If the expression does not match, it is not logged.
Please note you can use `sqlexpression` together with `include`/`exclude`, where both would be evaluated.

Moreover, please note, that special characters escaping (used in java) has to be done for the provided regular expression.
As an example, matching for:

    ^(.*(from\scustomers).*)$

would mean, that following should be specified (please note doubled backslash)::

    filter=true
    sqlexpression=^(.*(from\\scustomers).*)$

### excludecategories

The log includes category information that describes the type of statement. This property excludes the listed categories. Valid options include the following:

* `error` includes P6Spy errors. (It is recommended that you include this category.)
* `info` includes driver startup information and property file information.
* `debug` is only intended for use when you cannot get your driver to work properly, because it writes everything.
* `statement` includes Statements, PreparedStatements, and CallableStatements.
* `batch` includes calls made to the addBatch() JDBC API.
* `commit` includes calls made to the commit() JDBC API.
* `rollback` includes calls made to the rollback() JDBC API.
* `outage` includes outage related information.
* `result` includes statements generated by ResultSet.
* `resultset` includes values retrieve from the ResultSet.

Enter a comma-delimited list of categories to exclude from your log file. See filter, include, exclude for more details on how this process works.

### excludebinary

whether the binary values (passed to DB or retrieved ones) should be logged with placeholder: [binary] or not.

### outagedetection

This feature detects long-running statements that may be indicative of a database outage
problem. When enabled, it logs any statement that surpasses the configurable time boundary during its execution.
No other statements are logged except the long-running statements.

### outagedetectioninterval

The interval property is the boundary time set in seconds. For example, if set to
2, any statement requiring at least 2 seconds is logged. The same statement will continue to be logged for as
long as it executes. So, if the interval is set to 2 and a query takes 11 seconds, it is logged 5 times (at
the 2, 4, 6, 8, 10-second intervals).

### jmxPrefix

If set to true, the execution time will be measured in nanoseconds as opposed to milliseconds.
