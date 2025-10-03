# Multi-stage build for book-service
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom first for better layer caching
COPY pom.xml .
COPY book-service/pom.xml ./book-service/

# Copy proto files for code generation
COPY book-service/src/main/proto ./book-service/src/main/proto/

# Download dependencies
RUN mvn dependency:go-offline -f book-service/pom.xml

# Copy source code
COPY book-service/src ./book-service/src/

# Build the application
RUN mvn clean package -f book-service/pom.xml -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S bookservice && \
    adduser -S bookservice -u 1001

# Copy the jar file from build stage
COPY --from=build /app/book-service/target/book-service-*.jar app.jar

# Change ownership to non-root user
RUN chown bookservice:bookservice app.jar

USER bookservice

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]