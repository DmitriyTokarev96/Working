#!/bin/bash

echo "Testing User Service API..."
echo

BASE_URL=http://localhost:8080/api/users

echo "1. Testing GET all users (should be empty initially)..."
curl -s $BASE_URL
echo
echo

echo "2. Testing POST - Create user..."
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","age":25}'
echo
echo

echo "3. Testing GET all users (should show 1 user)..."
curl -s $BASE_URL
echo
echo

echo "4. Testing GET user by ID..."
curl -s $BASE_URL/1
echo
echo

echo "5. Testing GET user by email..."
curl -s $BASE_URL/email/test@example.com
echo
echo

echo "6. Testing PUT - Update user..."
curl -X PUT $BASE_URL/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated User","age":30}'
echo
echo

echo "7. Testing GET user after update..."
curl -s $BASE_URL/1
echo
echo

echo "8. Testing DELETE user..."
curl -X DELETE $BASE_URL/1
echo
echo

echo "9. Testing GET all users (should be empty again)..."
curl -s $BASE_URL
echo
echo

echo "API testing completed!"
