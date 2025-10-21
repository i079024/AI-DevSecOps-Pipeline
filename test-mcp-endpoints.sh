#!/bin/bash

# Test script for MCP HTTP endpoints
echo "Testing MCP HTTP Wrapper endpoints..."

echo -e "\n1. Testing health endpoint..."
curl -s http://127.0.0.1:3001/health | jq .

echo -e "\n2. Testing speculator analyze endpoint..."
curl -s -X POST http://127.0.0.1:3001/api/speculator/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "abc123test",
    "branch": "main", 
    "author": "test@example.com",
    "repository": "https://github.com/test/repo.git"
  }' | jq .status

echo -e "\n3. Testing MCP analyze endpoint..."
curl -s -X POST http://127.0.0.1:3001/api/mcp/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "abc123test",
    "codeDiff": [],
    "sonarURL": "http://localhost:9000/dashboard?id=test",
    "context": {
      "branch": "main",
      "author": "test@example.com"
    }
  }' | jq .status

echo -e "\nAll tests completed!"