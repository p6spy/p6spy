#!/bin/bash -e

psql -c "CREATE USER travis WITH PASSWORD '';" -U postgres
