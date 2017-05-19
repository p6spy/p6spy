FROM postgres:latest

MAINTAINER Peter Butkovic <butkovic@gmail.com>

RUN \
    # a must for xa transactions
    echo "max_prepared_transactions=2" >> /usr/share/postgresql/postgresql.conf.sample
