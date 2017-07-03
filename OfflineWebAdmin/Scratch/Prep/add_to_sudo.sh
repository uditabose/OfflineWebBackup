#!/bin/bash

if [ -z "$1" ]; then
    echo 'No valid username'
    exit 1
fi

echo 'Adding' $1 'to sudoers'

sudo echo "$1 ALL=(ALL:ALL) ALL" >>  /etc/sudoers
