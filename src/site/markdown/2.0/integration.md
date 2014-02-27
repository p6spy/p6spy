# Integrating P6Spy

A very typical use case for P6Spy is to enabled SQL logging to troubleshoot various database related issues during 
development.  Assuming that making code changes is acceptable, then the following instructions can be used.  If
 making code changes is not a viable option, then following the instructions for [Installing P6Spy](install.html).

1. Add **p6spy.jar** to the classpath.  If your application uses Maven, Ivy, Gradle, etc just add a dependency on 
   p6spy:p6spy.

1. Wrap your DataSource with P6DataSource or modify your connection URL to add 'p6spy:'.
   
If your application uses a DataSource, simply wrap your current DataSource object with P6DataSource.  P6DataSource
has a constructor method that accepts the DataSource to wrap.  This is by far the simplest method
especially if you use a dependency injection framework such as Spring or Guice.    

If your application obtains connections from DriverManager, simply modify your JDBC connection URL to include 
'p6spy:'.  For example, if your URL is `jdbc:mysql://host/db` then just change it to 
`jdbc:p6spy:mysql://host/db`. P6Spy implements the JDBC 4.0 API allowing automatic registration of our JDBC driver 
with DriverManager.  As long as your application obtains connections through DriverManager, you only need to modify 
your database connection URL to activate P6Spy.

By default, a file called spy.log will be created in the current working directory.  To customize the logging
(including using your application's logging framework) you can provide alternate configuration in a file called
spy.properties.  This file just needs to be at the root of the classpath.  See [Configuration and Usage](configandusage.html)
for details.

 






