#!/bin/bash
set -e

export ORACLE_HOME=/u01/app/oracle/product/11.2.0/xe
export PATH=$ORACLE_HOME/bin:$PATH
export ORACLE_SID=XE

echo exit | sqlplus -L travis/travis@localhost:1521/xe
