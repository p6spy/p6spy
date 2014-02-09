#!/bin/bash -e
# PostgreSQL

psql -c 'create database p6spy;' -U postgres

echo 'contents of postgresql.conf:'
cat /etc/postgresql/*/main/postgresql.conf
