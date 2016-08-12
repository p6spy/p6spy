#!/bin/bash -e
# Firebird

isql-fb -z -q -i /dev/null # --version
echo "create database '127.0.0.1:/tmp/firebird_p6spy.gdb';" > /tmp/firebird_create_db.sql # preps for test DB creation
sudo isql-fb -user sysdba -password masterkey -i /tmp/firebird_create_db.sql # test DB creation

