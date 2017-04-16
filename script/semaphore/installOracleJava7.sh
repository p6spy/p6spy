#!/bin/bash
# Installs Oracle Java 7

# exist on first failure
set -e

# download to cache directory if needed
if [ ! -e $SEMAPHORE_CACHE_DIR/jdk-7u67-linux-x64.tar.gz ] ; then
  echo "Downloading Java 1.7u67 to $SEMAPHORE_CACHE_DIR"
  curl -L --cookie "oraclelicense=accept-securebackup-cookie" \
     http://download.oracle.com/otn-pub/java/jdk/7u67-b01/jdk-7u67-linux-x64.tar.gz \
     -o $SEMAPHORE_CACHE_DIR/jdk-7u67-linux-x64.tar.gz
fi

ls -la $SEMAPHORE_CACHE_DIR

echo "Installing Java 1.7u67"
if [ ! -d /home/runner/java ] ; then
  mkdir -p /home/runner/java
fi   
tar -xvzf $SEMAPHORE_CACHE_DIR/jdk-7u67-linux-x64.tar.gz -C /home/runner/java

