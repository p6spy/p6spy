#!/bin/bash -e
# MySQL
mysql -e 'create database p6spy;'

# otherwise error on trusty: com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: Access denied for user 'travis'@'%' to database 'p6spy'
mysql -e "GRANT ALL ON p6spy.* TO 'travis'@'%';"
mysql -e "GRANT ALL ON p6spy TO 'travis'@'%';"
mysql -e "GRANT CREATE ON p6spy TO 'travis'@'%';"
mysql -e "FLUSH PRIVILEGES;"
