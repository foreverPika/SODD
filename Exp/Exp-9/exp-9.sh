#!/bin/bash

script1="int.sh"
script2="long.sh"
for script in $script1 $script2; do
    if [ -f "$script" ]; then
        if [ ! -x "$script" ]; then
            chmod +x "$script"
        fi
    else
        echo "Error: $script not found."
        exit 1
    fi
done

echo "Running $script1..."
./$script1
echo "$script1 completed."

echo "Running $script2..."
./$script2
echo "$script2 completed."

echo "All scripts have been executed."