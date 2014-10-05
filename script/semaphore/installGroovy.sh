#!/bin/bash
# Installs groovy

# exist on first failure
set -e

export GROOVY_HOME=/home/runner/groovy/groovy-2.3.7

# download to cache directory if needed
if [ ! -e $SEMAPHORE_CACHE_DIR/groovy-2.3.7.zip ] ; then
  echo "Downloading groovy 2.3.7 to $SEMAPHORE_CACHE_DIR"
  wget http://dl.bintray.com/groovy/maven/groovy-binary-2.3.7.zip -O $SEMAPHORE_CACHE_DIR/groovy-2.3.7.zip
fi

echo "Installing groovy"
mkdir -p $GROOVY_HOME
unzip $SEMAPHORE_CACHE_DIR/groovy-2.3.7.zip -d $GROOVY_HOME
export PATH=$PATH:$GROOVY_HOME/bin
ls -la $GROOVY_HOME/bin

groovy -version