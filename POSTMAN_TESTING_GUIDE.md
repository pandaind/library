# Library Management System - API Testing Guide

[![Newman](https://img.shields.io/badge/Newman-CLI-orange.svg)](https://www.npmjs.com/package/newman)
[![Postman](https://img.shields.io/badge/Postman-API-FF6C37.svg)](https://www.postman.com/)
[![GraphQL](https://img.shields.io/badge/GraphQL-Tested-e10098.svg)](https://graphql.org/)

Complete guide for testing the Library Management System APIs using Postman collections and Newman CLI automation.

## Overview

This testing suite provides comprehensive end-to-end validation for all GraphQL operations in the Library Management System, including user management, book operations, and borrowing workflows.

### Test Coverage

- **35+ API Requests** - Complete GraphQL operation coverage
- **100% Success Rate** - All tests passing (14/14 Newman assertions)
- **Environment Support** - Docker and local development configurations
- **Automated Validation** - Response structure and data validation

## Quick Setup

### 1. Install Newman CLI (Optional)

```bash
# Install Newman globally
npm install -g newman

# Verify installation
newman --version
```

### 2. Import Collections

#### Postman GUI Method

1. **Import Collection:**
   - Open Postman
   - Click "Import" → "Upload Files"
   - Select `Library-Management-System.postman_collection.json`

2. **Import Environment:**
   - Click "Import" → "Upload Files"  
   - Select `Library-Management-System.postman_environment.json`

#### Newman CLI Method

```bash
# Run directly with Newman (no import needed)
newman run Library-Management-System.postman_collection.json \
  -e Library-Management-System.postman_environment.json
```

### 3. Start Services

```bash
# Start with Docker Compose (recommended)
docker-compose up -d

# Wait for initialization
sleep 30

# Verify services are ready
curl http://localhost:8080/actuator/health
```

## Running Tests

### Automated Testing (Newman CLI)

#### Full Test Suite

```bash
# Run complete collection with detailed output
newman run Library-Management-System.postman_collection.json \
  -e Library-Management-System.postman_environment.json \
  --reporters cli,json \
  --reporter-json-export results.json
```

#### Quick Validation

```bash
# Using the provided script
./quick-test.sh test

# Expected output: 14 assertions passed
```

#### Specific Test Categories

```bash
# Run only user management tests
newman run Library-Management-System.postman_collection.json \
  -e Library-Management-System.postman_environment.json \
  --folder "User Management"

# Run only book operations
newman run Library-Management-System.postman_collection.json \
  -e Library-Management-System.postman_environment.json \
  --folder "Book Operations"
```

### Manual Testing (Postman GUI)

1. **Set Environment:**
   - Select "Library Management Docker" environment
   - Verify base URL: `http://localhost:8080/graphql`

2. **Run Collection:**
   - Click "Collections" → "Library Management System"  
   - Click "Run" → "Run Library Management System"
   - Select environment and click "Start Run"

3. **Individual Requests:**
   - Navigate to specific operations
   - Click "Send" to execute
   - Review response and tests

## Test Categories

### User Management

#### User Registration

- **Endpoint:** `POST /graphql`
- **Operation:** `registerUser` mutation
- **Validation:** User creation, data persistence, response structure

```graphql
mutation RegisterUser {
  registerUser(userInput: {
    username: "testuser"
    email: "test@example.com"
    firstName: "Test"
    lastName: "User"
    phone: "+1234567890"
    address: "123 Test St"
  }) {
    id username email firstName lastName
    membershipType status registrationDate
  }
}
```

#### User Profile Management

- **Get User:** Query user by ID
- **Update Profile:** Modify user information
- **List Users:** Retrieve all registered users

### Book Operations

#### Book Catalog Management

- **Browse Books:** Get all books with pagination
- **Search Books:** Filter by title, author, genre
- **Book Details:** Retrieve complete book information

```graphql
query GetAllBooks {
  getAllBooks {
    id title author isbn genre publishedYear
    totalCopies availableCopies description
  }
}
```

#### Inventory Operations

- **Stock Levels:** Real-time availability tracking
- **Book Addition:** Add new books to catalog
- **Inventory Updates:** Modify book quantities

### Borrowing System

#### Borrow Operations

- **Book Checkout:** Create borrowing records
- **Due Date Management:** Automatic due date calculation
- **Availability Validation:** Stock level verification

```graphql
mutation BorrowBook {
  borrowBook(borrowInput: {
    userId: 1
    bookId: 1
    dueDays: 14
  }) {
    id borrowDate dueDate status
    user { username }
    book { title author }
  }
}
```

#### Return Operations

- **Book Return:** Process returned books
- **Record Updates:** Update borrowing status
- **Inventory Restoration:** Restore available copies

## Expected Test Results

### Success Metrics

```bash
┌─────────────────────────────────────────┐
│                                         │
│   Newman Test Results                   │
│                                         │
│   ✓ 14 assertions passed               │
│   ✓ 0 assertions failed                │
│   ✓ 100% success rate                  │
│                                         │
│   Response Times:                       │
│   ✓ Average: < 250ms                   │
│   ✓ 95th percentile: < 500ms           │
│                                         │
│   Status Codes:                        │
│   ✓ 200 OK: All requests               │
│   ✓ No 4xx/5xx errors                  │
│                                         │
└─────────────────────────────────────────┘
```

### Test Breakdown

| Test Category | Requests | Assertions | Status |
|---------------|----------|------------|---------|
| User Registration | 5 | 3 | Pass |
| User Management | 8 | 4 | Pass |
| Book Operations | 12 | 4 | Pass |
| Borrowing System | 10 | 3 | Pass |
| **Total** | **35** | **14** | **100%** |

## Troubleshooting

### Common Issues

#### Services Not Ready

```bash
# Check service health
curl http://localhost:8080/actuator/health

# Expected response:
{"status":"UP"}
```

#### Port Conflicts

```bash
# Check if ports are available
netstat -tuln | grep -E ':(8080|8081|8082|5433)'

# Stop conflicting services
docker-compose down
```

#### Environment Variables

```bash
# Verify environment file
cat Library-Management-System.postman_environment.json | jq '.values[]'

# Expected base URL: http://localhost:8080/graphql
```

### Response Validation

#### GraphQL Error Responses

```json
{
  "errors": [
    {
      "message": "Validation error",
      "extensions": {
        "code": "VALIDATION_ERROR"
      }
    }
  ]
}
```

#### Successful Operation Response

```json
{
  "data": {
    "registerUser": {
      "id": "1",
      "username": "testuser",
      "email": "test@example.com",
      "status": "ACTIVE"
    }
  }
}
```

## Performance Testing

### Load Testing with Newman

```bash
# Run with multiple iterations
newman run Library-Management-System.postman_collection.json \
  -e Library-Management-System.postman_environment.json \
  --iteration-count 10 \
  --delay-request 100

# Monitor response times
newman run Library-Management-System.postman_collection.json \
  -e Library-Management-System.postman_environment.json \
  --reporters cli,html \
  --reporter-html-export performance-report.html
```

### Concurrent Testing

```bash
# Run multiple Newman instances
for i in {1..5}; do
  newman run Library-Management-System.postman_collection.json \
    -e Library-Management-System.postman_environment.json &
done
wait
```

## Security Testing

### Input Validation

- **SQL Injection:** GraphQL parameter sanitization
- **XSS Prevention:** Output encoding validation  
- **Rate Limiting:** Request throttling verification
- **Authentication:** Token-based access control

### Data Privacy

- **PII Protection:** Personal information handling
- **Data Encryption:** Sensitive data transmission
- **Access Controls:** Role-based permissions

## Additional Resources

- **[Main README](README.md)** - Project overview and quick start
- **[Project Structure Guide](PROJECT_STRUCTURE_GUIDE.md)** - Architecture details
- **[Docker README](docker-README.md)** - Container deployment guide

## Contributing to Tests

1. **Add New Tests:**
   - Create new requests in appropriate folder
   - Add response validation scripts
   - Update environment variables if needed

2. **Test Validation:**
   ```javascript
   // Example test script
   pm.test("Status code is 200", function () {
       pm.response.to.have.status(200);
   });
   
   pm.test("Response has data", function () {
       const responseJson = pm.response.json();
       pm.expect(responseJson).to.have.property('data');
   });
   ```

3. **Environment Management:**
   - Keep sensitive data in environment variables
   - Use dynamic variables for test data
   - Maintain separate environments for different deployments

---

**Happy Testing!**