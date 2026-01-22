#!/bin/bash

if [ "$1" -gt "$2" ]; then
    echo "$1 is greater"
elif [ "$1" -lt "$2" ]; then
    echo "$2 is greater"
else
    echo "Both are equal"
fi
