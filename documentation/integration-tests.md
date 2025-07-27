# Integration Tests Documentation

## Overview
This document describes the comprehensive integration testing strategy for the full-stack bank account application, including backend (Spring Boot), frontend (Angular), and database (MySQL) components.

## Test Architecture

### 1. Test Environment Setup
- **Database**: MySQL 8.0 with dedicated test database (`test_bank_account_db`)
- **Backend**: Spring Boot application running with `integration-test` profile
- **Frontend**: Angular application served via http-server
- **Network**: Isolated Docker network for test containers

### 2. Integration Test Types

#### Backend Integration Tests
- **Database Integration**: Tests repository layer with real MySQL database
- **API Integration**: Tests REST endpoints with full Spring context
- **Service Integration**: Tests business logic with actual database transactions

#### Frontend Integration Tests
- **Component Integration**: Tests Angular components with real HTTP calls
- **E2E User Flows**: Tests complete user journeys from UI to database
- **API Communication**: Tests frontend-backend communication

#### Full-Stack Integration Tests
- **End-to-End Workflows**: Complete user scenarios across all layers
- **Data Consistency**: Ensures data integrity across frontend-backend-database
- **Error Handling**: Tests error propagation and handling across components

### 3. Test Execution Strategy

#### CI/CD Pipeline Integration
The integration tests run automatically when:
- Backend OR frontend code changes
- Database schema changes
- Docker configuration changes
- Main branch commits
- Merge request events

#### Test Environment Lifecycle
1. **Setup Phase**:
   - Start MySQL test database
   - Wait for database readiness
   - Start backend application with test profile
   - Build and serve frontend application
   - Wait for all services to be healthy

2. **Execution Phase**:
   - Run backend integration tests
   - Run frontend integration tests
   - Run cross-component tests
   - Collect test results and coverage

3. **Cleanup Phase**:
   - Stop all test services
   - Clean up test data
   - Archive test reports

### 4. Test Configuration

#### Backend Test Configuration (`application-integration-test.properties`)
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://mysql:3306/test_bank_account_db
spring.datasource.username=test_user
spring.datasource.password=test_password
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Test-specific configurations
logging.level.org.springframework.web=DEBUG
logging.level.org.cicd.accountservice=DEBUG

# Disable security for integration tests
spring.security.enabled=false
management.endpoints.web.exposure.include=*
```

#### Frontend Test Configuration
```typescript
// test-environment.ts
export const environment = {
  production: false,
  testing: true,
  apiUrl: 'http://localhost:8080/api',
  enableMocks: false
};
```

### 5. Test Data Management

#### Database Test Data
- **Test Fixtures**: Predefined test data for consistent testing
- **Data Builders**: Programmatic test data creation
- **Cleanup Strategy**: Automatic cleanup after each test class

#### Sample Test Data Structure
```sql
-- Test accounts
INSERT INTO bank_accounts (id, account_number, holder_name, balance, account_type, created_date) VALUES
(1, 'TEST001', 'John Doe', 1000.00, 'SAVINGS', NOW()),
(2, 'TEST002', 'Jane Smith', 2500.00, 'CHECKING', NOW()),
(3, 'TEST003', 'Bob Johnson', 500.00, 'SAVINGS', NOW());

-- Test transactions
INSERT INTO transactions (id, account_id, amount, transaction_type, description, transaction_date) VALUES
(1, 1, 100.00, 'DEPOSIT', 'Initial deposit', NOW()),
(2, 2, -50.00, 'WITHDRAWAL', 'ATM withdrawal', NOW());
```

### 6. Test Scenarios

#### Critical User Journeys
1. **Account Creation Flow**:
   - Frontend form validation
   - Backend account creation
   - Database persistence verification
   - Frontend state update

2. **Transaction Processing**:
   - Transaction submission via UI
   - Backend validation and processing
   - Database transaction recording
   - Balance update verification
   - Frontend balance refresh

3. **Account Management**:
   - View account details
   - Update account information
   - Delete account (with validation)
   - Error handling for invalid operations

#### Error Scenarios
1. **Database Connection Failures**
2. **Backend Service Unavailability**
3. **Invalid Data Validation**
4. **Concurrent Transaction Handling**
5. **Network Timeout Scenarios**

### 7. Performance Testing

#### Load Testing Scenarios
- **Concurrent User Simulation**: Multiple users accessing the system
- **Database Load Testing**: High-volume transaction processing
- **API Response Time Validation**: Ensuring acceptable response times

#### Performance Metrics
- API response time < 500ms for 95% of requests
- Database query execution < 100ms
- Frontend page load time < 2 seconds
- Memory usage within acceptable limits

### 8. Monitoring and Reporting

#### Test Execution Monitoring
- Real-time test progress tracking
- Service health monitoring during tests
- Resource utilization tracking

#### Test Reports
- **JUnit Reports**: Backend test results in XML format
- **Coverage Reports**: Code coverage for both backend and frontend
- **Performance Reports**: Response time and throughput metrics
- **Integration Reports**: Cross-component test results

#### Report Artifacts
```
backend/target/surefire-reports/TEST-*.xml
backend/target/site/jacoco/jacoco.xml
frontend/coverage/lcov.info
integration-test-results.html
performance-test-results.json
```

### 9. Troubleshooting Guide

#### Common Issues and Solutions

1. **Database Connection Timeout**
   - Increase health check timeout
   - Verify network connectivity
   - Check database startup logs

2. **Backend Service Not Ready**
   - Increase startup wait time
   - Check application logs
   - Verify database dependencies

3. **Frontend Build Failures**
   - Check Node.js version compatibility
   - Verify npm dependencies
   - Review build logs

4. **Test Data Conflicts**
   - Ensure proper test isolation
   - Verify cleanup procedures
   - Check transaction rollback

### 10. Best Practices

#### Test Development
1. **Test Isolation**: Each test should be independent
2. **Data Management**: Use transactions for test data cleanup
3. **Realistic Scenarios**: Test real-world user behaviors
4. **Error Testing**: Include negative test cases
5. **Performance Awareness**: Monitor test execution time

#### Maintenance
1. **Regular Updates**: Keep test data and scenarios current
2. **Documentation**: Maintain up-to-date test documentation
3. **Monitoring**: Track test stability and execution time
4. **Feedback Loop**: Use test results to improve code quality

### 11. Local Development Testing

#### Running Integration Tests Locally
```bash
# Start test environment
docker-compose --profile testing up -d test-db

# Run backend integration tests
cd backend
mvn test -Dtest=**/*IntegrationTest -Dspring.profiles.active=integration-test

# Run frontend integration tests
cd frontend/bank-account-app
npm run test:integration

# Run full stack tests
docker-compose up --build
```

#### Debug Mode
```bash
# Backend debug mode
mvn spring-boot:run -Dspring.profiles.active=integration-test -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

# Frontend debug mode
npm run start -- --configuration=integration-test
```

This comprehensive integration testing strategy ensures reliable, maintainable, and efficient testing of the complete application stack while providing clear feedback on system health and performance.
