# Release Notes

## Version 3.0.0-rc3 [Oct 6, 2016]

Improvements:

* [issue #359](https://github.com/p6spy/p6spy/pull/359): Add getConnectionInformation to StatementInformation
* [issue #358](https://github.com/p6spy/p6spy/pull/358): Add JdbcEventListener.onConnectionWrapped method
* [issue #356](https://github.com/p6spy/p6spy/pull/356): Store the creator of a connection and the connection itself in ConnectionInformation

Defects resolved:

* [issue #348](https://github.com/p6spy/p6spy/issues/348): tomcat 6x-8x integration

## Version 3.0.0-rc2 [Sept 8, 2016]

Defects resolved:

* [issue #347](https://github.com/p6spy/p6spy/pull/347): The real exception is never thrown
* [issue #346](https://github.com/p6spy/p6spy/pull/346): Fixes NPE on static initializer order

## Version 3.0.0-rc1 [Sept 2, 2016]

Improvements:

* [issue #332](https://github.com/p6spy/p6spy/issues/332): Add event listeners via service loader mechanism
* [issue #323](https://github.com/p6spy/p6spy/issues/323): log row count and SQLException
* [issue #297](https://github.com/p6spy/p6spy/issues/297): Allow for lazy initialization of P6Spy

## Version 3.0.0-alpha-1 [July 29, 2016]

Improvements:

* [issue #319](https://github.com/p6spy/p6spy/issues/319): Add support for events
* [issue #298](https://github.com/p6spy/p6spy/issues/298): Provide access to ResultSetInformation
* [issue #299](https://github.com/p6spy/p6spy/issues/299): Support of reacting differently on SQL-Errors within datasource-proxy
* [issue #327](https://github.com/p6spy/p6spy/issues/327): Remove CGLIB

Other:

* [issue #333](https://github.com/p6spy/p6spy/issues/333): Remove org.objectweb.util.monolog.wrapper.p6spy.P6SpyLogger

