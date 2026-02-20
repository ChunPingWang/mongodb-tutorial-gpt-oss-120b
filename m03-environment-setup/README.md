# Module 03 - Environment Setup

This module covers setting up the development environment for MongoDB with Spring Boot.

## Domain Model

- **Customer**: Represents a customer with id, name, and email

## Structure

```
src/main/java/com/course/mongodb/m03/
├── M03Application.java          # Spring Boot application entry point
├── domain/
│   └── Customer.java           # Customer domain model
├── repository/
│   └── CustomerRepository.java # MongoDB repository interface
└── service/
    └── CustomerService.java    # Customer service layer
```

## Running the Application

```bash
./gradlew :m03-environment-setup:bootRun
```

## Testing

```bash
./gradlew :m03-environment-setup:test
```
