#!/bin/bash -e

# set root pwd to empty
mysql -u root -p'123' -e "use mysql; update user set password=PASSWORD('') where User='root'; flush privileges;"

# add user 'travis with empty pwd
mysql -u root -e "CREATE USER 'travis'@'localhost' IDENTIFIED BY '';"
mysql -u root -e "GRANT ALL PRIVILEGES ON * . * TO 'travis'@'localhost';"
