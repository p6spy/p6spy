FROM jacobalberty/firebird:2.5-sc

MAINTAINER Peter Butkovic <butkovic@gmail.com>

RUN \
    # test DB creation
    echo "create database '/tmp/firebird_p6spy.gdb';" > /tmp/firebird_create_db.sql && \
    /usr/local/firebird/bin/isql -user sysdba -password masterkey -i /tmp/firebird_create_db.sql 
