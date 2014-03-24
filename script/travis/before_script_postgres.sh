#!/bin/bash -e
# PostgreSQL

psql -c 'create database p6spy;' -U postgres

pushd /etc/postgresql/*/main/
# a must for xa transactions
sudo sh -c 'echo "max_prepared_transactions=2" >> postgresql.conf'
#echo 'contents of postgresql.conf:'
#cat postgresql.conf
popd

# to apply setting:
# max_prepared_transactions=2
sudo service postgresql restart
