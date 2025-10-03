# Multi-stage build for api-gateway
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom first for better layer caching
COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/

# Copy proto files for code generation
COPY api-gateway/src/main/proto ./api-gateway/src/main/proto/

# Download dependencies
RUN mvn dependency:go-offline -f api-gateway/pom.xml

# Copy source code
COPY api-gateway/src ./api-gateway/src/

# Build the application
RUN mvn clean package -f api-gateway/pom.xml -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S apigateway && \
    adduser -S apigateway -u 1001

# Copy the jar file from build stage
COPY --from=build /app/api-gateway/target/api-gateway-*.jar app.jar

# Change ownership to non-root user
RUN chown apigateway:apigateway app.jar

USER apigateway

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]