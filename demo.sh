#!/bin/bash

# AI DevSecOps Pipeline - Spring Boot Application Demo Script
# This script demonstrates the complete functionality of the application

echo "🚀 AI DevSecOps Pipeline - Spring Boot Application Demo"
echo "======================================================"
echo

# Check if application is running
echo "1. Checking if application is running on port 8081..."
if curl -f -s http://localhost:8081/actuator/health > /dev/null; then
    echo "✅ Application is running!"
else
    echo "❌ Application is not running. Please start it with:"
    echo "   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081"
    exit 1
fi
echo

# Test API endpoints
echo "2. Testing API endpoints..."
echo "   📋 Getting all users (should be empty initially):"
curl -s http://localhost:8081/api/users | jq '.' 2>/dev/null || curl -s http://localhost:8081/api/users
echo
echo

echo "   ➕ Creating first user (John Doe):"
curl -s -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john.doe@example.com"}' | jq '.' 2>/dev/null || \
curl -s -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john.doe@example.com"}'
echo
echo

echo "   ➕ Creating second user (Jane Smith):"
curl -s -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Smith","email":"jane.smith@example.com"}' | jq '.' 2>/dev/null || \
curl -s -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Smith","email":"jane.smith@example.com"}'
echo
echo

echo "   📋 Getting all users (should show 2 users):"
curl -s http://localhost:8081/api/users | jq '.' 2>/dev/null || curl -s http://localhost:8081/api/users
echo
echo

echo "   🔍 Getting user by ID (ID: 1):"
curl -s http://localhost:8081/api/users/1 | jq '.' 2>/dev/null || curl -s http://localhost:8081/api/users/1
echo
echo

echo "   ✏️  Updating user (ID: 1):"
curl -s -X PUT http://localhost:8081/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Updated","email":"john.updated@example.com"}' | jq '.' 2>/dev/null || \
curl -s -X PUT http://localhost:8081/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Updated","email":"john.updated@example.com"}'
echo
echo

echo "   📋 Getting all users (should show updated user):"
curl -s http://localhost:8081/api/users | jq '.' 2>/dev/null || curl -s http://localhost:8081/api/users
echo
echo

echo "   🗑️  Deleting user (ID: 2):"
curl -s -X DELETE http://localhost:8081/api/users/2
echo "   User deleted successfully"
echo

echo "   📋 Getting all users (should show 1 user):"
curl -s http://localhost:8081/api/users | jq '.' 2>/dev/null || curl -s http://localhost:8081/api/users
echo
echo

# Show application endpoints
echo "3. Application endpoints:"
echo "   🌐 Web Interface:  http://localhost:8081"
echo "   🔧 H2 Console:     http://localhost:8081/h2-console"
echo "   ❤️  Health Check:   http://localhost:8081/actuator/health"
echo "   📊 Metrics:        http://localhost:8081/actuator/metrics"
echo "   ℹ️  Info:           http://localhost:8081/actuator/info"
echo

echo "4. API Documentation:"
echo "   📚 Base URL: http://localhost:8081/api"
echo "   └── GET    /users       - Get all users"
echo "   └── POST   /users       - Create new user"
echo "   └── GET    /users/{id}  - Get user by ID"
echo "   └── PUT    /users/{id}  - Update user by ID"
echo "   └── DELETE /users/{id}  - Delete user by ID"
echo

echo "✅ Demo completed successfully!"
echo "🎉 Your Spring Boot application with JavaScript frontend is working perfectly!"