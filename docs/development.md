# Development

## Prerequisites

1. Make sure to have Java 1.7 or later installed.
1. Download and install [Apache Maven](http://maven.apache.org) 3.0.4 or later.

## Building the project
The following are useful Maven commands:

to build binaries:

    mvn clean install

 to build the site:

    mvn site

 to run the JUnit tests Refer to the [Running the tests](#running-the-tests-in-eclipse) section

## Running the tests

To run the JUnit tests against specific database(s):

1. Make sure to have Java installed.
1. Download and install [Apache Maven](http://maven.apache.org).
1. Please note, that PostgreSQL, MySQL, Firebird, DB2 and Oracle specific tests require to have the detabase servers running with the specific databases, users and permissions setup (see: [Integration tests-like environment with Vagrant](#integration-tests-like-environment-with-vagrant) section).
1. Moreover as the DB2 and Oracle jdbc drivers are not publicly available in maven repositories, these are enabled in travis profile only (see: [Integration tests-like environment with Vagrant](#integration-tests-like-environment-with-vagrant) section).

By default, tests run against H2 database. To enable other databases, make sure to setup environment variable DB to one of the:

  * PostgreSQL
  * MySQL
  * H2 
  * HSQLDB
  * SQLite
  * Firebird
  * Derby
  * DB2
  * Oracle
  * or comma separated list of these


### Custom maven repositories

For some specific database tests (not using default - H2 dabase), the P6Spy maven repo should be added as a repository in the maven settings.xml file. This should be done
by adding the p6spy profile as shown in the example below.

Sample settings.xml:

```
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <profiles>
    <profile>
      <!-- This profile adds repositories needed for integration tests in the p6spy-it project -->
      <id>p6spy-it-mvnrepo</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <repositories>
        <repository>
          <id>p6spy-it-mvnrepo</id>
          <name>p6spy-it-mvnrepo</name>
          <url>https://github.com/p6spy/p6spy-it-mvnrepo/raw/master</url>
          <snapshots><enabled>true</enabled></snapshots>
          <releases><enabled>true</enabled></releases>
        </repository>
      </repositories>
    </profile>
    <profile>
      <!-- This profile adds repositories for site generation -->
      <id>eclipse-egit</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <repositories>
        <repository>
          <id>eclipse-egit</id>
          <name>eclipse-egit</name>
          <url>https://repo.eclipse.org/content/repositories/egit-releases/</url>
          <snapshots><enabled>true</enabled></snapshots>
          <releases><enabled>true</enabled></releases>
        </repository>
      </repositories>
    </profile>
  </profiles>
</settings>
```

### Running the tests in the command line

use the following maven command:

    mvn clean test -DDB=<DB_NAMES>

where &lt;DB_NAMES&gt; would hold the value of `DB` environment variable described before.

### Running the tests in Eclipse

1. Make sure to have [m2e plugin](http://eclipse.org/m2e/) installed 
1. Import the p6spy project to eclipse (as Maven project)
1. Right click the Class holding the test to run and choose: Run As -> JUnit Test

The `DB` environment variable can be set using Arguments tab -&gt; VM Argument of the JUnit Run Configuration.

### Integration tests-like environment with Vagrant

It might be tricky to run full batery of tests on developer machine (especially due to need of DB servers setup).
To make things easier, [Vagrant] (http://www.vagrantup.com/) is used to create environment close to the one running on our integration test servers ([travis-ci] (https://travis-ci.org/)).

To have tests running please follow these steps:

1. Install [Vagrant] (http://www.vagrantup.com/) in your environment with Virtualbox as provider (please note, that currently latest version: 1.6.5 is proved to be working)
1. Install [Chef Development Kit] (http://downloads.getchef.com/chef-dk/) to enable Berkshelf usage.
1. Install Vagrant plugins we use:

        vagrant plugin install vagrant-omnibus
        vagrant plugin install vagrant-berkshelf
        vagrant plugin install vagrant-cachier

1. To remotely debug the integration tests on your local machine run following:

        vagrant up
        vagrant ssh
        cd /vagrant
        mvn clean test -P travis -Dmaven.surefire.debug --settings ~/.m2/p6spySettings.xml
      
1. Use your favorite java IDE to remotely debug the tests run.

