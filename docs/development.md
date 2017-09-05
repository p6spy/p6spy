# Development

## Prerequisites

1. Make sure to have Java 1.7 or later installed.

## Building the project
The following are useful Gradle commands:

to build binaries:

    ./gradlew assemble

 to run the JUnit tests Refer to the [Running the tests](#running-the-tests-in-eclipse) section

## License headers
There is a license check done for the source file headers (as part of the CI build), invoked as part of the:

  ./gradlew check

Once new files is introduced, make sure to run (and push the updated files):

  ./gradlew licenseFormat

## Releasing the version
The project follows [semantic versioning](http://semver.org/) concept.
To release the version follow these steps:

* change: `version` in `gradle.properties` to desired one (non-snapshot, to be released one),
* update: `docs/releasenotes.md` to reflect next version, it's release date and release notes,
* change: `version` as well as `release` in `docs/conf.py` to desired version to be released,
* push the changes to master branch of the p6spy repo,
* wait for the green build (in Travis CI), fix problems if necessary,
* perform release (via github pages `releases` -> `Draft new release`), for tag version, use prefix `p6spy-` the version should be named without the prefix,
* after green build performed by Travis CI on tag update the `version` in `gradle.properties` to next snapshot one,
* change: `version` in `docs/conf.py` to next snapshot one and
* push the changes to master branch of the p6spy repo.

Released artifacts should be afterwards present in the [bintray](https://bintray.com/p6spy/maven/p6spy%3Ap6spy) and with a delay of approximatelly 24 hours also in the: [maven central](https://mvnrepository.com/artifact/p6spy/p6spy).

## Running the tests

To run the JUnit tests against specific database(s):

1. Please note, that PostgreSQL, MySQL, Firebird and Oracle specific tests require to have the detabase servers running with the specific databases, users and permissions setup (see: [Integration tests-like environment with Docker Compose](#integration-tests-like-environment-with-docker-compose) section).
1. Moreover as the Oracle jdbc drivers are not publicly available in maven repositories, however can be copied from running docker container used for Oracle DB testing.

By default, tests run against H2 database. To enable other databases, make sure to setup environment variable DB to one of the:

  * PostgreSQL
  * MySQL
  * H2 
  * HSQLDB
  * SQLite
  * Firebird
  * Derby
  * Oracle
  * or comma separated list of these

### Running the tests in the command line

use the following maven command:

    ./gradlew test -DDB=<DB_NAMES>

where `<DB_NAMES>` would hold the value of `DB` environment variable described before.

### Running the tests in Eclipse

1. Make sure to have [buildship plugin](https://github.com/eclipse/buildship) installed 
1. Import the p6spy project to eclipse (as Gradle project)
1. Right click the Class holding the test to run and choose: Run As -> JUnit Test

The `DB` environment variable can be set using Arguments tab -> VM Argument of the JUnit Run Configuration.

### Integration tests-like environment with Docker compose

It might be tricky to run full batery of tests on developer machine (especially due to need of DB servers setup).
To make things easier, [Docker](https://www.docker.com/) with [Docker compose](https://docs.docker.com/compose/) is used to create environment close to the one running on our integration test servers ([travis-ci] (https://travis-ci.org/)).

To have tests running please follow these steps:

1. Install [Docker] (https://docs.docker.com/engine/installation/)
1. Install [Docker compose] (https://docs.docker.com/compose/install/) (version proved to be working is: 1.13.0).
1. To run integration tests on your local machine run following:

        # get p6spy sources
        git clone https://github.com/p6spy/p6spy
        cd p6spy
        # start databases in dockerized environment, please note SQLite installation would still have to be done on the machine manually
        docker-compose up
        # once oracle container is started, run:
        mkdir -p ./build/repo && docker cp p6spy_oracle_1:/u01/app/oracle/product/11.2.0/xe/jdbc/lib/ojdbc6.jar ./build/repo
        # run tests
        ./gradlew test -P travis

To debug the tests remotely, use the following command:

        ./gradlew test -P travis --debug-jvm
      
1. Afterwards use your favorite java IDE to remotely debug (using port 5005) the tests run.

