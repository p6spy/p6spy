#!/bin/bash
# Installs groovy

# exist on first failure
set -e


# download to cache directory if needed
if [ ! -e $SEMAPHORE_CACHE_DIR/groovy-2.3.7.zip ] ; then
  echo "Downloading groovy 2.3.7 to $SEMAPHORE_CACHE_DIR"
  wget http://dl.bintray.com/groovy/maven/groovy-binary-2.3.7.zip -O $SEMAPHORE_CACHE_DIR/groovy-2.3.7.zip
fi

echo "Installing groovy"
mkdir -p /home/runner/groovy
unzip $SEMAPHORE_CACHE_DIR/groovy-2.3.7.zip -d /home/runner/groovy
mv /home/runner/groovy/*/* /home/runner/groovy

sudo ln -s /home/runner/groovy/bin/groovy /usr/local/bin/groovy 
