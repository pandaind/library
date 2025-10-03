# Library Management System - Learning Project

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![GraphQL](https://img.shields.io/badge/GraphQL-Java-e10098.svg)](https://graphql.org/)
[![gRPC](https://img.shields.io/badge/gRPC-Java-4285F4.svg)](https://grpc.io/)
[![Docker](https://img.shields.io/badge/Docker-Latest-blue.svg)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791.svg)](https://www.postgresql.org/)

A hands-on learning project demonstrating **GraphQL** and **gRPC** implementation using **Spring Boot**. This microservices-based Library Management System showcases modern API development patterns and inter-service communication.

## Learning Objectives

This project demonstrates:

- **GraphQL API Development** - Query language implementation with Spring Boot
- **gRPC Inter-Service Communication** - High-performance RPC framework
- **Microservices Architecture** - Service decomposition and communication patterns
- **Container Orchestration** - Docker and Docker Compose deployment
- **API Testing Strategies** - Postman collections and Newman automation
- **Service Monitoring** - Health checks and application metrics
- **Database Integration** - JPA/Hibernate with PostgreSQL

## Quick Start

### Prerequisites

- **Docker & Docker Compose** - For containerized deployment
- **Java 17+** & **Maven 3.9+** - For local development  
- **Newman CLI** - For automated API testing (optional)

### Start the System

```bash
# Start all services
docker-compose up -d

# Wait for services to initialize (30 seconds)
sleep 30

# Run validation tests
./quick-test.sh test
```

### Access Points

- **GraphQL API**: <http://localhost:8080/graphql>
- **Health Check**: <http://localhost:8080/actuator/health>
- **Book Service**: <http://localhost:8081>
- **User Service**: <http://localhost:8082>
- **Database**: localhost:5433

## System Architecture

### Microservices

- **API Gateway** (Port 8080) - GraphQL interface, service orchestration
- **User Service** (Port 8082/9093) - User management, authentication
- **Book Service** (Port 8081/9091) - Book catalog, inventory management
- **PostgreSQL** (Port 5433) - Data persistence

### Communication

- **HTTP/GraphQL** - Client to API Gateway
- **gRPC** - Inter-service communication
- **PostgreSQL** - Database connections

## Testing

### Automated Testing with Newman

```bash
# Run complete test suite
newman run Library-Management-System.postman_collection.json \
  -e Library-Management-System.postman_environment.json
```

### Manual Testing with Postman

1. Import `Library-Management-System.postman_collection.json`
2. Import `Library-Management-System.postman_environment.json`
3. Set environment to "Docker Environment"
4. Run individual requests or entire collection

## Sample API Operations

### User Registration

```graphql
mutation RegisterUser {
  registerUser(userInput: {
    username: "johndoe"
    email: "john@example.com"
    firstName: "John"
    lastName: "Doe"
    phone: "+1234567890"
    address: "123 Main St, City, State"
  }) {
    id
    username
    email
    firstName
    lastName
    phone
    address
    membershipType
    status
    registrationDate
  }
}
```

### Book Borrowing

```graphql
mutation BorrowBook {
  borrowBook(borrowInput: {
    userId: 1
    bookId: 1
    dueDays: 14
  }) {
    id
    borrowDate
    dueDate
    status
    user { username }
    book { title author }
  }
}
```

### Browse Books

```graphql
query GetAllBooks {
  getAllBooks {
    id
    title
    author
    isbn
    genre
    publishedYear
    totalCopies
    availableCopies
    description
  }
}
```

## System Status

### Working Operations

- **User Management**: Registration, profile management
- **Book Catalog**: Browse, search, filter books
- **Borrowing System**: Book checkout, return tracking
- **Inventory**: Real-time availability updates
- **Health Monitoring**: Service status checks

### Performance

- **Response Time**: < 250ms average
- **Newman Tests**: 14/14 passing (100% success rate)
- **Error Handling**: Comprehensive validation and error responses
- **Data Consistency**: ACID transactions with PostgreSQL

## Development

### Build Services

```bash
# Build all services
mvn clean install

# Build individual service
cd api-gateway && mvn clean install
cd book-service && mvn clean install  
cd user-service && mvn clean install
```

### Run Locally

```bash
# Start PostgreSQL
docker run --name postgres -e POSTGRES_DB=library_db \
  -e POSTGRES_USER=library_user -e POSTGRES_PASSWORD=library_password \
  -p 5432:5432 -d postgres:15-alpine

# Run services individually
cd user-service && mvn spring-boot:run
cd book-service && mvn spring-boot:run  
cd api-gateway && mvn spring-boot:run
```

## Project Structure

```
library/
├── Documentation
│   ├── README.md                     # This file - project overview
│   ├── PROJECT_STRUCTURE_GUIDE.md    # Detailed architecture guide
│   └── POSTMAN_TESTING_GUIDE.md     # API testing instructions
├── Testing & Automation
│   ├── Library-Management-System.postman_collection.json  # API test collection
│   ├── Library-Management-System.postman_environment.json # Test environment
│   └── quick-test.sh                # Automated validation script
├── Deployment Configuration
│   ├── docker-compose.yml           # Service orchestration
│   ├── .dockerignore               # Docker build optimization
│   └── .gitignore                  # Git version control exclusions
├── api-gateway/                     # GraphQL API Gateway Service
│   ├── src/main/java/com/library/apigateway/
│   │   ├── config/                 # GraphQL & gRPC client configuration
│   │   ├── resolver/               # GraphQL query/mutation resolvers
│   │   ├── dto/                    # Data transfer objects
│   │   ├── mapper/                 # Entity-DTO mapping
│   │   ├── exception/              # Custom exception handling
│   │   └── validation/             # Input validation logic
│   ├── src/main/proto/             # Protocol buffer definitions
│   ├── src/main/resources/graphql/ # GraphQL schema files
│   └── Dockerfile                  # Container build instructions
├── book-service/                    # Book Management Microservice
│   ├── src/main/java/com/library/bookservice/
│   │   ├── entity/                 # JPA entities (Book, BorrowRecord)
│   │   ├── repository/             # Data access repositories
│   │   ├── service/                # Business logic services
│   │   ├── config/                 # Database & gRPC configuration
│   │   ├── exception/              # Domain-specific exceptions
│   │   └── interceptor/            # gRPC interceptors
│   ├── src/main/proto/             # gRPC service definitions
│   └── src/main/resources/         # Application configuration
├── user-service/                    # User Management Microservice
│   ├── src/main/java/com/library/userservice/
│   │   ├── entity/                 # User domain entities
│   │   ├── repository/             # User data repositories
│   │   ├── service/                # User business services
│   │   ├── config/                 # Service configuration
│   │   ├── exception/              # User-specific exceptions
│   │   ├── validation/             # User input validation
│   │   └── interceptor/            # Request interceptors
│   └── src/main/proto/             # User service contracts
└── init-db/                        # Database Initialization
    └── init.sql                    # PostgreSQL schema & sample data
```

## Learning Resources

- **[Project Structure Guide](PROJECT_STRUCTURE_GUIDE.md)** - Detailed architecture and codebase walkthrough
- **[Postman Testing Guide](POSTMAN_TESTING_GUIDE.md)** - Complete API testing tutorials

## Key Technologies Demonstrated

- **Spring Boot 3.2.0** - Modern Java application framework
- **GraphQL Java** - Type-safe API query language implementation
- **gRPC** - High-performance RPC framework for service communication
- **JPA/Hibernate** - Object-relational mapping with PostgreSQL
- **Docker Compose** - Multi-container application orchestration
- **Protocol Buffers** - Efficient data serialization for gRPC
- **Postman/Newman** - API testing and automation

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Learning project for GraphQL & gRPC with Spring Boot**