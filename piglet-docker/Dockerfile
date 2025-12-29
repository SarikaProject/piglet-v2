# Multi-stage build for PigletV2
FROM eclipse-temurin:11-jdk AS builder

LABEL maintainer="PigletV2 Team"
LABEL description="PigletV2 QA Framework Docker Container"

WORKDIR /build

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy pom.xml and resolve dependencies
COPY pom.xml .
RUN mvn dependency:resolve

# Copy source code
COPY src ./src

# Build the project (without running tests yet)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:11-jdk

WORKDIR /piglet

# Install Maven and Python (for HTTP server)
RUN apt-get update && apt-get install -y maven python3 && rm -rf /var/lib/apt/lists/*

# Create directories
RUN mkdir -p /piglet/resources/beanshell \
    && mkdir -p /piglet/resources/sparql \
    && mkdir -p /piglet/resources/data \
    && mkdir -p /piglet/output \
    && mkdir -p /piglet/reports

# Copy project files from builder
COPY --from=builder /build/pom.xml /piglet/
COPY --from=builder /build/target /piglet/target
COPY --from=builder /build/src /piglet/src

# Copy resources
COPY src/main/resources /piglet/resources/

# Environment
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV PIGLET_HOME=/piglet

# Expose port for web server
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD java -version || exit 1

# Run tests, generate report, then serve it via HTTP
CMD ["sh", "-c", "java -jar target/piglet-example.jar && echo '\n========================================' && echo 'Report ready at: http://localhost:8080/piglet-test-report.html' && echo '========================================\n' && python3 -m http.server 8080 --directory /piglet/output"]