#!/bin/bash -e

# in VM current dir is accesible in the: /vagrant
pushd /vagrant/script/vagrant

./before_install_mysql.sh
./before_install_postgres.sh

popd

# in VM current dir is accesible in the: /vagrant
pushd /vagrant/script/travis

# ./before_install_oracle.sh
./before_install_firebird.sh 
./before_install_db2.sh

./before_script_firebird.sh
./before_script_mysql.sh
./before_script_postgres.sh
./before_script_db2.sh

popd

# let's cache the maven repo (kind of manually)
# if you want even more caching, make sure you make link in the host system to your maven repo
ln -s /vagrant/script/vagrant/m2_cached /home/vagrant/.m2
