#!/bin/sh

# install firebird
sudo apt-get -y install firebird2.5-superclassic

# configure firebird
sudo dpkg-reconfigure firebird2.5-superclassic -f readline << EOF
Y
travis
EOF

# create DB in the firebird
echo "create database /tmp/firebird_p6spy.gdb;" > /tmp/firebird_create_db.sql
sudo isql-fb -user sysdba -password travis -i /tmp/firebird_create_db.sql