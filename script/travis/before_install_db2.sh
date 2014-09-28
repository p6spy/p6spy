#!/bin/bash -e
# Installs DB2 for SOCI/p6spy build at travis-ci.org
#
# Copyright (c) 2013 Brian R. Toonen <toonen@alcf.anl.gov>
# Copyright (c) 2013 Mateusz Loskot <mateusz@loskot.net>
# 
# addaptions for P6Spy purposes done by Peter Butkovic <butkvic@gmail.com>

# add canonical partner repository
sudo bash -c 'echo "deb http://archive.canonical.com/ubuntu precise partner" >> /etc/apt/sources.list.d/canonical_parter.list'

# update package list after adding new source
sudo apt-fast update -qq -y

# install db2
sudo apt-fast install -qq -y bc db2exc

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
mvn -q install:install-file -Dfile=/opt/ibm/db2/V9.7/java/db2jcc4.jar -DgroupId=com.ibm.db2 -DartifactId=db2jcc4 -Dversion=9.7 -Dpackaging=jar -DgeneratePom=true 
mvn -q install:install-file -Dfile=/opt/ibm/db2/V9.7/java/db2jcc_license_cu.jar -DgroupId=com.ibm.db2 -DartifactId=db2jcc_license_cu -Dversion=9.7 -Dpackaging=jar -DgeneratePom=true

# fix for:
# Failure in loading native library db2jcct2, java.lang.UnsatisfiedLinkError: no db2jcct2 in java.library.path:  ERRORCODE=-4472, SQLSTATE=null
# see: https://stackoverflow.com/questions/3957131/java-lang-unsatisfiedlinkerror-while-loading-db2-jdbc-driver
echo 'export LD_LIBRARY_PATH=/home/db2inst1/sqllib/lib64/;' >> /home/vagrant/.bashrc
