#!/bin/bash

# Library Management System - Quick Test Script
# This script starts services and runs basic API tests

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
GRAPHQL_URL="http://localhost:8080/graphql"
HEALTH_URL="http://localhost:8080/actuator/health"
TIMEOUT=30

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $(date '+%H:%M:%S') - $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $(date '+%H:%M:%S') - $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%H:%M:%S') - $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $(date '+%H:%M:%S') - $1"
}

# Function to check if services are running
check_services() {
    log_info "Checking if services are running..."
    
    # Check API Gateway health
    if curl -s -f "$HEALTH_URL" > /dev/null 2>&1; then
        log_success "API Gateway is running"
        return 0
    else
        log_error "API Gateway is not responding"
        return 1
    fi
}

# Function to wait for services to be ready
wait_for_services() {
    log_info "Waiting for services to be ready..."
    local count=0
    local max_attempts=60  # 2 minutes timeout
    
    while [ $count -lt $max_attempts ]; do
        if check_services; then
            log_success "All services are ready!"
            return 0
        fi
        
        count=$((count + 1))
        echo -n "."
        sleep 2
    done
    
    log_error "Services did not start within timeout period"
    return 1
}

# Function to run a GraphQL query
run_graphql_query() {
    local query="$1"
    local description="$2"
    
    log_info "Testing: $description"
    
    local response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -d "$query" \
        "$GRAPHQL_URL")
    
    # Check if response contains errors
    if echo "$response" | jq -e '.errors' > /dev/null 2>&1; then
        log_error "$description failed"
        echo "$response" | jq '.errors'
        return 1
    else
        log_success "$description passed"
        return 0
    fi
}

# Function to run basic API tests
run_basic_tests() {
    log_info "Running basic API tests..."
    
    local tests_passed=0
    local tests_total=0
    
    # Test 1: Health Check
    tests_total=$((tests_total + 1))
    log_info "Test 1: Health Check"
    if curl -s -f "$HEALTH_URL" | jq -e '.status == "UP"' > /dev/null 2>&1; then
        log_success "Health check passed"
        tests_passed=$((tests_passed + 1))
    else
        log_error "Health check failed"
    fi
    
    # Test 2: GraphQL Schema Query
    tests_total=$((tests_total + 1))
    local schema_query='{"query": "query { __schema { queryType { name } } }"}'
    if run_graphql_query "$schema_query" "GraphQL Schema Query"; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test 3: Get Total Books
    tests_total=$((tests_total + 1))
    local books_query='{"query": "query { totalBooks }"}'
    if run_graphql_query "$books_query" "Get Total Books"; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test 4: Get Total Users  
    tests_total=$((tests_total + 1))
    local users_query='{"query": "query { totalUsers }"}'
    if run_graphql_query "$users_query" "Get Total Users"; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test 5: Get Book Genres
    tests_total=$((tests_total + 1))
    local genres_query='{"query": "query { bookGenres }"}'
    if run_graphql_query "$genres_query" "Get Book Genres"; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test Results Summary
    echo ""
    log_info "=== TEST RESULTS SUMMARY ==="
    log_info "Tests Passed: $tests_passed/$tests_total"
    
    if [ $tests_passed -eq $tests_total ]; then
        log_success "ALL TESTS PASSED! 🎉"
        return 0
    else
        log_warning "Some tests failed. Check the output above."
        return 1
    fi
}

# Function to start services with Docker
start_docker_services() {
    log_info "Starting services with Docker Compose..."
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "docker-compose is not installed"
        return 1
    fi
    
    # Build and start services
    docker-compose up -d --build
    
    if [ $? -eq 0 ]; then
        log_success "Docker services started"
        return 0
    else
        log_error "Failed to start Docker services"
        return 1
    fi
}

# Function to stop services
stop_services() {
    log_info "Stopping services..."
    docker-compose down
    log_success "Services stopped"
}

# Function to show service logs
show_logs() {
    log_info "Showing service logs..."
    docker-compose logs -f --tail=50
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start     Start services and run tests"
    echo "  test      Run tests only (services must be running)"
    echo "  stop      Stop all services"
    echo "  logs      Show service logs"
    echo "  status    Check service status"
    echo "  help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start    # Start services and run tests"
    echo "  $0 test     # Run tests against running services"
    echo "  $0 logs     # Show service logs"
}

# Main script logic
main() {
    local command="${1:-start}"
    
    case "$command" in
        "start")
            log_info "Starting Library Management System Quick Test..."
            if start_docker_services; then
                if wait_for_services; then
                    run_basic_tests
                else
                    log_error "Services failed to start properly"
                    exit 1
                fi
            else
                log_error "Failed to start services"
                exit 1
            fi
            ;;
        "test")
            log_info "Running tests only..."
            if check_services; then
                run_basic_tests
            else
                log_error "Services are not running. Please start them first."
                exit 1
            fi
            ;;
        "stop")
            stop_services
            ;;
        "logs")
            show_logs
            ;;
        "status")
            check_services
            ;;
        "help")
            show_usage
            ;;
        *)
            log_error "Unknown command: $command"
            show_usage
            exit 1
            ;;
    esac
}

# Check dependencies
if ! command -v curl &> /dev/null; then
    log_error "curl is required but not installed"
    exit 1
fi

if ! command -v jq &> /dev/null; then
    log_error "jq is required but not installed. Please install it:"
    log_info "Ubuntu/Debian: sudo apt-get install jq"
    log_info "macOS: brew install jq"
    exit 1
fi

# Run main function
main "$@"