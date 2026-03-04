#!/bin/bash

# Create class directory if it doesn't exist
mkdir -p bin/class

# Compile Java sources
javac DungeonExit/src/**/*.java -d bin/class

# Check if compilation succeeded
if [ $? -eq 0 ]; then
    # Navigate to GameProject directory and run
    cd DungeonExit && \
    java -cp .:../bin/class:res main.Main
else
    echo "Compilation failed, cannot run"
fi
