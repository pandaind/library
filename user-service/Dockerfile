# Multi-stage build for user-service
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom first for better layer caching
COPY pom.xml .
COPY user-service/pom.xml ./user-service/

# Copy proto files for code generation
COPY user-service/src/main/proto ./user-service/src/main/proto/

# Download dependencies
RUN mvn dependency:go-offline -f user-service/pom.xml

# Copy source code
COPY user-service/src ./user-service/src/

# Build the application
RUN mvn clean package -f user-service/pom.xml -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S userservice && \
    adduser -S userservice -u 1001

# Copy the jar file from build stage
COPY --from=build /app/user-service/target/user-service-*.jar app.jar

# Change ownership to non-root user
RUN chown userservice:userservice app.jar

USER userservice

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]