# Multi-stage Dockerfile for Backend Service
# Stage 1: Build stage (used in CI/CD pipeline)
FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app
COPY backend/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Install security updates and minimal packages
RUN apk update && \
    apk upgrade && \
    apk add --no-cache \
    curl \
    mysql-client \
    && rm -rf /var/cache/apk/*

WORKDIR /app

# Copy application layers for better caching
COPY --from=builder --chown=appuser:appgroup app/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup app/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup app/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup app/application/ ./

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
