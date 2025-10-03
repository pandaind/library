# Library Management System - Architecture & Learning Guide

## Overview

This guide provides a compre├── book-service/                               # Book Management Microservice
│   ├── Dockerfile               └── init-db/                         # Database Initialization              # Container configuration
│   ├── pom.xml                                 # Maven dependencies
│   └── src/main/
│       ├── java/com/library/bookservice/
│       │   ├── entity/                         # JPA entities (Book, BorrowRecord)
│       │   ├── repository/                     # Spring Data repositories
│       │   ├── service/                        # Business logic services
│       │   ├── config/                         # Database & gRPC configuration
│       │   ├── exception/                      # Domain-specific exceptions
│       │   └── interceptor/                    # gRPC interceptors
│       ├── proto/                              # gRPC service definitions
│       └── resources/                          # Application properties & datadown of the Library Management System used for learning **GraphQL** and **gRPC** with **Spring Boot**. It covers the verified architecture, actual codebase organization, and key implementation patterns for educational purposes.

## System Architecture

### High-Level Architecture
```
┌─────────────────┐    GraphQL     ┌──────────────────┐
│   Frontend      │◄──────────────►│   API Gateway    │
│   (Web/Mobile)  │                │  (GraphQL API)   │
└─────────────────┘                └──────────────────┘
                                             │
                                    ┌────────┴────────┐
                                    │                 │
                            gRPC    ▼                 ▼    gRPC
                        ┌─────────────────┐   ┌─────────────────┐
                        │  Book Service   │   │  User Service   │
                        │ (Spring Boot)   │   │ (Spring Boot)   │
                        └─────────────────┘   └─────────────────┘
                                    │                 │
                                    └────────┬────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │   PostgreSQL    │
                                    │   Database      │
                                    └─────────────────┘
```

### Service Breakdown

| Service | Port | Protocol | Purpose | Technology Stack |
|---------|------|----------|---------|------------------|
| **API Gateway** | 8080 | HTTP/GraphQL | Main entry point, GraphQL API | Spring Boot + GraphQL Java |
| **Book Service** | 8081/9091 | HTTP/gRPC | Book management, inventory | Spring Boot + gRPC + JPA |
| **User Service** | 8082/9093 | HTTP/gRPC | User management, authentication | Spring Boot + gRPC + JPA |
| **PostgreSQL** | 5433 | TCP | Data persistence | PostgreSQL 15-alpine |

## Verified Project Structure

**Root Directory Structure (Learning Project)**

