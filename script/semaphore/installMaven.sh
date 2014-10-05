#!/bin/bash
# Installs Apache Maven

# exist on first failure
set -e

# download to cache directory if needed
if [ ! -e $SEMAPHORE_CACHE_DIR/apache-maven-3.2.2-bin.tar.gz ] ; then
  echo "Downloading Maven 3.2.2 to $SEMAPHORE_CACHE_DIR"
  curl https://archive.apache.org/dist/maven/binaries/apache-maven-3.2.2-bin.tar.gz \
     -o $SEMAPHORE_CACHE_DIR/apache-maven-3.2.2-bin.tar.gz
fi

ls -la $SEMAPHORE_CACHE_DIR

echo "Installing Maven 3.2.2"
if [ ! -d /home/runner/maven ] ; then
  mkdir -p /home/runner/maven
fi   
tar -zvxf $SEMAPHORE_CACHE_DIR/apache-maven-3.2.2-bin.tar.gz -C /home/runner/maven
mv /home/runner/maven/apache-maven-3.2.2/* /home/runner/maven

ln -s /home/runner/maven/bin/mvn /usr/local/bin/mvn 

