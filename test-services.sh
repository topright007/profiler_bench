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

# Test recommendation endpoint: small/large customers, with and without fixed=true
echo ""
echo "4. Testing Recommendation Endpoint..."

echo "   Small customer (id=1), fixed=false:"
curl -s -X POST "http://localhost:8080/api/recommendations/1" -w " HTTP %{http_code}\n" -o /dev/null || echo "   Failed"

echo "   Small customer (id=1), fixed=true:"
curl -s -X POST "http://localhost:8080/api/recommendations/1?fixed=true" -w " HTTP %{http_code}\n" -o /dev/null || echo "   Failed"

echo "   Large customer (id=100001), fixed=false:"
curl -s -X POST "http://localhost:8080/api/recommendations/100001" -w " HTTP %{http_code}\n" -o /dev/null || echo "   Failed"

echo "   Large customer (id=100001), fixed=true:"
curl -s -X POST "http://localhost:8080/api/recommendations/100001?fixed=true" -w " HTTP %{http_code}\n" -o /dev/null || echo "   Failed"

# Run a small load test (small + large customers)
echo ""
echo "5. Running Load Test..."
curl -X POST -H "Content-Type: application/json" \
  http://localhost:8082/api/load/start \
  -d '{
    "customerIds": [1, 2, 10, 100001, 100002],
    "parallelRequests": 2,
    "warmupSeconds": 5,
    "pauseSeconds": 2,
    "measurementSeconds": 10
  }' \
  -w "\nHTTP Status: %{http_code}\n" || echo "Load test failed"

echo ""
echo "=== Tests Complete ==="
