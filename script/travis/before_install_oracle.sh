#/bin/sh
# Script performs non-interactive instllation of Oracle XE 10g on Debian
#
# Based on oracle10g-update.sh from HTSQL project:
# https://bitbucket.org/prometheus/htsql
#
# Modified by Mateusz Loskot <mateusz@loskot.net>
# Changes:
# - Add fake swap support (backup /usr/bin/free manually anyway!)
# 
# Modified by Peter Butkovic <butkovic@gmail.com> to enable i386 install on amd64 architecture (precise 64)
# based on: http://www.ubuntugeek.com/how-to-install-oracle-10g-xe-in-64-bit-ubuntu.html
# 
# set -ex

#
# Utilities
#
function free_backup()
{
    # Multiple copies to be on safe side
    sudo cp /usr/bin/free /root
    sudo mv /usr/bin/free /usr/bin/free.original
}

function free_restore()
{
    sudo cp /usr/bin/free.original /usr/bin/free
}

# Install fake free
# http://www.axelog.de/2010/02/7-oracle-ee-refused-to-install-into-openvz/
free_backup

sudo tee /usr/bin/free <<EOF > /dev/null
#!/bin/sh
cat <<__eof
             total       used       free     shared    buffers     cached
Mem:       1048576     327264     721312          0          0          0
-/+ buffers/cache:     327264     721312
Swap:      2000000          0    2000000
__eof
exit
EOF

sudo chmod 755 /usr/bin/free

# add Oracle repo + key
sudo bash -c 'echo "deb http://oss.oracle.com/debian/ unstable main non-free" >/etc/apt/sources.list.d/oracle.list'
wget -q https://oss.oracle.com/el4/RPM-GPG-KEY-oracle -O- | sudo apt-key add -
# sudo sh -c 'apt-get update -qq; true' # just to prevent stopping on error
sudo apt-get update -qq
# sudo apt-get update -qq | true

#
# ok, bc, is the dependency that is required by DB2 as well => let's remove it from oracle xe dependencies and provide 64bit one only
#

# Install the Oracle 10g dependant packages
sudo apt-get install -qq --force-yes libc6:i386 libaio:i386
sudo apt-get install -qq bc 

# only download the package, to manually install afterwards
sudo apt-get install -qq --force-yes -d oracle-xe-universal:i386

mkdir /tmp/oracle_unpack
dpkg-deb -x /var/cache/apt/archives/oracle-xe-universal_10.2.0.1-1.1_i386.deb /tmp/oracle_unpack
cd /tmp/oracle_unpack
dpkg-deb --control /var/cache/apt/archives/oracle-xe-universal_10.2.0.1-1.1_i386.deb 
sed -i "s/,\ bc//g" /tmp/oracle_unpack/DEBIAN/control
mkdir /tmp/oracle_repack
dpkg -b /tmp/oracle_unpack /tmp/oracle_repack/oracle-xe-universal_fixed_10.2.0.1-1.1_i386.deb

# install Oracle 10g with the fixed dependencies, to prevent i386/amd64 conflicts on bc package
sudo dpkg -i --force-architecture /tmp/oracle_repack/oracle-xe-universal_fixed_10.2.0.1-1.1_i386.deb

# remove key + repo (to prevent failures on next updates)
sudo apt-key del B38A8516
sudo bash -c 'rm -rf /etc/apt/sources.list.d/oracle.list'
sudo apt-get update -qq
sudo apt-get autoremove -qq

# Fix the problem when the configuration script eats the last
# character of the password if it is 'n': replace IFS="\n" with IFS=$'\n'.
sudo sed -i -e s/IFS=\"\\\\n\"/IFS=\$\'\\\\n\'/ /etc/init.d/oracle-xe

# Configure the server; provide the answers for the following questions:
# The HTTP port for Oracle Application Express: 8080
# A port for the database listener: 1521
# The password for the SYS and SYSTEM database accounts: admin
# Start the server on boot: yes
sudo /etc/init.d/oracle-xe configure <<END
8080
1521
admin
admin
y
END

# Load Oracle environment variables so that we could run `sqlplus`.
. /usr/lib/oracle/xe/app/oracle/product/10.2.0/server/bin/oracle_env.sh

# Increase the number of connections.
echo "ALTER SYSTEM SET PROCESSES=40 SCOPE=SPFILE;" | \
sqlplus -S -L sys/admin AS SYSDBA

# Set Oracle environment variables on login.
cat <<END >>/root/.bashrc

. /usr/lib/oracle/xe/app/oracle/product/10.2.0/server/bin/oracle_env.sh
END

free_restore

# no more needed - stored in the cloudbees mvn repo
# need to install jdbc to local repo
# mvn install:install-file -Dfile=/vagrant/script/travis/ojdbc7.jar -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.1 -Dpackaging=jar -DgeneratePom=true

