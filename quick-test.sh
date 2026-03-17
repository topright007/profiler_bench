#!/bin/bash

echo "=== Profiler Bench Quick Test ==="
echo ""

echo "1. Checking service health..."
echo "   IO Service (8080):"
curl -s http://localhost:8080/actuator/health | grep -q "UP" && echo "   ✅ Healthy" || echo "   ❌ Not healthy"

echo "   CPU Service (8081):"
curl -s http://localhost:8081/actuator/health | grep -q "UP" && echo "   ✅ Healthy" || echo "   ❌ Not healthy"

echo ""
echo "2. Testing recommendation endpoint (small customer, no fixed)..."
echo "   Customer 1:"
RESULT=$(curl -s -X POST "http://localhost:8080/api/recommendations/1")
echo "   $RESULT" | head -c 150
echo "..."

echo ""
echo "3. Testing fixed=true vs default (small + large customers)..."
echo "   Small customer (id=10), fixed=false:"
START=$(date +%s%3N)
curl -s -X POST "http://localhost:8080/api/recommendations/10" > /dev/null
END=$(date +%s%3N)
echo "   Response time: $((END - START))ms"

echo "   Small customer (id=10), fixed=true:"
START=$(date +%s%3N)
curl -s -X POST "http://localhost:8080/api/recommendations/10?fixed=true" > /dev/null
END=$(date +%s%3N)
echo "   Response time: $((END - START))ms"

echo "   Large customer (id=100001), fixed=false:"
START=$(date +%s%3N)
curl -s -X POST "http://localhost:8080/api/recommendations/100001" > /dev/null
END=$(date +%s%3N)
echo "   Response time: $((END - START))ms"

echo "   Large customer (id=100001), fixed=true:"
START=$(date +%s%3N)
curl -s -X POST "http://localhost:8080/api/recommendations/100001?fixed=true" > /dev/null
END=$(date +%s%3N)
echo "   Response time: $((END - START))ms"

echo ""
echo "4. Testing different customer sizes..."
for ID in 10 100 1000; do
  echo "   Customer $ID:"
  START=$(date +%s%3N)
  curl -s -X POST "http://localhost:8080/api/recommendations/$ID" > /dev/null
  END=$(date +%s%3N)
  DURATION=$((END - START))
  echo "   Response time: ${DURATION}ms"
done

echo ""
echo "5. Quick load test (10 requests)..."
START=$(date +%s%3N)
for i in {1..10}; do
  curl -s -X POST "http://localhost:8080/api/recommendations/$((1 + RANDOM % 100))" > /dev/null &
done
wait
END=$(date +%s%3N)
DURATION=$((END - START))
echo "   10 parallel requests completed in: ${DURATION}ms"

echo ""
echo "=== Test Complete! ==="
echo ""
echo "Services are ready for profiling:"
echo "  - IO Service: http://localhost:8080"
echo "  - CPU Service: http://localhost:8081"
echo "  - Prometheus: http://localhost:9090"
echo ""
echo "See QUICKSTART.md for more testing scenarios"
