# MEMORYBANK - Profiler Bench Project

## Project Context

**Project Name**: Profiler Bench  
**Purpose**: Showcase profiler capabilities through realistic Java microservices workloads  
**Target Audience**: Profiler vendors, performance engineers, developers  
**Technology**: Java 21, Spring Boot 3.2.2, PostgreSQL, Docker  

## Core Objectives

1. **Demonstrate Profiler Value**: Create realistic workloads that highlight profiler capabilities
2. **Showcase Performance Patterns**: IO-bound, CPU-bound, and mixed workload scenarios
3. **Enable Profiling Practice**: Provide a safe environment for learning profiling tools
4. **Realistic Architecture**: Follow microservices best practices with proper layering

## Design Decisions

### Threading Model
**Decision**: Traditional thread-per-request (NO virtual threads)  
**Rationale**: 
- Clear profiling data with visible thread allocation
- Easier to understand thread behavior in profilers
- Realistic thread-per-request pattern still common in production
- Avoids complexity of virtual thread profiling in early adopter phase

### Service Architecture
**Decision**: Three separate services (IO, CPU, Load Generator)  
**Rationale**:
- Separation of concerns for different performance characteristics
- IO service demonstrates database and HTTP client profiling
- CPU service shows pure computational profiling
- Load generator provides controlled, reproducible load patterns

### Method Call Depth
**Decision**: 5-level deep call stacks in both IO and CPU services  
**Rationale**:
- Demonstrates profiler's ability to handle call stack depth
- Realistic for enterprise applications
- Makes flame graphs more interesting
- Shows method-level granularity in profilers

### Database Size
**Decision**: 100,000 regular customers + 10 large customers  
**Rationale**:
- Enough data to show performance differences
- Large customers demonstrate handling of complex queries
- Not so large that initial setup takes forever
- Realistic for SMB/mid-market scenarios

### CPU Work Duration
**Decision**: Configurable 50-500ms CPU time per recommendation  
**Rationale**:
- Long enough to be visible in profilers
- Short enough for reasonable test execution
- Configurable to test different scenarios
- Realistic for complex calculations

## Technology Choices

### Java 21
- Latest LTS version
- Modern language features
- Good profiling tool support
- Represents current enterprise adoption

### Spring Boot 3.2.2
- Industry-standard framework
- Rich ecosystem for profiling
- Actuator for metrics
- Well-understood by developers

### PostgreSQL 17.8
- Mature, production-ready database
- Good performance characteristics
- Complex query support
- Realistic for microservices

### Gradle 8.5
- Modern build tool
- Multi-module support
- Faster than Maven for builds
- Good IDE integration

### Docker Compose
- Easy deployment
- Consistent environment
- Good for demos
- Resource limit controls

## Key Implementation Patterns

### Layered Architecture
```
Controller Layer (REST endpoints)
  └─> Service Layer (business logic)
      └─> Repository Layer (data access)
          └─> Database
```

### Synchronous Communication
- RestTemplate for HTTP calls (not WebClient)
- JDBC for database (via JPA)
- No async/reactive patterns
- Clearer profiling data

### Metrics Collection
- Spring Boot Actuator
- Prometheus integration
- Custom metrics where needed
- VictoriaMetrics for storage

## Data Model

### Customer Types
1. **Regular Customers**: 1-3 buildings, 1-10 devices per building
2. **Large Customers**: 50-200 buildings, 20-100 devices per building

### Device Types
- SENSOR, ACTUATOR, CONTROLLER, CAMERA, THERMOSTAT, SMART_LOCK
- Randomly distributed across buildings
- Various manufacturers and models
- Power consumption tracking

## Load Generation Strategy

### Three-Phase Pattern
1. **Warmup**: 10% load to warm up JVM
2. **Pause**: Brief stabilization period
3. **Measurement**: Full load for profiling

