#!/bin/bash
# Reset
Color_Off='\033[0m'       # Text Reset

# Regular Colors
Black='\033[0;30m'        # Black
Red='\033[0;31m'          # Red
Green='\033[0;32m'        # Green
Yellow='\033[0;33m'       # Yellow
Blue='\033[0;34m'         # Blue
Purple='\033[0;35m'       # Purple
Cyan='\033[0;36m'         # Cyan
White='\033[0;37m'        # White


echo -e $Yellow'************* etcd configuration : start **************'$Color_Off

if [[ -z "$1" ]]; then
	echo -e $Red'No directory for etcd'$Color_Off
	exit 1
fi

echo
echo -e $Cyan'Download golang'$Color_Off

mkdir '/tmp/golang' && cd '/tmp/golang' &&  curl -LO https://storage.googleapis.com/golang/go1.7.4.linux-amd64.tar.gz

echo -e $Cyan'Install golang'$Color_Off
echo
sudo apt-get update
tar -xvf go1.7.4.linux-amd64.tar.gz
sudo mv go /usr/local

echo -e $Cyan'Configure golang'$Color_Off
echo
echo 'export PATH=$PATH:/usr/local/go/bin' >> ~/.bash_profile
echo 'export PATH=$PATH:/usr/local/go/bin' >> ~/.bashrc
source ~/.bash_profile
source ~/.bashrc


echo -e $Cyan'Download etcd'$Color_Off
if [ ! -d "$1"/etcd ]; then
	cd "$1" && git clone https://github.com/coreos/etcd.git	
fi

echo -e $Cyan'Install etcd'$Color_Off
echo

cd "$1"/etcd
chmod +x build
./build

echo -e $Yellow'************* etcd configuration : end ****************'$Color_Off
