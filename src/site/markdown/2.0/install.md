# P6Spy Installation

This installation documentation contains instructions for installing P6Spy with various application
servers. In addition, it contains general installation instructions for application
servers not listed and applications that do not use application servers. Keep
in mind that the [Unspecified Application Server](install.html#unspec)
instructions do not contain details specific to your application server. Use
them only as a guideline. If you create instructions for other application servers, send us a copy for
possible publication in the documentation.

The installation instructions for each app server assume that you are using a MySQL database.  MySQL is only
being used as an example for the purposes of this documentation.  You simply need to use the correct connection URL
and driver class as appropriate for your database.

All instructions also assume *nix operating system.  Again, this is just for the purposes of the documentation.  For
installation on Windows platforms, just substitute p6spy-dist.zip for p6spy-dist.tar.gz.

The installed P6Spy software offers many configuration options. See
[Common Property File Settings](configandusage.html#settings) for additional information.

## <a name="unspec">Generic Instructions</a>

The following installation instructions are intended for use with application servers not listed below and
applications that do not use application servers. To install P6Spy, complete the following steps:

1. Extract the **p6spy-dist.tar.gz** file. The **p6spy-dist.tar.gz** file contains **p6spy.jar** and **spy.properties**.
1. Put the **p6spy.jar** file in your classpath.
1. Put **spy.properties** into a directory which is on the classpath.  Many application servers have a directory for
   configuration files which are accessible via the classpath.  Most applications which do not run an application
   server will have one as well.
1. Modify the JDBC connection URL in your database connection configuration to add p6spy.

       jdbc:p6spy:mysql://<hostname>:<port>/<database>

Installation is complete. When you run your application, a **spy.log** file is generated. The log file contains a
list of all of the database statements executed. You can change both the destination of **spy.log** and what it
logs by editing the **spy.properties** file (see [Common Property File Settings](configandusage.html#settings)).

## JBoss

The following sections contain specific information on installing P6Spy on [JBoss 5.x](#jboss5.x)

### <a name="jboss5.x">JBoss 5.x</a>

The following instructions were tested with JBoss 5.2.0 EAP. For these instructions,
P6Spy assumes that you are using the default server residing in `$JBOSS_DIST\server\default`, where $JBOSS_DIST
is the directory in which JBoss is installed. To install P6Spy on JBoss 5.x, complete the following steps:

1. Extract the **p6spy-dist.tar.gz** file. The **p6spy-dist.tar.gz** file contains **p6spy.jar** and **spy.properties**.
1. Move the **p6spy.jar** file to the `$JBOSS_DIST\server\default\lib` directory.
1. Move the **spy.properties** file to the `$JBOSS_DIST\server\default\conf` directory.
1. Update the data source in `$JBOSS_DIST\server\default\deploy`.  This file is normally called `?????-ds.xml'.
   An example of the pertinent portions (not the complete XML file) follows:

      <jndi-name>MySqlDS</jndi-name>
      <connection-url>jdbc:p6spy:mysql://mysql-hostname:3306/jbossdb</connection-url>
      <driver-class>com.mysql.jdbc.Driver</driver-class>

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in the bin
directory of the JBoss server. The log file contains a list of all of the database statements executed. You can
change both the destination of **spy.log** and what it logs by editing the **spy.properties** file
(see [Common Property File Settings](configandusage.html#settings)).

## Apache Tomcat

The following instructions were tested with Apache Tomcat 6.x.  For these instructions, it is assumed that $CATALINA_HOME
refers to the tomcat installation directory.

1. Extract the **p6spy-dist.tar.gz** file. The **p6spy-dist.tar.gz** file contains **p6spy.jar** and **spy.properties**.
1. Move the **p6spy.jar** file to the lib directory. An example of the path to your
   lib directory is `$CATALINA_HOME\lib\`.
1. Move the **spy.properties** file to the conf directory. An example of the path to your
   conf directory is `$CATALINA_HOME\conf\`.
1. Modify the JDBC connection URL to include p6spy. Tomcat applications obtain their JDBC connection in a variety of ways.
   If your application is using a JNDI data source, then consult the Tomcat docs for the various locations where the
   database connection could be configured.  If the application created its own connection pools, then there will likely
   be an application specific configuration file.  Once you have found the database connection configuration, just modify
   the connection url as per the example below.

        jdbc:p6spy:mysql://localhost/localdb

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in the directory from
which you launched the application. The log file contains a list of all of the database statements executed.
You can change both the destination of **spy.log** and what it logs by editing the **spy.properties** file
(see [Common Property File Settings](configandusage.html#settings)).

