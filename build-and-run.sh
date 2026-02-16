#!/bin/bash
set -e

echo "Building all services..."

# Clean and build all services
./gradlew clean build -x test
profiler_build/build.sh

echo "Build successful!"
echo "Starting Docker Compose..."

# Build and start all services
docker-compose up --build -d

echo "Waiting for services to be healthy..."
sleep 30

echo "Checking service status..."
docker ps

echo ""
echo "Services should be available at:"
echo "  - IO Service: http://localhost:8080"
echo "  - CPU Service: http://localhost:8081"
echo "  - Load Generator: http://localhost:8082"
echo ""
echo "To view logs: docker-compose logs -f <service-name>"
echo "To stop: docker-compose down"
