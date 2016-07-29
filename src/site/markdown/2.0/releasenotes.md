# Release Notes

## Version 2.3.1 [June 23, 2016]

Defects resolved:

* [issue #325](https://github.com/p6spy/p6spy/pull/325): CGLIB lock contention/deadlock

Other:

* [issue #326](https://github.com/p6spy/p6spy/issues/326): Upgraded to CGLIB 3.2.3
* [issue #329](https://github.com/p6spy/p6spy/issues/329): Use default naming policy for CGLIB generated proxies

## Version 2.3.0 [May 11, 2016]

Improvements:

* [issue #295](https://github.com/p6spy/p6spy/issues/295): Add option to report execution time in nanoseconds or milliseconds
* [issue #313](https://github.com/p6spy/p6spy/issues/313): Remove throws SQLException declaration on P6Core.wrapConnection

Defects resolved:

* [issue #320](https://github.com/p6spy/p6spy/pull/320): Calls to getResultSet() not instrumented on Statement and PreparedStatement
* [issue #318](https://github.com/p6spy/p6spy/pull/318): Remove unused Cache abstraction and make cache thread safe
* [issue #317](https://github.com/p6spy/p6spy/pull/317): Make connectionId thread safe
* [issue #314](https://github.com/p6spy/p6spy/pull/314): ClassNotFoundException loading MySQL Statement interface using OSGi

Other:

* [issue #304](https://github.com/p6spy/p6spy/issues/304): Build failures using openjdk7
* [issue #300](https://github.com/p6spy/p6spy/issues/300): Maven warning about missing dependency

## Version 2.2.0 [March 22, 2016]

Improvements:

* [issue #290](https://github.com/p6spy/p6spy/issues/290): Lazy initialization for FileLogger

Defects resolved:

* [issue #330](https://github.com/p6spy/p6spy/issues/330): Unsafe iteration over System.getProperties() 

## Version 2.1.4 [May 9, 2015]

Defects resolved:

* [issue #286](https://github.com/p6spy/p6spy/issues/286): P6Spy proxy creation fails when JDBC object is wrapped by JBoss 7+
* [issue #282](https://github.com/p6spy/p6spy/issues/282): No resultset logged when executing a stored procedure 

## Version 2.1.3 [Jan 24, 2015]

Defects resolved:

* [issue #275](https://github.com/p6spy/p6spy/issues/275): ArrayIndexOutOfBoundsException when calling 
  PreparedStatement.setMaxRows(int) with outage module enabled 

## Version 2.1.2 [Oct 13, 2014]

Defects resolved:

* [issue #268] (https://github.com/p6spy/p6spy/issues/268): SingleLineFormat updated to remove CR and LF characters 
  from the log file 
* [issue #267] (https://github.com/p6spy/p6spy/issues/267): The equals(Object) method on all proxied objects now 
  unwraps the argument passed in (if it is a p6spy proxy) before invoking the method on the proxied object.  This 
  fixes a problem with c3p0 and statement caching.
* [issue #264] (https://github.com/p6spy/p6spy/issues/264): Fixed a defect causing the last row read of a result set 
  to not be logged unless all rows were read.

## Version 2.1.1 [Sep 9, 2014]

Defects resolved:

* [issue #256] (https://github.com/p6spy/p6spy/issues/256): jmx exposing becomes optional (enabled/disabled via flag) + jmx prefix introduced (see )
* [issue #254] (https://github.com/p6spy/p6spy/issues/254): resultset logging filtering fixed 

## Version 2.1.0 [Jun 15, 2014]

Improvements:

* P6ConnectionPoolDataSource merged to P6DataSource (to simplify datasource config)
* `excludecategories` using class `Category` rather than just plain strings (affects `P6Logger` API) 
* [issue #131](https://github.com/p6spy/p6spy/issues/131): providing additional distribution artifacts - wrapping (slf4j bridged) logging implementations for log4j, log4j2 and logback `p6spy-<version>-*-nodep.jar`
* [issue #231](https://github.com/p6spy/p6spy/issues/231): `include`/`exclude` behavior enabling any substring in SQL string matching
* considering Wrapper for DataSource proxies (bringing support for Glassfish XADataSources)
* `unSet*` API provided for properties (in `com.p6spy.engine.spy.P6SpyOptions` and `com.p6spy.engine.logging.P6LogOptions`) to enable reverting to `null` (default value) 
* [issue #247](https://github.com/p6spy/p6spy/issues/247): `-` prefixed syntax for list-like properties deprecated, in favor of full overriding

Defects resolved: 

* [issue #221] (https://github.com/p6spy/p6spy/issues/221): Bind variables set by name on a CallableStatement are now logged
* [issue #226](https://github.com/p6spy/p6spy/issues/226): `setAppender()` considered in logging properly 
* [issue #227] (https://github.com/p6spy/p6spy/issues/227): fixed disabling modules on reload
* [issue #242](https://github.com/p6spy/p6spy/issues/242): character `'` escaping in the logged SQL query fixed 
* [issue #246](https://github.com/p6spy/p6spy/issues/246): `NullPointerException` fixed for empty batch execution

## Version 2.0.2 [Apr 3, 2014]

Improvements:

* [issue #84] (https://github.com/p6spy/p6spy/issues/84): significant performance improvements for huge data selects

Defects resolved: 

* [issue #214] (https://github.com/p6spy/p6spy/issues/214): fixed PostgreSQL issue: `operator is not unique: date + unknown` 
* [issue #219](https://github.com/p6spy/p6spy/issues/219): fixed defect causing ClassCastException when setting bind variables by name on CallableStatement
* [issue #217](https://github.com/p6spy/p6spy/issues/217): fixed defect in P6Leak module causing closed connections not to be recorded properly

## Version 2.0.1 [Mar 15, 2014]

Defects resolved: 

* [issue #200] (https://github.com/p6spy/p6spy/issues/200): fixed usage with signed jdbc jars
* [issue #201] (https://github.com/p6spy/p6spy/issues/201): internal logs not printed out any more

## Version 2.0.0 [Mar 3, 2014]

Improvements:

* project hosting was moved from [sourceforge](http://sourceforge.net/projects/p6spy/) to [github](https://github.com/p6spy/p6spy)
* major part of the legacy code was refactored
* Java 6/7 JDBC API support introduced,
* proxying via modified `JDBC` `URL`s only was implemented, so for for MySQL original url would be (without a need for any further configuration):

    ```
    jdbc:mysql://<hostname>:<port>/<database>
    ```
        
    the one proxied via p6spy would one:
    
    ```
    jdbc:p6spy:mysql://<hostname>:<port>/<database>
    ```
  
 * XA Datasource support has been introduced
 * configuration via:
     * system/environment properties and
     * JMX properties
     * as an alternative to file configuration only
     * or even zero config use case supported
 * slf4j support (more flexible as previously used log4j)
 * junit tests were migrated to junit 4
 * Continuous integration using Travis was setup providing testing on popular:
     * DB systems (namely: Oracle, DB2, PostgreSQL, MySQL, H2, HSQLDB, SQLite, Firebird, and Derby), see build status on: [travis-ci](https://travis-ci.org/p6spy/p6spy) as well as 
     * application servers (namely: Wildfly 8, JBoss 4.2, 5.1, 6.1, 7.1, Glassfish 3.1, 4.0, Jetty 7.6, 8.1, 9.1, Tomcat 6, 7, 8, Resin 4, Jonas 5.3 and Geronimo 2.1, 2.2), see build status on: [travis-ci](https://travis-ci.org/p6spy/p6spy-it).



