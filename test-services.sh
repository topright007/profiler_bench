#!/bin/bash

# Test script for profiler bench services

echo "=== Testing Profiler Bench Services ==="
echo ""

# Wait for services to be ready
echo "Waiting for services to be healthy..."
sleep 10

# Test IO Service Health
echo ""
echo "1. Testing IO Service Health..."
curl -s http://localhost:8080/actuator/health || echo "IO Service not ready"

# Test CPU Service Health
echo ""
echo "2. Testing CPU Service Health..."
curl -s http://localhost:8081/actuator/health || echo "CPU Service not ready"

# Test Load Generator Health
echo ""
echo "3. Testing Load Generator Health..."
curl -s http://localhost:8082/actuator/health || echo "Load Generator not ready"

# Get a customer ID to test (assuming customer 1 exists after Liquibase)
echo ""
echo "4. Testing Recommendation Endpoint..."
curl -X POST -H "Content-Type: application/json" \
  http://localhost:8080/api/recommendations/1 \
  -w "\nHTTP Status: %{http_code}\n" || echo "Recommendation endpoint failed"

# Run a small load test
echo ""
echo "5. Running Load Test..."
curl -X POST -H "Content-Type: application/json" \
  http://localhost:8082/api/load/start \
  -d '{
    "customerIds": [1, 2, 3, 4, 5],
    "parallelRequests": 2,
    "warmupSeconds": 5,
    "pauseSeconds": 2,
    "measurementSeconds": 10
  }' \
  -w "\nHTTP Status: %{http_code}\n" || echo "Load test failed"

echo ""
echo "=== Tests Complete ==="
