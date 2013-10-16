#!/bin/sh

# install firebird + expect for supplying the values on config
sudo apt-get -y install firebird2.5-superclassic expect

# configure firebird
sudo ./script/travis/firebird_config.sh

# create DB in the firebird
echo "create database '/tmp/firebird_p6spy.gdb';" > /tmp/firebird_create_db.sql
sudo isql-fb -user sysdba -password travis -i /tmp/firebird_create_db.sql
