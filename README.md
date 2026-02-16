# Profiler Bench - Profiler Showcase System

A comprehensive microservices system built with Java 21 and Spring Boot to showcase profiler capabilities through realistic IO-bound and CPU-bound workloads.

## Quick Start

```bash
# Build and run all services
./build-and-run.sh

# Or manually:
./gradlew clean build
docker-compose up --build -d
```

## Architecture

Three microservices demonstrating different performance characteristics:

1. **IO Service** (`:8080`) - Database operations with PostgreSQL and HTTP client calls
2. **CPU Service** (`:8081`) - CPU-intensive calculations (50-500ms per request)
3. **Load Generator** (`:8082`) - Configurable load patterns with warmup/pause/measure phases

All services feature 5-level deep call stacks for profiling visibility.

## Testing

```bash
# Test a single recommendation
curl -X POST http://localhost:8080/api/recommendations/1

# Run a load test
curl -X POST http://localhost:8082/api/load/start \
  -H "Content-Type: application/json" \
  -d '{
    "customerIds": [1, 2, 3, 4, 5],
    "parallelRequests": 10,
    "warmupSeconds": 30,
    "pauseSeconds": 10,
    "measurementSeconds": 60
  }'
```

## Documentation

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Detailed architecture, service descriptions, and profiling integration points
- **[MEMORYBANK.md](MEMORYBANK.md)** - Project context, design decisions, and AI agent guidance
- **[test-services.sh](test-services.sh)** - Automated testing script

## Key Features

- **Traditional Threading Model**: Thread-per-request for clear profiling data (no virtual threads)
- **5-Level Call Depth**: Deep method hierarchies visible in profilers
- **Realistic Data**: 100K+ customers with devices and buildings
- **CPU-Intensive Work**: Actual computations (matrix ops, fibonacci, prime checks)
- **Configurable Load**: Warmup, pause, and measurement phases
- **Metrics Integration**: Prometheus metrics on all services
- **Database Operations**: Complex queries, JPA mapping, connection pooling

## Services

### IO Service (Port 8080)
- Database queries with JPA
- HTTP client calls to CPU service
- Transaction management
- Connection pooling

**Call Stack:**
```
RecommendationController
└─> RecommendationService
    └─> CustomerEnrichmentService
        └─> DeviceAggregationService
            └─> BuildingAnalysisService
                └─> Repository Layer
```

### CPU Service (Port 8081)
- Matrix multiplication
- Fibonacci calculations
- Prime number checks
- Hash computations
- Statistical analysis

**Call Stack:**
```
CalculationController
└─> RecommendationCalculatorService
    └─> DeviceScorerService
        └─> CompatibilityAnalyzerService
            └─> ScoreAggregatorService
                └─> Utility Classes
```

### Load Generator (Port 8082)
- Configurable parallel requests
- Three-phase load pattern
- Metrics collection
- Random customer selection

## Profiling Scenarios

### Scenario 1: Database Performance
Profile IO service with large customers to analyze query execution and connection pool behavior.

### Scenario 2: CPU Hotspots
Profile CPU service to identify most expensive calculations and optimization opportunities.

### Scenario 3: Memory Allocation
Profile both services under load to analyze object creation patterns and GC pressure.

### Scenario 4: Thread Behavior
Profile load generator to analyze thread pool utilization and contention.

## Monitoring

- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`
- VictoriaMetrics: `http://localhost:8428`

All services expose metrics at `/actuator/prometheus`.

## Database

PostgreSQL with Liquibase migrations:
- 100,000 regular customers (1-3 buildings, 1-10 devices per building)
- 10 large customers (50-200 buildings, 20-100 devices per building)

Access: `localhost:15432` (user: postgres, password: password)

## Technology Stack

- **Java 21** with Eclipse Temurin JDK
- **Spring Boot 3.2.2** with Actuator
- **PostgreSQL 17.8** with Liquibase
- **Gradle 8.5** multi-module build
- **Docker Compose** for deployment
- **Prometheus & VictoriaMetrics** for metrics

## Development

```bash
# Build specific service
./gradlew :io-service:bootJar

# Run tests
./gradlew test

# View logs
docker-compose logs -f io-service

# Stop services
docker-compose down
```

## Requirements

- Docker & Docker Compose
- Java 21 (for local development)
- 4GB+ RAM for Docker

## License

MIT
