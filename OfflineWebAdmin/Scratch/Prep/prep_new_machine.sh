#!/bin/bash

echo 'Add papa to sudoers'
./add_to_sudo.sh 'papa'

echo 'Create offline and add to sudoers'
./create_new_sudo_user.sh 'offline'

echo 'Install SSH server'
./ssh_server_install.sh

echo 'Configure SSH server for papa'
./ssh_server_config.sh 'papa'

echo 'Configure SSH server for offline'
./ssh_server_config.sh 'offline'

echo 'Install python'
./python_install.sh

echo 'Install LAMP stack'
./lamp_stack.sh

echo 'Install java'
./java_install.sh

echo 'Install nano git chrome'
./nano_git_chrome.sh 
