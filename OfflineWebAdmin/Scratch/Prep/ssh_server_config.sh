#!/bin/bash

if [ -z "$1" ]; then
    echo 'No valid username'
    exit 1
fi

echo 'Create ~/.ssh directory'
if [ ! -d "/home/$1/.ssh" ]; then  
    sudo su - "$1" "mkdir -v /home/$1/.ssh"
fi

sudo su -c "chmod 700 /home/$1/.ssh" "$1" 
sudo su -c "cd /home/$1/.ssh" "$1" 

echo 'Create ssh key'
sudo su -c "cat /dev/zero | ssh-keygen -t rsa -f id_rsa -q -P ''"  "$1" 
sudo su -c "cat id_rsa.pub >> authorized_keys"  "$1" 
sudo su -c "chmod 600 /home/$1/.ssh/authorized_keys"  "$1" 

echo 'Set-up password less ssh login'

if [ ! -f '/etc/ssh/sshd_config' ]; then  
    sudo su -c "touch '/etc/ssh/sshd_config'" "$1" 
fi

sudo su -c "echo 'RSAAuthentication yes' >> '/etc/ssh/sshd_config'" "$1" 
sudo su -c "echo 'PubkeyAuthentication yes' >> '/etc/ssh/sshd_config'" "$1" 
sudo su -c "echo 'ChallengeResponseAuthentication no' >> '/etc/ssh/sshd_config'" "$1" 
sudo su -c "echo 'PasswordAuthentication no' >> '/etc/ssh/sshd_config'" "$1" 
sudo su -c "echo 'UsePAM no' >> '/etc/ssh/sshd_config'" "$1" 

sudo service ssh restart
