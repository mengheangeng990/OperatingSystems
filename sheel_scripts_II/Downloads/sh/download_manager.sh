#!/bin/bash

# Download Manager Script
# Automatically organizes files in the current directory by extension

for file in *; do
    # Skip directories
    [ -f "$file" ] || continue

    # Get file extension (lowercase)
    ext="${file##*.}"
    ext="${ext,,}" # convert to lowercase

    # If no extension, put in "others" folder
    [ "$ext" = "$file" ] && ext="others"

    # Create folder if it doesn't exist
    [ -d "$ext" ] || mkdir "$ext"

    # Move file to the folder
    mv "$file" "$ext/"
done

echo "Files organized by type."
