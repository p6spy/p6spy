#!/bin/bash -e
# MySQL

mysql -e "create database p6spy;"

# mysql -e "\
# create database p6spy; \
# GRANT ALL ON p6spy.* TO 'travis'@'%'; \
# GRANT ALL ON p6spy TO 'travis'@'%'; \
# GRANT CREATE ON p6spy TO 'travis'@'%'; \
# FLUSH PRIVILEGES;"
