# Module 04 - Document Thinking

This module covers document-oriented thinking in MongoDB and how to model data as documents.

## Domain Model

- **Address**: Represents an address with city, district, and detail

## Structure

```
src/main/java/com/course/mongodb/m04/
├── M04Application.java          # Spring Boot application entry point
├── domain/
│   └── Address.java            # Address domain model
├── repository/
│   └── AddressRepository.java   # MongoDB repository interface
└── service/
    └── AddressService.java      # Address service layer
```

## Running the Application

```bash
./gradlew :m04-document-thinking:bootRun
```

## Testing

```bash
./gradlew :m04-document-thinking:test
```
