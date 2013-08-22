# see: http://about.travis-ci.org/docs/user/database-setup/

# PostgresSQL is provided by travis => no additional setup needed

# DB creation
psql -c 'create database p6spy;' -U postgres