```text
library/
├──  Documentation & Guides
│   ├── README.md                                    # Main project overview
│   ├── PROJECT_STRUCTURE_GUIDE.md                  # This architecture guide
│   ├── POSTMAN_TESTING_GUIDE.md                   # API testing tutorial
│   └── docker-README.md                           # Container deployment guide
│
├── Testing & Validation
│   ├── Library-Management-System.postman_collection.json    # Complete API test suite
│   ├── Library-Management-System.postman_environment.json   # Test environment config
│   ├── quick-test.sh                              # Automated validation script
│   ├── test-graphql.sh                           # GraphQL-specific tests
│   └── test-n1-optimization.sh                   # Performance testing
│
├── Deployment & Configuration
│   ├── docker-compose.yml                        # Multi-service orchestration
│   ├── .dockerignore                            # Docker build exclusions
│   ├── .gitignore                               # Git version control exclusions
│   ├── LICENSE                                  # MIT license
│   ├── pom.xml                                  # Root Maven configuration
│   ├── start-services.sh                       # Service startup automation
│   └── stop-services.sh                        # Service shutdown automation
│
├── api-gateway/                                 # GraphQL API Gateway Service
│
├──  Deployment & Configuration
│   ├── docker-compose.yml                        # Multi-service orchestration
│   ├── .dockerignore                            # Docker build exclusions
│   ├── .gitignore                               # Git version control exclusions
│   ├── LICENSE                                  # MIT license
│   ├── pom.xml                                  # Root Maven configuration
│   ├── start-services.sh                       # Service startup automation
│   └── stop-services.sh                        # Service shutdown automation
│
├──  api-gateway/                               # GraphQL API Gateway Service
│   ├── Dockerfile                               # Container build instructions
│   ├── pom.xml                                  # Service-specific dependencies
│   └── src/
│       ├── main/
│       │   ├── java/com/library/apigateway/
│       │   │   ├── config/                      # Spring & GraphQL configuration
│       │   │   ├── resolver/                    # GraphQL query/mutation resolvers  
│       │   │   ├── dto/                         # Data transfer objects
│       │   │   ├── mapper/                      # Entity-DTO mapping utilities
│       │   │   ├── exception/                   # Custom exception handling
│       │   │   ├── validation/                  # Input validation logic
│       │   │   └── enums/                       # Enumeration types
│       │   ├── proto/                           # Protocol buffer definitions
│       │   └── resources/
│       │       ├── graphql/                     # GraphQL schema files (.graphqls)
│       │       ├── application.yml              # Default configuration
│       │       └── application-docker.yml       # Docker environment config
│       └── test/
│           ├── java/com/library/apigateway/config/ # Configuration tests
│           └── resources/graphql/               # Test GraphQL schemas
│
├──  book-service/                             # Book Management Microservice
│   ├── Dockerfile                              # Container configuration
│   ├── pom.xml                                 # Maven dependencies
│   └── src/main/
│       ├── java/com/library/bookservice/
│       │   ├── entity/                         #  JPA entities (Book, BorrowRecord)
│       │   ├── repository/                     #  Spring Data repositories
│       │   ├── service/                        #  Business logic services
│       │   ├── config/                         #  Database & gRPC configuration
│       │   ├── exception/                      #  Domain-specific exceptions
│       │   └── interceptor/                    #  gRPC interceptors
│       ├── proto/                              #  gRPC service definitions
│       └── resources/                          #  Application properties & data
│
├──  user-service/                            # User Management Microservice  
│   ├── Dockerfile                             # Container setup
│   ├── pom.xml                                # Service dependencies
│   └── src/main/
│       ├── java/com/library/userservice/
│       │   ├── entity/                        #  User domain entities
│       │   ├── repository/                    #  User data repositories
│       │   ├── service/                       #  User business services
│       │   ├── config/                        #  Service configuration
│       │   ├── exception/                     #  User-specific exceptions
│       │   ├── validation/                    #  User input validation
│       │   └── interceptor/                   #  Request interceptors
│       ├── proto/                             #  User service contracts
│       └── resources/                         #  Configuration files
│
└──  init-db/                               # Database Initialization
    └── init.sql                               #  PostgreSQL schema & sample data
│
├──  book-service/                  # Book Management Microservice
│   ├──  Dockerfile
│   ├──  pom.xml
│   ├── 📂 src/main/
│   │   ├── 📂 java/com/library/bookservice/
│   │   │   ├── 📂 entity/           # JPA Entities
│   │   │   │   ├── Book.java        # Book entity with relationships
│   │   │   │   ├── BorrowRecord.java # Borrowing transaction entity
│   │   │   │   └── Genre.java       # Book categorization
│   │   │   ├── 📂 repository/       # Data Access Layer
│   │   │   │   ├── BookRepository.java      # JPA repository
│   │   │   │   ├── BorrowRecordRepository.java
│   │   │   │   └── GenreRepository.java
│   │   │   ├── 📂 service/          # Business Logic Layer
│   │   │   │   ├── BookService.java         # Core book operations
│   │   │   │   ├── BorrowService.java       # Borrowing logic
│   │   │   │   └── InventoryService.java    # Stock management
│   │   │   ├── 📂 grpc/            # gRPC Service Implementation
│   │   │   │   └── BookGrpcService.java     # gRPC server
│   │   │   ├── 📂 exception/       # Error Handling
│   │   │   │   ├── BookNotFoundException.java
│   │   │   │   └── InsufficientStockException.java
│   │   │   ├── 📂 interceptor/     # gRPC Interceptors
│   │   │   │   ├── LoggingInterceptor.java  # Request logging
│   │   │   │   └── ValidationInterceptor.java # Input validation
│   │   │   └── 📂 config/          # Configuration
│   │   │       ├── DatabaseConfig.java      # JPA configuration
│   │   │       └── GrpcServerConfig.java    # gRPC server setup
│   │   ├── 📂 proto/               # Protocol Buffers
│   │   │   └── book.proto          # Service contract definition
│   │   └── 📂 resources/
│   │       ├── data.sql            # Sample data initialization
│   │       ├── application.yml     # Default configuration
│   │       └── application-docker.yml # Docker environment
│   └── 📂 target/                  # Build artifacts
│
├── user-service/                    # User Management Microservice
│   ├──  Dockerfile
│   ├──  pom.xml
│   ├── 📂 src/main/
│   │   ├── 📂 java/com/library/userservice/
│   │   │   ├── 📂 entity/          # User Domain Entities
│   │   │   │   ├── User.java       # User entity with validations
│   │   │   │   ├── MembershipType.java # Enum for membership levels
│   │   │   │   └── UserStatus.java # Enum for user states
│   │   │   ├── 📂 repository/      # Data Persistence
│   │   │   │   └── UserRepository.java     # JPA repository with custom queries
│   │   │   ├── 📂 service/         # Business Services
│   │   │   │   ├── UserService.java        # Core user operations
│   │   │   │   ├── AuthenticationService.java # User authentication
│   │   │   │   └── MembershipService.java  # Membership management
│   │   │   ├── 📂 grpc/           # gRPC Implementation
│   │   │   │   └── UserGrpcService.java    # gRPC service endpoints
│   │   │   ├── 📂 exception/      # Domain Exceptions
│   │   │   │   ├── UserNotFoundException.java
│   │   │   │   ├── DuplicateUserException.java
│   │   │   │   └── InvalidMembershipException.java
│   │   │   └── 📂 config/         # Service Configuration
│   │   │       ├── DatabaseConfig.java
│   │   │       └── GrpcServerConfig.java
│   │   ├── 📂 proto/              # Protocol Buffer Schema
│   │   │   └── user.proto         # User service contract
│   │   └── 📂 resources/
│   │       ├── data.sql           # Initial user data
│   │       ├── application.yml
│   │       └── application-docker.yml
│   └── 📂 target/
│
├──  init-db/                      # Database Initialization
│   └── init.sql                    # PostgreSQL schema and initial data
│
├──  Docker Configuration Files
│   ├── docker-compose.yml          # Multi-service orchestration
│   ├── .dockerignore               # Docker build exclusions
│   └── docker-README.md            # Docker-specific documentation
│
├── Testing & Automation Scripts
│   ├── start-services.sh           # Service startup automation
│   ├── stop-services.sh            # Graceful service shutdown
│   ├── test-graphql.sh             # Comprehensive API testing
│   ├── test-n1-optimization.sh     # Performance testing
│   └── quick-test.sh               # Quick validation script
│
├──  Testing Collections
│   ├── Library-Management-System.postman_collection.json    # Newman/Postman tests
│   └── Library-Management-System.postman_environment.json   # Test environment config
│
├──  Documentation
│   ├── README.md                   # Main project documentation
│   ├── POSTMAN_TESTING_GUIDE.md    # Testing instructions
│   ├── COMPLETE_SUCCESS_REPORT.md  # Implementation report
│   ├── CLEANUP_SUMMARY.md          # File organization summary
│   └── PROJECT_STRUCTURE_GUIDE.md  # This file
│
├── Build Configuration
│   └── pom.xml                     # Root Maven configuration
```

