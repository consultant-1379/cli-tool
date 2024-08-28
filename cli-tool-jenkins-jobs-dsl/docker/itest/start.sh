#!/bin/bash

__create_user() {
# Create a user to SSH into as.
useradd netsim
SSH_USERPASS=netsim
echo -e "$SSH_USERPASS\n$SSH_USERPASS" | (passwd --stdin netsim)
echo ssh user password: $SSH_USERPASS
}

# Call all functions
__create_user