This pattern ensures:
- JIT compilation completes
- Class loading stabilizes
- Connection pools reach steady state
- Metrics are representative

## Profiling Integration Points

### IO Service
- **Database Queries**: Query performance, N+1 problems, connection pooling
- **HTTP Clients**: Network latency, connection management
- **Transactions**: Transaction boundaries, locking
- **Object Mapping**: JPA entity mapping overhead

### CPU Service
- **Hotspots**: Most CPU-intensive methods
- **Algorithms**: Matrix operations, recursive functions, prime checks
- **Memory**: Object allocation patterns, GC pressure
- **Call Stacks**: Method hierarchy visualization

### Load Generator
- **Concurrency**: Thread pool behavior, thread contention
- **Distribution**: Request distribution patterns
- **Latency**: End-to-end latency tracking
- **Errors**: Failure patterns and recovery

## Build Process

### Multi-Stage Docker Builds
1. **Builder Stage**: Gradle build with JDK 21
2. **Runtime Stage**: JRE 21 (Alpine) for smaller images
3. **Layers**: Efficient layer caching for faster rebuilds

### Gradle Multi-Module
- Root project with shared configuration
- Three sub-modules (io-service, cpu-service, load-generator)
- Common dependencies in root build.gradle
- Module-specific dependencies in sub-modules

## Database Initialization

### Liquibase Migration Strategy
1. **Schema Creation**: DDL for tables, indexes, foreign keys
2. **Regular Customers**: Bulk insert with generate_series
3. **Large Customers**: Separate changeset for visibility

### Data Generation
- PostgreSQL generate_series for bulk data
- Random values within realistic ranges
- Proper foreign key relationships
- Indexed columns for query performance

## Running the System

### Startup Order (Handled by depends_on)
1. PostgreSQL (wait for healthy)
2. CPU Service (standalone, wait for healthy)
3. IO Service (wait for PostgreSQL + CPU service)
4. Load Generator (wait for IO service)

### Health Checks
- All services expose /actuator/health
- Docker health checks configured
- Startup grace period (40-60s)
- Retry logic (3 retries)

## Testing Strategy

### Manual Testing
- Direct curl calls to services
- Health check verification
- Single recommendation requests
- Small load tests

### Automated Testing
- Docker Compose health checks
- Service startup verification
- End-to-end flow testing
- Load test execution

## Monitoring & Observability

### Metrics Stack
- **Prometheus**: Scrapes metrics from all services
- **VictoriaMetrics**: Long-term metrics storage
- **Grafana**: Visualization (optional setup)

### Exposed Metrics
- JVM metrics (heap, GC, threads, classes)
- HTTP metrics (requests, latency, errors)
- Database metrics (connection pool, query time)
- Custom application metrics

## Common Issues & Solutions

### Port Conflicts
**Issue**: Port 8080 already in use  
**Solution**: Stop conflicting service or change port in docker-compose.yaml

### Database Connection
**Issue**: IO service can't connect to PostgreSQL  
**Solution**: Wait for PostgreSQL health check, check network connectivity

### Build Failures
**Issue**: Gradle build fails in Docker  
**Solution**: Check Java version, verify Gradle wrapper, review error logs

### Memory Issues
**Issue**: Services OOM in Docker  
**Solution**: Increase Docker memory limits, tune JVM heap settings

## Extension Points

### Adding New Services
1. Create new Gradle sub-module
2. Add Dockerfile
3. Add to docker-compose.yaml
4. Configure prometheus scraping
5. Update documentation

### Modifying Load Patterns
1. Update LoadGeneratorService
2. Add new load pattern types
3. Configure via request parameters
4. Document new patterns

### Database Schema Changes
1. Create new Liquibase changeset
2. Test with existing data
3. Update JPA entities
4. Update repository queries

## AI Agent Context

