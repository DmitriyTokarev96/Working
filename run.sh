#!/bin/bash

echo "Starting User Service Application..."
echo

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven and add it to your PATH"
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 11 or higher and add it to your PATH"
    exit 1
fi

echo "Compiling and running the application..."
echo

# Compile and run the application
mvn clean compile exec:java -Dexec.mainClass="com.example.userservice.UserServiceApplication"

if [ $? -ne 0 ]; then
    echo
    echo "Error occurred while running the application"
    exit 1
fi

echo
echo "Application finished"

