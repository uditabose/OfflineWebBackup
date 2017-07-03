#!/bin/bash

sudo apt-get update -y 

sudo apt-get upgrade -y 

sudo apt-get install -y openssh-client openssh-server

sudo service ssh restart
