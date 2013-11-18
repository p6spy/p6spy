#!/bin/bash -e
# Sets up environment for p6psy backends for Vagrant run
#
# Copyright (c) 2013 Peter Butkovic <butkovic@gmail.com>
#

#
# vagrant-cachier maven support pending (see status of: https://github.com/fgrehm/vagrant-cachier/issues/57)
# => let's do it kind of manually for now
# if you want even more caching, make sure you make link in the host system to your maven repo
rm -rf /home/vagrant/.m2
ln -s /vagrant/script/vagrant/m2_cached /home/vagrant/.m2

# in VM current dir is accesible in the: /vagrant
pushd /vagrant/script/vagrant

./before_install_mysql.sh
./before_install_postgres.sh

popd

# in VM current dir is accesible in the: /vagrant
pushd /vagrant/script/travis

./before_install_firebird.sh 
./before_install_db2.sh
./before_install_oracle.sh

./before_script_firebird.sh
./before_script_mysql.sh
./before_script_postgres.sh
./before_script_db2.sh
./before_script_oracle.sh

popd

