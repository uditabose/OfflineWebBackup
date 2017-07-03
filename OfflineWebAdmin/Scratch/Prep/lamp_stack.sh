#!/bin/bash

sudo apt-get update -y 
sudo apt-get install -y apache2
sudo apt-get install -y mysql-server libapache2-mod-auth-mysql php5-mysql
sudo apt-get install -y php5 libapache2-mod-php5 php5-mcrypt
sudo apt-get install -y php libapache2-mod-php php-mcrypt php-mysql
