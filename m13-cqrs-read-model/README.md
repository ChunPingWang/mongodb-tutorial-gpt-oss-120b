# M13: CQRS Read Model

This module demonstrates the Command Query Responsibility Segregation (CQRS) pattern with MongoDB.

## Domain Model: Customer360View

The `Customer360View` is a read model that provides a denormalized view of customer data:
- Aggregates account and loan information
- Maintains computed fields like totalBalance and activeLoans
- Uses Java records for nested summaries

### Structure

- Customer basic info (id, name, email)
- Aggregated financial data (totalBalance, accountCount, activeLoans)
- List of account summaries
- List of loan summaries
- Last updated timestamp

## CQRS Pattern

### Command Side
- Handles writes through domain events
- Updates the write database (aggregate tables)

### Query Side
- Handles reads through denormalized views
- Materialized views optimized for read performance
- Can be updated via events or scheduled jobs

### Benefits

1. **Performance**: Read models are optimized for specific queries
2. **Scalability**: Separate read and write scaling
3. **Flexibility**: Different data models for different use cases
4. **Consistency**: Event-driven updates ensure synchronization

## Service Operations

The `Customer360ViewService` provides:
- Creating customer views
- Updating account information
- Updating loan information
- Querying customer data

## Running Tests

```bash
./gradlew :m13-cqrs-read-model:test
```
