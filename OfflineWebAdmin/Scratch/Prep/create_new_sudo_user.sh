#/bin/bash
if [ -z "$1" ]; then
    echo 'No valid username'
    exit 1
fi

echo 'Creating sudo user without password'

sudo adduser --disabled-password --gecos "" "$1"

./add_to_sudo.sh "$1"
