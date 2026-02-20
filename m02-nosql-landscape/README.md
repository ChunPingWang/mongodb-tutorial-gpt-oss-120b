# Module 02 - NoSQL Landscape

This module covers the fundamentals of NoSQL databases and their differences from relational databases.

## Domain Model

- **Product**: Represents a product with id, name, price, and category

## Structure

```
src/main/java/com/course/mongodb/m02/
├── M02Application.java          # Spring Boot application entry point
├── domain/
│   └── Product.java             # Product domain model
├── repository/
│   └── ProductRepository.java   # MongoDB repository interface
└── service/
    └── ProductService.java      # Product service layer
```

## Running the Application

```bash
./gradlew :m02-nosql-landscape:bootRun
```

## Testing

```bash
./gradlew :m02-nosql-landscape:test
```
