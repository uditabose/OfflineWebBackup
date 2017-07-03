#!/bin/bash

sudo apt-get update -y
sudo apt-get install -y build-essential libssl-dev libcurl4-gnutls-dev libexpat1-dev gettext unzip
sudo apt-get install -y git nano

git config --global user.name "Udita Bose"
git config --global user.email "ub298@nyu.edu"

wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
sudo sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list'

sudo apt-get install google-chrome-stable

#wget https://www.nano-editor.org/dist/v2.7/nano-2.7.3.tar.gz
#tar zxvf nano-2.7.3.tar.gz
#cd nano-2.7.3
#./configure && make && make install
