#!/bin/bash -e
# Installs DB2 for SOCI/p6spy build at travis-ci.org
#
# Copyright (c) 2013 Brian R. Toonen <toonen@alcf.anl.gov>
# Copyright (c) 2013 Mateusz Loskot <mateusz@loskot.net>
# 
# addaptions for P6Spy purposes done by Peter Butkovic <butkvic@gmail.com>

sudo bash -c 'echo "deb http://archive.canonical.com/ubuntu precise partner" >> /etc/apt/sources.list'
sudo apt-get update -qq
sudo apt-get install -y bc
sudo apt-get install -y db2exc

echo "Running db2profile and db2rmln"
sudo /bin/sh -c '. ~db2inst1/sqllib/db2profile ; $DB2DIR/cfg/db2rmln'

echo "Setting up db2 users"
echo -e "db2inst1\ndb2inst1" | sudo passwd db2inst1
echo -e "db2fenc1\ndb2fenc1" | sudo passwd db2fenc1
echo -e "dasusr1\ndasusr1" | sudo passwd dasusr1

echo "Configuring DB2 ODBC driver"
if test `getconf LONG_BIT` = "64" ; then
    if test -f /home/db2inst1/sqllib/lib64/libdb2o.so ; then
        DB2_ODBC_DRIVER=/home/db2inst1/sqllib/lib64/libdb2o.so
    else
        echo "ERROR: can't find the 64-bit DB2 ODBC library"
        exit 1
    fi
else
    if test -f /home/db2inst1/sqllib/lib32/libdb2.so ; then
        DB2_ODBC_DRIVER=/home/db2inst1/sqllib/lib32/libdb2.so
    elif test -f /home/db2inst1/sqllib/lib/libdb2.so ; then
        DB2_ODBC_DRIVER=/home/db2inst1/sqllib/lib/libdb2.so
    else
        echo "ERROR: can't find the 32-bit DB2 ODBC library"
        exit 1
    fi
fi
echo "DB2 ODBC driver set to $DB2_ODBC_DRIVER"

# need to install jdbc to local repo
mvn install:install-file -Dfile=/opt/ibm/db2/V9.7/java/db2jcc4.jar -DgroupId=com.ibm.db2 -DartifactId=db2jcc4 -Dversion=9.7 -Dpackaging=jar
mvn install:install-file -Dfile=/opt/ibm/db2/V9.7/java/db2jcc_license_cu.jar -DgroupId=com.ibm.db2 -DartifactId=db2jcc_license_cu -Dversion=9.7 -Dpackaging=jar

