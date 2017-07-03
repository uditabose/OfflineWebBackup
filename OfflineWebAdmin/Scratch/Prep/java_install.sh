#!/bin/bash

sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get update -yqq

sudo (echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections)
sudo (echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections)
  
sudo apt-get install -y  --no-install-recommends  oracle-java6-installer
sudo apt-get install -y  --no-install-recommends  oracle-java7-installer
sudo apt-get install -y  --no-install-recommends  oracle-java8-installer

sudo update-alternatives --config java
