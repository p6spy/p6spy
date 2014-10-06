#!/bin/bash -e
# Sets up environment for p6psy backends for Vagrant run
#
# Copyright (c) 2013 Peter Butkovic <butkovic@gmail.com>
#

# make sure to share maven config across root as well as vagrant user
ln -s /vagrant/script/vagrant/settings.xml /home/vagrant/.m2/settings.xml
sudo ln -s /home/vagrant/.m2 /root/.m2

# in VM current dir is accesible in the: /vagrant
pushd /vagrant/script/vagrant

./before_install_mysql.sh
./before_install_postgres.sh

popd

# in VM current dir is accesible in the: /vagrant
pushd /vagrant/script/travis

# to prevent: sudo: add-apt-repository: command not found
# sudo apt-get install -qq -y software-properties-common python-software-properties

# apt-fast fails with garbled chars in the console for me (in vagrant) => let's just keep using apt-get in vagrant for now
# ./before_install_apt-fast.sh
sudo tee /usr/bin/apt-fast <<EOF > /dev/null
#!/bin/bash
apt-get "\$@"
exit
EOF
sudo chmod 755 /usr/bin/apt-fast

sudo apt-fast update -qq -y

./before_install_firebird.sh
./before_install_db2.sh
./before_install_oracle.sh

./before_script_firebird.sh
./before_script_mysql.sh
./before_script_postgres.sh
./before_script_db2.sh
./before_script_oracle.sh

groovy generateMavenSettings.groovy

popd
