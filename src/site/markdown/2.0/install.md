# P6Spy Installation

This installation documentation contains instructions for installing P6Spy with various application
servers. In addition, it contains general installation instructions for application
servers not listed and applications that do not use application servers. Keep
in mind that the [Unspecified Application Server](install.html#unspec)
instructions do not contain details specific to your application server. Use
them only as a guideline. If you create instructions for other application servers, send us a copy for
possible publication in the documentation.

The installed P6Spy software offers many configuration options. See
[Common Property File Settings](configandusage.html#settings) for additional information.

## JBoss

The following sections contain specific information on installing P6Spy on [JBoss 2.x](#jboss2) and [JBoss 3.x.](#jboss3)

### <a name="jboss2">JBoss 2.x</a>

The following instructions were tested with various JBoss versions ranging from
JBoss 2.2.2-2.4.4 with Jakarta Tomcat 3.2.2-4.0.1 integration. To install P6Spy on JBoss 2.x,
complete the following steps:

1. Extract the **p6spy-install.jar** file.  The **p6spy-install.jar** file contains
  **p6spy.jar** and **spy.properties**.
1. Move the **p6spy.jar** file to the  directory. An example of the path to your ext directory is
  `C:\JBoss-2.2.2_Tomcat-3.2.2\jboss\lib\ext`.
1. Move the **spy.properties** file to the db directory, which is part of the JBoss classpath.
  An example of the path to your db directory is C:\JBoss-2.2.2_Tomcat-3.2.2\jboss\db.
1. Modify the**jboss.jcml** file in the Tomcat conf directory to use the P6Spy driver, by removing
  your existing driver from the Drivers attribute and replacing it with the P6Spy driver. An example
  of an edited **jboss.jcml** file follows:

        <mbean code="org.jboss.jdbc.JdbcProvider" name="DefaultDomain:service=JdbcProvider">
          <attribute name="Drivers">com.p6spy.engine.spy.P6SpyDriver</attribute>
        </mbean>

1. Modify the realdriver line in the **spy.properties** file to reflect the wrapped database driver.
  An example of a modified realdriver line follows:

        realdriver = oracle.jdbc.driver.OracleDriver

Installation is complete. When you run your application, a **spy.log** file is generated in the bin directory
of the JBoss server. The log file contains a list of all of the database statements executed. You can change
both the destination of **spy.log** and what it logs by editing the **spy.properties** file
(see [Common Property File Settings](configandusage.html#settings)).

### <a name="jboss3">JBoss 3.x</a>
(Contributed by Alan Arvesen, IronGrid)

The following instructions were tested with JBoss 3.0.4 with Jetty integration. For these instructions,
P6Spy assumes that you are using the default server residing in `JBOSS_DIST\server\default`, where JBOSS_DIST
is the directory in which JBoss is installed. These instructions are intended for wrapping driver services,
not XADataSource services. To install P6Spy on JBoss 3.x, complete the following steps:

1. Extract the **p6spy-install.jar** file. The **p6spy-install.jar** file contains **p6spy.jar** and **spy.properties**.
1. Move the **p6spy.jar** file to the lib directory. An example of the path to your lib directory is
  `C:\java\jbossext-3.0.4\server\default\lib`.
1. Add **spy.properties** to your JBoss classpath. By default, the JBoss classpath set up by **run.bat**
  only points to **tools.jar** and **run.jar**. You may need to add a directory, via JBOSS_CLASSPATH, to this path.
  An example of an edited JBoss classpath follows:

        C:\> set JBOSS_CLASSPATH=C:\java\jboss-3.0.4\server\default\db

        C:\> copy C:\java\p6spy\spy.* C:\java\jboss-3.0.4\server\default\db

1. Create a **p6spy-service.xml** file in JBOSS_DIST\server\deploy. Set the config-property element named
  DriverClass to **P6SpyDriver** in **p6spy-service.xml**, by basing a new XML file on one of the examples in
  the `JBOSS_DIST\docs\examples\jca` directory. You can also configure JndiName and ConnectionURL for your
  specific implementation. An example of the pertinent portions (not the complete XML file) of a
  **p6spy-service.xml** file follows:

        <attribute name="JndiName">MySqlDS</attribute>
        <attribute name="ManagedConnectionFactoryProperties">
        <properties>
        <config-property name="ConnectionURL" type="java.lang.String">jdbc:mysql://localhost/localdb</config-property>
        <config-property name="DriverClass"type="java.lang.String">com.p6spy.engine.spy.P6SpyDriver</config-property>

**Note:** Make sure there is not a service that explicitly loads the wrapped driver. A service of this type will
load the specified driver before P6SpyDriver, resulting in unwrapped connections.
Modify the realdriver line in the **spy.properties** file to reflect the wrapped database driver. An example of a
modified realdriver line follows:

        realdriver=org.gjt.mm.mysql.Driver

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in the bin
directory of the JBoss server. The log file contains a list of all of the database statements executed. You can
change both the destination of **spy.log** and what it logs by editing the **spy.properties** file
(see [Common Property File Settings](configandusage.html#settings)).


## Orion

The following instructions were tested with Orion. To install P6Spy on Orion, complete the following steps:

1. Extract the **p6spy-install.jar** file. The **p6spy-install.jar** file contains **p6spy.jar** and **spy.properties**.
1. Move **p6spy.jar** and **spy.properties** to the orion-home\lib directory, where orion-home is the
  directory in which Orion is installed. An example of the path to your orion-home\lib directory is `C:\orion\lib`.
1. Modify the connection-driver line in **data-sources.xml** to use P6SpyDriver. **data-sources.xml**
  resides in orion-home\config. An example of an edited **data-sources.xml** file follows:

        <data-source
        class="com.evermind.sql.DriverManagerDataSource"
        name="Hypersonic"
        location="jdbc/HypersonicCoreDS"
        xa-location="jdbc/xa/HypersonicXADS"
        ejb-location="jdbc/HypersonicDS"
        connection-driver="com.p6spy.engine.spy.P6SpyDriver"
        username="sa"
        password=""
        url="jdbc:HypersonicSQL:./database/defaultdb"
        inactivity-timeout="30"
        />

1. In the example installation outlined above, Orion cannot locate **spy.properties** because of the classpath
  that Orion uses. Use the -D option to tell Orion where the property file resides by running Orion using
  the following command:

        java -Dp6.home=c:\orion\orion\lib -jar orion.jar

1. Modify the realdriver line in the **spy.properties** file to reflect the wrapped database driver.
  (This is the original value specified in connection-driver.) For example, if Hypersonic
  (the default database) was the original connection-driver value, the realdriver value
  is set to **org.hsql.jdbcDriver**.

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in
the orion-home directory of the Orion server. The log file contains a list of all of the database
statements executed. You can change both the destination of **spy.log** and what it logs by editing the
**spy.properties** file (see [Common Property File Settings](configandusage.html#settings)).

## Jakarta Tomcat

The following instructions were tested with Jakarta Tomcat 3.x. Note that since you typically write or otherwise
obtain your own Connection Pool when using Tomcat, these instructions will differ by application. To
install P6Spy on Jakarta Tomcat, complete the following steps:

1. Extract the **p6spy-install.jar** file. The **p6spy-install.jar** file contains **p6spy.jar** and **spy.properties**.
1. Move the **p6spy.jar** and **spy.properties** files to the lib directory. An example of the path to your
`lib directory is` C:\jakarta-tomcat-3.2.1\webapps\p6\WEB-INF\lib`.
1. Modify Tomcat to use P6SpyDriver. Tomcat applications obtain their JDBC connection in a variety of ways.
  Most applications have the database driver name externalized as a configuration parameter. For example, in
  your **web.xml** file (which resides in `C:\jakarta-tomcat-3.2.1\webapps\p6\WEB-INF`) you may have something
  similar to the following:

        <init-param><param-name>databaseDriver</param-name><param-value>org.gjt.mm.mysql.Driver</param-value></init-param>

    In this above case, the databaseDriver parameter has a value of **org.gjt.mm.mysql.Driver**, which is the MySQL
    driver. Wherever this configuration information is stored, change the driver name (**org.gjt.mm.mysql.Driver**,
    in our example) to **com.p6spy.engine.spy.P6SpyDriver**, as in the following example:

        <init-param><param-name>databaseDriver</param-name><param-value>com.p6spy.engine.spy.P6SpyDriver</param-value></init-param>

1. Enable Tomcat to locate **spy.properties**. While all of the JAR files in your lib directory are, by default,
  included in Tomcat's classpath, the lib directory itself is not. This means Tomcat will not be able to find your
  **spy.properties** file, which will result in an error. So, you must include **spy.properties** in the Tomcat
  classpath. Use the TOMCAT_OPTS environment variable to specify the location of the **spy.properties** file.
  An example of the edited TOMCAT_OPTS environment variable follows:

        SET TOMCAT_OPTS=-Dp6.home=C:\jakarta-tomcat-3.2.1\webapps\p6\WEB-INF\lib

1. Modify the realdriver line in the **spy.properties** file to reflect the wrapped database driver. An example of a
`modified realdriver line (using MySQL as the driver) follows:

        realdriver = org.gjt.mm.mysql.Driver

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in the directory from
which you launched the application. The log file contains a list of all of the database statements executed.
You can change both the destination of **spy.log** and what it logs by editing the **spy.properties** file
(see [Common Property File Settings](configandusage.html#settings)).

## JOnAS EJB

(Contributed by François Exertier, JOnAS Opensource EJB Server team)

The following instructions were tested with JOnAS 2.3.x. To install P6Spy on JOnAS EJB, complete the following steps:

1. Extract the **p6spy-install.jar** file. The **p6spy-install.jar** file contains **p6spy.jar** and **spy.properties**.
1. Put the **p6spy.jar** file in the classpath of the JOnAS EJB server. For example, if **p6spy.jar** is in the
  `/opt/Provider6` directory, add `/opt/Provider6/p6psy.jar` to the XTRA_CLASSPATH environment variable or
  directly edit the config_env script.
1. Move **spy.properties** to a directory which is part of your classpath. It is recommended that you move it
  to the directory where your jndi.properties file resides.
1. Modify the **datasource.properties** file (where datasource is the name of the data source) by replacing the
  database driver classname with the P6Psy database driver classname in the datasource.classname property.
  For example, the Oracle1.properties file delivered with JOnAS should be updated by replacing
  **oracle.jdbc.driver.OracleDriver** with **com.p6spy.engine.spy.P6SpyDriver**. An example follows:

        datasource.name   jdbc_1
        datasource.description  "Standard jdbc driver for Oracle"
        datasource.url   jdbc:oracle:thin:scott/tiger@maltes:1521:ORA1
        datasource.classname com.p6spy.engine.spy.P6SpyDriver
        ...

Note: Database access configuration within JOnAS is described in a DataSource property file, such as
Oracle1.properties. Refer to the JOnAS Bean Programmer's Guide for more details.

1. Modify the realdriver line in the **spy.properties** file to reflect the wrapped database driver. An
  example of a modified realdriver line follows:

        realdriver = oracle.jdbc.driver.OracleDriver

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in
the directory from which you launched the application. The log file contains a list of all of the database
statements executed. You can change both the destination of **spy.log** and what it logs by editing the
**spy.properties** file (see [Common Property File Settings](configandusage.html#settings)).


## BEA WebLogic

The following sections contain specific information on installing P6Spy on
[BEA WebLogic Portal 4.0](#beaportal), [BEA WebLogic Server 6.1](#wlserver6), and [BEA WebLogic 5.1](#wlserver5).

Note: IBM Cloudscape does not work with P6Spy and BEA WebLogic. However, Cloudscape does work with P6Spy
when using the J2EE reference implementation server. The following suggestion was submitted by Adrian
Fletcher:

*"I may be wrong but I suspect the problem with Cloudscape and WebLogic is that both drivers end up
registered against the same URL. I seem to remember that the best trick for avoiding this is mangling the URL.
If you instruct users to prepend "p6spy:" to their URL and then remove it in your wrapped file, it should
work in more cases." You can do this with the useprefix option.*

If anyone has an opportunity to validate this, please drop us a line with updated information.

### <a name="beaportal">BEA WebLogic Portal 4.0</a>

(Contributed by Philip Ogren, BEA)
The following instructions were tested with WebLogic Server 6.1 Service Pack 2 with WebLogic Portal 4.0 Service Pack 1
running on Microsoft Windows using the following:

* WebLogic jDriver for Oracle
* Oracle Thin driver for 8.1.7
* WebLogic jDriver for Microsoft SQL Server
* Sybase jConnect-5_2 driver

The default Stockportal (Avitek) application is used in this example. To install P6Spy on BEA WebLogic Portal,
  complete the following steps:

1. Extract the **p6spy-install.jar** file to a temporary directory. The **p6spy-install.jar** file contains the
  files **p6spy.jar** and **spy.properties**.
1. Move **p6spy.jar** to `bea-home\wlportal4.0\lib\ext`, where bea-home is the directory in which BEA WebLogic is
  installed.
1. Add `;%P13N_DIR%\lib\ext\p6spy.jar` to `EXT_CLASSPATH` in the **set-environment.bat** file. **set-environment.bat**
  resides in `bea-home\wlportal4.0\bin\win32`. Do not include any trailing spaces after the new line.
1. Move **spy.properties** to bea-home\wlportal4.0\config\portalDomain or to the application's domain directory.
1. Modify the realdriver line in the **spy.properties** file to reflect your wrapped database driver. An example
  of a modified realdriver line follows:

        realdriver=weblogic.jdbc.oci.Driver

1. Add `;%P13N_DIR%\config\portalDomain` to the classpath in the **startPortal.bat** file. The path to **startPortal.bat**
  is `bea-home\wlportal4.0\config\portalDomain`. Do not include any trailing spaces after the new line.
1. Modify WebLogic Portal to use the P6Spy driver. Either modify **config.xml** (which resides in
  `bea-home\wlportal4.0\config\portalDomain`) directly or complete the following steps:
  * Start WebLogic Portal by running **startPortal.bat**.
  * Start the Administration Console tool by navigating to `http://your-host:7501/console`, where your-host
    is the machine on which BEA WebLogic is installed.
  * Navigate to the *your-domain* (where *your-domain* is the domain name specified during the BEA WebLogic install)
    **-> Services -> JDBC -> Connection Pools** node and set the Driver Classname for each Connection Pool to **com.p6spy.engine.spy.P6SpyDriver**.
1. If using RDBMS Security Realm, configure P6Spy to monitor your realms (in this example, commercePool,
  dataSyncPool, and wlcsRealm) by completing the following steps:
  * Using the Administration Console, navigate to the **your-domain -> Security -> Realms** node.
  * Select the security realm.
  * Click the Database tab.
  * Set the Driver parameter to com.p6spy.engine.spy.P6SpyDriver.
1. Using the Administration Console, shut down WebLogic Portal. Right-click on the **your-domain->Servers->your-server**
  node and select **Stop this Server** from the pop-up menu.
1. Restart WebLogic Portal by running **startPortal.bat**.

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in
`bea-home\wlportal4.0`. The log file contains a list of all of the database statements executed.
You can change both the destination of **spy.log** and what it logs by editing the **spy.properties**
file (see [Common Property File Settings](configandusage.html#settings)).

### <a name="wlserver6">BEA WebLogic Server 6.1</a>

(Contributed by Philip Ogren, BEA)

The following instructions were tested with WebLogic Server 6.1 Service Pack 2 with WebLogic Portal 4.0 Service Pack 1
running on Microsoft Windows using the following:

* WebLogic jDriver for Oracle
* Oracle Thin driver for 8.1.7
* WebLogic jDriver for Microsoft SQL Server
* Sybase jConnect-5_2 driver

To install P6Spy on BEA WebLogic Server, complete the following steps:

1. Extract the **p6spy-install.jar** file to a temporary directory. The **p6spy-install.jar** file contains the
  files **p6spy.jar** and **spy.properties**.
1. Move **p6spy.jar** to `bea-home\wlserver6.1\lib\ext`, where bea-home is the directory in which BEA
  WebLogic is installed.
1. Move **spy.properties** to bea-home\wlserver6.1\config\your-domain, where your-domain is the domain name
  specified during the BEA WebLogic install.
1. Modify the realdriver line in the **spy.properties** file to reflect your wrapped database driver. An
  example of a modified realdriver line follows:

        realdriver=weblogic.jdbc.oci.Driver

1. Add `;.\lib\ext\**p6spy.jar**;.\config\your-domain` to the classpath in the **startWebLogic.cmd** file.
  **startWebLogic.cmd** resides in `bea-home\wlserver6.1\config\your-domain`. Do not include any trailing spaces
  after the new line. This adds **p6spy.jar** and **spy.properties** to the classpath.
1. Modify WebLogic Server to use the P6Spy driver. Either modify **config.xml**
  (which resides in `bea-home\wlserver6.1\config\your-domain`) directly or complete the following steps:
  * Start WebLogic Server by running **startWebLogic.cmd**.
  * Start the Administration Console tool by navigating to `http://<your-host>:7501/console`.
  * Navigate to the **your-domain -> Services -> JDBC -> Connection Pools** node and set the Driver Classname
  for each Connection Pool to **com.p6spy.engine.spy.P6SpyDriver**.
1. If using RDBMS Security Realm, configure P6Spy to to monitor your realms (in this example, commercePool,
  dataSyncPool, and wlcsRealm) by completing the following steps:
  * Using the Administration Console, navigate to the **your-domain -> Security -> Realms** node.
  * Select the security realm.
  * Click the Database tab.
  * Set the Driver parameter to **com.p6spy.engine.spy.P6SpyDriver**.
1. Using the Administration Console, shut down WebLogic Server. Right-click on the
  **your-domain -> Servers -> your-server** node and select **Stop this Server** from the pop-up menu.
1. Restart WebLogic Portal by running **startWebLogic.cmd**.

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in
`bea-home\wlserver6.1`. The log file contains a list of all of the database statements executed. You can change
both the destination of **spy.log** and what it logs by editing
the **spy.properties** file (see [Common Property File Settings](configandusage.html#settings)).


### <a name="wlserver5">BEA WebLogic 5.1</a>

(Contributed by Richard Delbert)

The following instructions were tested with WebLogic 5.1. To install P6Spy on BEA WebLogic, complete the following steps:

1. Extract the **p6spy-install.jar** file. The **p6spy-install.jar** file contains **p6spy.jar** and **spy.properties**.
1. Edit **startWeblogic.sh**, by putting **p6spy.jar** in the JAVA_CLASSPATH.
1. Edit **startWeblogic.sh**, by putting **spy.properties** in the JAVA_CLASSPATH.
1. Edit **weblogic.properties** (Connection Pool), by replacing the JDBC class driver with **com.p6spy.engine.spy.P6SpyDriver**.
1. Modify the realdriver line in the **spy.properties** file to reflect your wrapped database driver. An example of
  a modified realdriver line follows:

        realdriver = oracle.jdbc.driver.OracleDriver

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in the WebLogic home
directory. The log file contains a list of all of the database statements executed. You can change both the
destination of **spy.log** and what it logs by editing the **spy.properties** file
(see [Common Property File Settings](configandusage.html#settings)).

## ATG Dynamo
The following instructions were tested with ATG Dynamo 5.1. To install P6Spy on ATG Dynamo, complete the following steps:

1. Extract the **p6spy-install.jar** file into a directory (C:\p6spy, in our example). The
  **p6spy-install.jar** contains **p6spy.jar** and **spy.properties**.
1. Start the ATG Dynamo application server and your database.
1. Modify the ATG Dynamo server to include **p6spy.jar** and **spy.properties** in its classpath by completing the
  following steps:
    1. Run the Admin Tool by logging in to `http://localhost:8830`, assuming you are. (This URL is an example
      which assumes you are running ATG Dynamo on your local machine).
        Note: The default user/pass is admin/admin.
    1. Select **Configuration Manager -> Default Configuration -> System Paths**.
    1. Enter the full path of the **p6spy.jar** file under **Extend Dynamo's Classpath**. An example of
      the path is `C:\p6spy\p6spy.jar`.
    1. Enter the full path of the **spy.properties** file under Extend Dynamo's Classpath. An example of the
      path is `C:\p6spy`.
    1. Click Append to Classpath.
    1. Confirm the new classpath value is correct under **Extend Dynamo's Classpath**.
    1. Restart ATG Dynamo by navigating to `http://localhost:8830` and clicking **Restart Dynamo**.

1. Modify the realdriver line in the **spy.properties** file to reflect the wrapped database driver. The
  realdriver property is, by default, set to Oracle, as in:

        realdriver = oracle.jdbc.driver.OracleDriver

    Change this to reflect your database. An example of a modified realdriver line for ATG demo follows:

        realdriver = solid.jdbc.SolidDriver

1. Modify ATG to use the P6Spy driver by completing the following steps:
    1. Run the Admin Tool by logging in to `http://localhost:8830`.
    1. Select **Configuration Manager -> Default Configuration -> Connection Pools -> JTDataSource -> JDBC 1.x/2.x driver -> Custom JDBC Information**.
    1. Enter the information exactly as you would for connecting to your database, except for the driver box. Set
    the driver box to **com.p6spy.engine.spy.P6SpyDriver**.

      An example setup follows:

          URL: jdbc:solid://localhost:1313
          driver: com.p6spy.engine.spy.P6SpyDriver
          database:
          server: localhost:1313
          server name:
          user name: admin
          password: admin
          confirm password: admin

    1. Click Try to Connect. Confirm that the ATG connection is successful.
    1. Click Apply Changes, if the connection is successful.
    1. Restart ATG Dynamo by navigating to to `http://localhost:8830` and clicking Restart Dynamo.

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in the
`C:\ATG\Dynamo5.1\home` directory. The log file contains a list of all of the database statements executed. You can
change both the destination of **spy.log** and what it logs by editing the **spy.properties** file
(see [Common Property File Settings](configandusage.html#settings)).

## Sun iPlanet

(Contributed by Michael Sgroi)

The following instructions were tested with Sun iPlanet 6.0 Service Pack 3 and should work for any release of
iPlanet 6.0. To install P6Spy on iPlanet, complete the following steps:

1. Extract the **p6spy-install.jar** file into a directory named spy-home, where spy-home is the name of the
  P6Spy directory. The **p6spy-install.jar** file contains **p6spy.jar** and **spy.properties**.
1. Add **p6spy.jar** to your classpath by running the command line executable **kregedit**.
  Under **Software\iPlanet -> 6.0 -> Java -> ClassPath**, modify the entry adding spy-home to the end of the classpath.
1. Modify your application server to use the P6Spy database driver by running the command line executable
  **jdbcsetup**. Add a new 3rd Party JDBC Configuration as shown in the following example:

        Driver Identifier: spy
        Driver Classname: com.p6spy.engine.spy.P6SpyDriver
        Driver Classpath: spy-home\p6spy.jar

1. To tell P6Spy about the wrapped database driver, register your DataSource by running the following command line executable:

        iasdeploy regdatasource -user iPlanet_admin_username -password iPlanet_admin_password -host localhost -port iPlanet_admin _port config_filename

    where iPlanet_admin_username is your iPlanet admin username, iPlanet_admin_password is your iPlanet admin
    password, iPlanet_admin_port is the iPlanet admin port number, and config_filename is the full path to the
    file containing your DataSource configuration. An example follows:

        <ias-resource>
        <resource>
        <jndi-name>jdbc/yourapp/ora-type4-spy</jndi-name>
        <jdbc>
        <driver-type>spy</driver-type>
        <database-url>jdbc:oracle:thin:@localhost:1521:orcl</database-url>
        <username>user</username>
        <password>password</password>
        </jdbc>
        </resource>
        </ias-resource>

    (For more details on parameters passed to **iasdeploy** and the format of config_filename, refer to the iPlanet documentation.)
    Installation is complete. When you run your application, a **spy.log** file is generated. The log file contains
    a list of all of the database statements executed. You can change both the destination of **spy.log** and what
    it logs by editing the **spy.properties** file (see [Common Property File Settings](configandusage.html#settings)).

## IBM WebSphere

The following instructions were tested with IBM WebSphere 4.0.

Note: Before installing, make sure your JDBC driver is configured in your target application server as a DataSource.

To install P6Spy on IBM WebSphere, complete the following steps:

1. Extract the **p6spy-install.jar** file. The installation file contains **p6spy.jar** and **spy.properties**.
1. Run the WebSphere Administration tool by logging in to `http://localhost/9090/admin`.
1. Under the WebSphere Administration Domain, open the Resources tree.
1. Open the JDBC Drivers tree.
1. Open the JDBC Driver you want to replace. The list of DataSources defined for this driver displays.
1. Click on the name of the DataSource that you want to trace with P6Spy. The configuration page for that DataSource displays.
1. Before making any changes, make a note of the values for the following fields:

        DatabaseName
        Default User
        Default Password

1. Click **Properties** to view the Driver Specific properties. Each of the items displayed in the table is a setting
  that the driver needs in order to connect to the database. Make a note of the name and value of each item. In our
  MySQL example, these properties are as follows:

        port 3306
        serverName myhost

1. Navigate to the previous page (the configuration page for the driver), and change the JNDI Name for the DataSource.
  For example, if the DataSource JNDI name is **MySqlDS**, change it to **RealMySqlDS**.
1. Click **JDBC Drivers** (in the left panel), to open the list of Drivers.
1. Click **New** to open a configuration page for a new driver.
1. Accept the **User-defined JDBC Driver** selection.
1. Click **Next**.
1. Fill in the table with the following values:
        Server Class Path = path_to_p6spy.jar
        Name = P6SpyDriver
        Description = P6Spy JDBC tracing driver
        Implementation Classname = com.p6spy.engine.spy.P6ConnectionPoolDataSource
1. Click **OK**. The list of drivers, which contains your new driver specifications, displays.
1. Open the new JDBC Drivers tree, which contains P6SpyDriver, in the left panel.
1. Open the **Data Sources** folder.
1. Click **New** to create a new DataSource for P6SpyDriver.
1. Set this DataSource up like the original configuration for your target driver (the one that you earlier recorded
  in your notes), except for two things: the DataSource name and the Driver Specific properties. Use something
  different for the DataSource name, and do not set any Driver Specific properties.
1. Save the configuration.
1. Configure the **spy.properties** file. Assuming that your names match the ones used above, and that you are using
  the MySQL driver from mysql.com, your DataSource related entries should look like the following examples:

        realdatasource=RealMySqlDS
        prealdatasourceclass=com.mysql.jdbc.jdbc2.optional.MysqlDataSource
        realdatasourceproperties=port;3306,serverName;myhost

    Note: The items in **realdatasourceproperties** are the items that you recorded from the Driver Specific
    properties in your original DataSource configuration.

1. Modify the realdriver line in the **spy.properties** file to reflect the wrapped database driver (**com.mysql.Driver**,
  in our example). An example of a modified realdriver line follows:

        realdriver=com.mysql.Driver

1. Move the **spy.properties** file to the WebSphere/AppServer/properties directory.
1. Restart WebSphere, by using either the FirstSteps control panel or the scripts in the WebSphere bin directory.

Installation is complete. When you run your application, a **spy.log** file is generated. The log file contains
a list of all of the database statements executed. You can change both the destination of **spy.log** and what it
logs by editing the **spy.properties** file (see [Common Property File Settings](configandusage.html#settings)).

## Caucho Resin

(Contributed by Hadi Nahari, RadonSoft)

The following instructions were tested with Resin 2.1.x. Note that since you typically write or otherwise obtain
your own Connection Pool when using Resin, these instructions will differ by application. To install
P6Spy on Resin, complete the following steps:


1. Extract the **p6spy-install.jar** file. The **p6spy-install.jar** file contains **p6spy.jar** and **spy.properties**.
1. Move the **p6spy.jar** and **spy.properties** files to the lib directory. An example of the path to your lib
  directory is `C:\resin-2.1.6\webapps\your_app_name\WEB-INF\lib`, where your_app_name is the name of your application.
1. Modify Resin to use P6SpyDriver. Resin applications obtain their JDBC connection in a variety of ways. Most
  applications have the database driver name externalized as a configuration parameter. For example, in your
  **web.xml** file (which may reside in `C:\resin-2.1.6\webapps\your_app_name\WEB-INF`) you may have something similar to the following:

        <init-param>
        <param-name>db.driver</param-name>
        <!-- mysql (mm driver) -->
        <param-value>org.gjt.mm.mysql.Driver</param-value>
        </init-param>

    In the above case, the db.driver parameter has a value of **org.gjt.mm.mysql.Driver**, which is the MySQL driver.
    Wherever this configuration information is stored, change the driver name (**org.gjt.mm.mysql.Driver**, in
    our example) to **com.p6spy.engine.spy.P6SpyDriver**, as in the following example:

        <init-param>
        <param-name>db.driver</param-name>
        <!-- mysql (mm driver) -->
        <param-value>com.p6spy.engine.spy.P6SpyDriver</param-value>
        </init-param>

1. Modify the realdriver line in the **spy.properties** file to reflect the wrapped database driver. An example of a
modified realdriver line (using MySQL as the driver) follows:

        realdriver = **org.gjt.mm.mysql.Driver**

1. In your start-up script (for Unix), or startup command-line (for Windows), enable Resin to locate **spy.properties**.
  While all of the JAR files in your lib directory are, by default, included in Resin's classpath, the lib directory
  itself is not. This means Resin will not be able to find your **spy.properties** file, which will result in an error.
  So, you must include **spy.properties** in the Resin classpath. Use Resin's command-line argument to specify the
  location of the **spy.properties** file. An example of passing p6.home environment variable as a command-line
  argument to Resin's httpd on Windows follows:

        C:\resin-2.1.6\bin\httpd - -Dp6.home=C:\resin-2.1.6\webapps\your_app_name\WEB-INF\lib

Installation is complete. When you run your application, a log file ( **spy.log** ) is generated in the
directory from which you launched the application. The log file contains a list of all of the database statements
executed. You can change both the destination of **spy.log** and what it logs by editing the **spy.properties**
file (see [Common Property File Settings](configandusage.html#settings)).

## <a name="unspec">Unspecified Application Server</a>

The following installation instructions are intended for use with application servers not listed above and applications that do not use application servers. To install P6Spy, complete the following steps:

1. Extract the **p6spy-install.jar** file. The **p6spy-install.jar** file contains **p6spy.jar** and **spy.properties**.
1. Put the **p6spy.jar** file in your classpath by copying the **p6spy.jar** file into your ext directory. If you are using an EJB server it probably has its own ext directory. If you are not using an EJB server, then use the JDK ext directory, which is JDK_HOME/jre/lib/ext, or add **p6spy.jar** to your classpath. Remember that if you are adding **p6spy.jar** to your classpath, you must reference the JAR filename directly.  An example of the path in Microsoft Windows follows:

        CLASSPATH=c:\p6spy\p6spy.jar

    Note: If you are using an EJB server, adding the **p6spy.jar** file to your classpath may not work. Some EJB servers ignore the classpath, setting their own on startup.
1. Move **spy.properties** into a directory listed in your classpath. For example, if your classpath is C:\p6spy\p6spy.jar;c:\java, copy it to the java directory. Unlike JAR files, you do not directly reference your property file in the classpath.
1. Modify your application server or application to use the P6Spy database driver.
1. Modify the realdriver line in the **spy.properties** file to reflect the wrapped database driver. An example of a modified realdriver line follows:

        realdriver = oracle.jdbc.driver.OracleDriver

Installation is complete. When you run your application, a **spy.log** file is generated. The log file contains a list of all of the database statements executed. You can change both the destination of **spy.log** and what it logs by editing the **spy.properties** file (see [Common Property File Settings](configandusage.html#settings)).