## Technology Stack Deep Dive

### Core Technologies

#### Backend Framework
- **Spring Boot 3.2.0**: Main application framework
- **Spring Data JPA**: Database abstraction layer
- **Spring Boot Actuator**: Production-ready monitoring
- **Spring Boot Validation**: JSR-303 input validation

#### API & Communication
- **GraphQL Java**: GraphQL API implementation
- **gRPC**: High-performance inter-service communication
- **Protocol Buffers**: Efficient data serialization

#### Database & Persistence
- **PostgreSQL 15**: Primary database
- **Hibernate**: ORM implementation
- **Flyway**: Database migration management (optional)

#### Containerization & Deployment
- **Docker**: Application containerization
- **Docker Compose**: Multi-service orchestration
- **Alpine Linux**: Lightweight container base images

#### Testing & Quality
- **JUnit 5**: Unit testing framework
- **Testcontainers**: Integration testing with real databases
- **Newman**: API testing automation
- **Postman**: Manual API testing

##  Architectural Patterns

### Design Patterns Used

#### 1. **Microservices Architecture**
- **Domain Separation**: Each service owns its domain (Books, Users)
- **Independent Deployment**: Services can be deployed separately
- **Technology Diversity**: Each service can use different technologies

#### 2. **API Gateway Pattern**
- **Single Entry Point**: All client requests go through API Gateway
- **Service Orchestration**: Gateway coordinates multiple service calls
- **Cross-Cutting Concerns**: Authentication, logging, rate limiting

