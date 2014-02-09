#!/bin/bash -e

psql -c "CREATE USER travis WITH PASSWORD '';" -U postgres

# to apply setting:
# max_prepared_transactions=2
sudo service postgresql restart
