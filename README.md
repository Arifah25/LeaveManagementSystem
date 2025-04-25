# Leave Management System

Leave Management System for HR use

## Features

- **User Authentication**: Secure JWT-based authentication with role-based authorization
- **Role-based Access Control**: Different permissions for Employees, Managers, and Administrators
- **Leave Management**:
  - Employees can apply for leaves, view leave history, and check leave balances
  - Managers can review and approve/reject leave requests from their team members
  - Administrators have complete oversight and final approval authority
- **Department Management**: Track employees by department and manage organizational structure
- **Caching**: Redis-backed caching for improved performance
- **Swagger API Documentation**: Interactive API documentation with OpenAPI

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **CI/CD**: GitHub Actions
- **API Documentation**: OpenAPI/Swagger
- **Containerization**: Docker & Docker Compose

## Prerequisites

- JDK 17 or higher
- Maven 3.x
- Docker and Docker Compose (for local development)

## Getting Started

### Setting Up the Development Environment

1. Clone the repository:

   ```bash
   git clone <repository-url>
   cd LeaveManagementSystem
   ```

2. Start the required services using Docker Compose:

   ```
   docker-compose up -d
   ```

3. Build the application

   ```
    ./mvnw clean package
   ```

4. Run the application

   ```
    ./mvnw spring-boot:run
   ```

#### The application will be available at http://localhost:8080.
