#!/bin/bash -e
# Installs apt-fast to speed up package downloads
#
# http://www.webupd8.org/2012/10/speed-up-apt-get-downloads-with-apt.html
# https://github.com/ilikenwf/apt-fast

sudo add-apt-repository -y ppa:saiarcot895/myppa
sudo apt-get update -qq -y
sudo apt-get install -qq -y --force-yes apt-fast