### When Resuming This Project
1. **Architecture**: Three Java microservices (IO, CPU, Load Generator)
2. **Threading**: Traditional model, NO virtual threads
3. **Call Depth**: 5 levels in each service for profiling visibility
4. **Database**: PostgreSQL with 100K customers via Liquibase
5. **Build**: Gradle multi-module with Docker Compose deployment
6. **Metrics**: Prometheus/VictoriaMetrics integration

### Key Files to Check
- `/docker-compose.yaml`: Service definitions
- `/ARCHITECTURE.md`: Detailed architecture documentation
- `/{service}/src/main/java/com/profiler/{service}/`: Source code
- `/{service}/Dockerfile`: Container builds
- `/build.gradle`: Root build configuration
- `/settings.gradle`: Multi-module structure

### Making Changes
- **Code Changes**: Edit in `{service}/src/main/java/`
- **Config Changes**: Edit `{service}/src/main/resources/application.yml`
- **Database Changes**: Add Liquibase changeset in `io-service/src/main/resources/db/changelog/`
- **Dependencies**: Update `build.gradle` (root or service-level)
- **Deployment**: Modify `docker-compose.yaml`

### Testing Changes
```bash
# Rebuild specific service
docker-compose up --build -d <service-name>

# View logs
docker-compose logs -f <service-name>

# Test endpoint
curl http://localhost:<port>/actuator/health
```

### Important Constraints
- ❌ NO virtual threads (use traditional threading)
- ❌ NO async/reactive patterns (use synchronous)
- ✅ 5-level call depth required
- ✅ CPU work must be actual computation (not sleep)
- ✅ All services must expose Prometheus metrics
- ✅ Follow Spring Boot best practices

## Project Goals Achievement

### ✅ Profiler Showcase
- Realistic workloads with IO and CPU patterns
- Clear method hierarchies visible in profilers
- Proper threading model for profiler analysis
- Metrics integration for correlation

### ✅ Best Practices
- Layered architecture (Controller → Service → Repository)
- Dependency injection via Spring
- Transaction management
- Error handling
- Configuration externalization
- Health checks and monitoring

### ✅ Scalability
- Docker Compose for easy deployment
- Resource limits configured
- Database connection pooling
- Configurable concurrency
- Health checks for orchestration

### ✅ Maintainability
- Clear code organization
- Comprehensive documentation
- Configuration over hard-coding
- Standard Spring Boot patterns
- Well-defined interfaces

## Quick Reference

### Service Ports
- IO Service: 8080
- CPU Service: 8081
- Load Generator: 8082
- PostgreSQL: 15432 (mapped from 5432)
- Prometheus: 9090
- VictoriaMetrics: 8428
- Grafana: 3000

### Key Commands
```bash
# Start everything
docker-compose up --build -d

# Check status
docker ps

# View logs
docker-compose logs -f io-service

# Test service
curl http://localhost:8080/actuator/health

# Run load test
curl -X POST http://localhost:8082/api/load/start \
  -H "Content-Type: application/json" \
  -d '{"customerIds":[1,2,3],"parallelRequests":5,"warmupSeconds":10,"pauseSeconds":5,"measurementSeconds":30}'

# Stop everything
docker-compose down
```

### Gradle Commands
```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :io-service:bootJar

# Clean build
./gradlew clean build
```

## Success Criteria

The project successfully demonstrates profiler capabilities if:
1. ✅ All three services start and pass health checks
2. ✅ Database populates with 100K+ customers
3. ✅ Recommendation endpoint returns results
4. ✅ CPU service consumes 50-500ms per request
5. ✅ Load generator successfully creates load
6. ✅ Profilers can attach and collect data
7. ✅ Method call depth visible in profilers
8. ✅ Metrics available in Prometheus

## Version History

**v1.0** (Initial Release)
- Three microservices (IO, CPU, Load Generator)
- PostgreSQL with 100K customers
- Liquibase migrations
- Docker Compose deployment
- Prometheus metrics
- 5-level call depth
- Traditional threading model
- Comprehensive documentation