#### 3. **Repository Pattern**
- **Data Access Abstraction**: Clean separation between business logic and data
- **Testability**: Easy to mock repositories for unit testing
- **Query Centralization**: Database queries centralized in repository layer

#### 4. **DTO (Data Transfer Object) Pattern**
- **API Contract Stability**: Internal changes don't affect API clients
- **Data Transformation**: Clean mapping between internal and external representations
- **Validation Layer**: Input validation at DTO level

### Communication Patterns

#### 1. **GraphQL for Client Communication**
- **Single Endpoint**: `/graphql` for all client operations
- **Flexible Queries**: Clients request exactly what they need
- **Type Safety**: Strong typing with schema validation

#### 2. **gRPC for Service-to-Service Communication**
- **High Performance**: Binary protocol with HTTP/2
- **Strong Contracts**: Protocol buffer schemas ensure compatibility
- **Streaming Support**: Real-time data streaming capabilities

#### 3. **Event-Driven Architecture (Future)**
- **Async Processing**: Non-blocking operations
- **Loose Coupling**: Services communicate through events
- **Scalability**: Better handling of high-throughput scenarios

## 🗃 Database Design

### Schema Overview

#### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    membership_type VARCHAR(20) DEFAULT 'BASIC',
    registration_date TIMESTAMP DEFAULT NOW(),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

