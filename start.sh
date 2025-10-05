#!/bin/bash

echo "Starting User Service..."
echo

echo "Checking Java version..."
java -version
echo

echo "Building application..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo
echo "Starting Spring Boot application..."
echo "Application will be available at: http://localhost:8080"
echo "API documentation: http://localhost:8080/api/users"
echo
echo "Press Ctrl+C to stop the application"
echo

mvn spring-boot:run
