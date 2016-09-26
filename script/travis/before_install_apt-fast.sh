#!/bin/bash -e
# Installs apt-fast to speed up package downloads
#
# http://www.webupd8.org/2012/10/speed-up-apt-get-downloads-with-apt.html

sudo add-apt-repository -y ppa:apt-fast/stable
sudo apt-get update -qq -y
sudo apt-get install -qq -y --force-yes apt-fast
