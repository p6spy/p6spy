#!/bin/bash
# Installs Oracle Java 6

# exist on first failure
set -e

# download to cache directory if needed
if [ ! -e $SEMAPHORE_CACHE_DIR/jdk-6u45-linux-x64.bin ] ; then
  echo "Downloading Java 1.6u45 to $SEMAPHORE_CACHE_DIR"
  curl -L  \
     http://download.oracle.com/otn/java/jdk/6u45-b06/jdk-6u45-linux-x64.bin?AuthParam=1412474638_4e2e9710258a88902ed80c56340ab6b3 \
     -o $SEMAPHORE_CACHE_DIR/jdk-6u45-linux-x64.bin
fi

ls -la $SEMAPHORE_CACHE_DIR

echo "Installing Java 1.6u45"
if [ ! -d /home/runner/java ] ; then
  mkdir -p /home/runner/java
fi   
cd /home/runner/java
$SEMAPHORE_CACHE_DIR/jdk-6u45-linux-x64.bin -noregister

ls -la /home/runner/java

