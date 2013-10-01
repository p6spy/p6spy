# Troubleshooting

* Application server does not start properly. This is probably because the p6spy.jar or spy.properties is not in the classpath. If using JBoss, look at the JBoss log file for clues. A message like the following usually means the p6spy.jar file is not found.

        [JDBC provider] Initializing
        [Service Control] Could not initialize DefaultDomain:service=JdbcProvider

* You may see that the driver is loaded, but a message will indicate that the spy.properties file is not found, and will list all of the directories searched for that file. In this case, move the spy.properties file to one of the searched directories listed.

* The spy.properties cannot be found.  Often application servers have their own classpath and ignore the standard Java classpath. Make sure spy.properties is in the classpath that is listed in the error message stating spy.properties cannot be found, this is the classpath that P6Spy is searching.

* The spy.log file is not written. Searching your entire drive for spy.log. Remember, you can specify the default location using the spy.properties property file. See Common Property File Settings for more information.

* The spy.log file is generated, but nothing is logged. P6Spy has the ability to print debug statements that are very useful in determining why the driver is not working. To turn on these statements, remove the debug category and the info category from the excludecategories list in your spy.options file. Also, make sure your wrapped driver is registered correctly. Set useprefix=true in your spy.properties file and put p6spy: as a prefix for your connection URL for the P6Spy driver in your application server configuration. P6Spy will fail if the prefix is not present.

* The spy.log file is generated, but nothing is logged. The DriverManager class sequentially tries every driver that is registered to find the right driver. In some instances, it's possible to load up the realdriver before the p6spy driver, in which case your connections will not get wrapped as the realdriver will "steal" the connection before p6spy sees it. Set the deregisterdrivers property to "true" to cause p6spy to explicitily deregister the realdrivers

* P6Spy was invoked illegally. You probably specified an illegal P6Spy driver in your application configuration. You most likely got this message because you did not specify com.p6spy.engine.spy.SpyDriver as the driver to register in your application configuration. Specifying another class that extends P6SpyDriver is an illegal action.