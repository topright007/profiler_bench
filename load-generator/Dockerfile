# Simple Dockerfile for Load Generator
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy pre-built jar (from build context root)
COPY build/libs/*.jar app.jar

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