#### Books Table
```sql
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(17) UNIQUE,
    genre VARCHAR(50),
    published_year INTEGER,
    total_copies INTEGER DEFAULT 1,
    available_copies INTEGER DEFAULT 1,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

#### Borrow Records Table
```sql
CREATE TABLE borrow_records (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT REFERENCES books(id),
    user_id BIGINT REFERENCES users(id),
    borrow_date TIMESTAMP DEFAULT NOW(),
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'BORROWED',
    fine_amount DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### Indexing Strategy
```sql
-- Performance optimization indexes
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_genre ON books(genre);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_borrow_records_user_id ON borrow_records(user_id);
CREATE INDEX idx_borrow_records_book_id ON borrow_records(book_id);
CREATE INDEX idx_borrow_records_status ON borrow_records(status);
```

## 🔌 Configuration Management

### Environment-Specific Configurations

#### Development Profile (`application.yml`)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  h2:
    console:
      enabled: true

logging:
  level:
    com.library: DEBUG
    org.springframework.web: DEBUG
```

#### Docker Profile (`application-docker.yml`)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/library_db
    username: library_user
    password: library_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    database-platform: org.hibernate.dialect.PostgreSQLDialect

grpc:
  server:
    port: 9091
  client:
    book-service:
      address: static://book-service:9091
      negotiationType: plaintext
    user-service:
      address: static://user-service:9093
      negotiationType: plaintext

logging:
  level:
    root: INFO
    com.library: DEBUG
```

#### Production Profile (`application-prod.yml`)
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    root: WARN
    com.library: INFO
  file:
    name: /app/logs/application.log
```

## Monitoring & Observability

### Health Checks
```yaml
management:
  endpoint:
    health:
      show-details: always
  health:
    db:
      enabled: true
    diskspace:
      enabled: true
```

### Metrics Collection
- **Micrometer**: Application metrics
- **Prometheus**: Metrics storage (configurable)
- **Custom Metrics**: Business-specific measurements

### Logging Strategy
- **Structured Logging**: JSON format for production
- **Correlation IDs**: Request tracing across services
- **Log Levels**: Environment-appropriate verbosity

## API Testing Structure (Postman/Newman)

### Testing Organization

The project includes a comprehensive **Postman collection** specifically designed for learning GraphQL API testing patterns:

#### **Collection Structure**

```text
Library-Management-System.postman_collection.json
├──  Health & System - WORKING
│   ├── Health Check                    # Service availability validation
│   └── Statistics - Total Books       # Basic GraphQL query example
├──  Book Management - CORRECTED  
│   └── Get All Books - FIXED          # Complete book catalog query
└──  User Management - CORRECTED
    └── Register New User - FIXED SCHEMA # User registration mutation
```

#### **Environment Configuration**

```json
// Library-Management-System.postman_environment.json
{
  "name": "Library Management Docker",
  "values": [
    {"key": "baseUrl", "value": "http://localhost:8080/graphql"},
    {"key": "healthUrl", "value": "http://localhost:8080/actuator/health"}
  ]
}
```

#### **GraphQL Learning Examples**

Each request demonstrates key GraphQL concepts:

1. **Health Check** - Basic HTTP endpoint testing
2. **Statistics Query** - Simple GraphQL aggregation
3. **Get All Books** - Complex nested GraphQL query
4. **User Registration** - GraphQL mutation with input validation

#### **Newman Automation**

```bash
# Run complete test suite (14 assertions verified)
newman run Library-Management-System.postman_collection.json \
  -e Library-Management-System.postman_environment.json

# Expected Results:  14/14 assertions passing
```

#### **Test Validation Scripts**

Each request includes **JavaScript test scripts** for learning:

```javascript
// Example validation pattern
pm.test("GraphQL response structure", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('data');
    pm.expect(responseJson.data).to.not.be.null;
});

pm.test("Book data validation", function () {
    const books = responseJson.data.getAllBooks;
    pm.expect(books).to.be.an('array');
    if (books.length > 0) {
        pm.expect(books[0]).to.have.property('title');
        pm.expect(books[0]).to.have.property('author');
    }
});
```

### **Testing Learning Path**

1. **Start with Health Check** - Understand basic API connectivity
2. **Explore GraphQL Queries** - Learn query structure and data fetching
3. **Practice Mutations** - Understand data modification patterns
4. **Examine Response Validation** - Learn testing best practices
5. **Run Newman Automation** - Understand CI/CD integration

## Deployment & Scaling

### Container Orchestration
```yaml
# docker-compose.yml highlights
version: '3.8'
services:
  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - book-service
      - user-service
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

### Scaling Considerations
- **Horizontal Scaling**: Multiple instances of each service
- **Load Balancing**: Distribute requests across instances
- **Database Connection Pooling**: Optimize database connections
- **Caching Strategy**: Redis for frequently accessed data

## Security Considerations

### Input Validation
- **Bean Validation**: JSR-303 annotations
- **GraphQL Schema Validation**: Type-level validation
- **Custom Validators**: Business rule validation

### Data Protection
- **SQL Injection Prevention**: Parameterized queries
- **XSS Protection**: Output encoding
- **CORS Configuration**: Controlled cross-origin access

### Container Security
- **Non-root Users**: Security best practices
- **Minimal Images**: Reduced attack surface
- **Network Isolation**: Service segmentation

This project structure provides an excellent learning foundation for understanding **GraphQL** and **gRPC** with **Spring Boot**, demonstrating modern microservices architecture patterns and API development best practices.