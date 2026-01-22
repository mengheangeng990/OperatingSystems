#!/bin/bash

# Function to check if a group exists
check_group() {
    getent group "$1" > /dev/null 2>&1
}

# Function to check if a user exists
check_user() {
    getent passwd "$1" > /dev/null 2>&1
}

# Prompt for group name
while true; do
    read -p "Enter new group name: " groupname
    if check_group "$groupname"; then
        echo "Error: Group '$groupname' already exists. Try another name."
    else
        break
    fi
done

# Create the group
groupadd "$groupname"
echo "Group '$groupname' created successfully."

# Prompt for username
while true; do
    read -p "Enter new username: " username
    if check_user "$username"; then
        echo "Error: User '$username' already exists. Try another name."
    else
        break
    fi
done

# Create the user with Bash shell and assigned group
useradd -m -s /bin/bash -g "$groupname" "$username"

# Set password for the user
echo "Set password for $username:"
passwd "$username"

# Create a directory at root with the username
mkdir "/$username"

# Set ownership to the user and group
chown "$username:$groupname" "/$username"

# Set permissions: full control for owner and group
chmod 770 "/$username"

# Set sticky bit so only the owner can delete their files
chmod +t "/$username"

echo "User '$username' and directory '/$username' created successfully."
