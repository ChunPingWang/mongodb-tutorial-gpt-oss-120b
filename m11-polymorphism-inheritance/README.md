# M11: Polymorphism and Inheritance

This module demonstrates polymorphism and inheritance patterns using Java 17+ sealed interfaces with MongoDB.

## Domain Model: FinancialProduct

The `FinancialProduct` is a sealed interface that permits two implementations:
- `Deposit` - A time deposit with interest rate and term
- `Fund` - An investment fund with NAV and shares

### Sealed Interface Pattern

Java 17+ sealed interfaces provide type-safe polymorphism:
```java
public sealed interface FinancialProduct permits FinancialProduct.Deposit, FinancialProduct.Fund
```

### Record Types

Both implementations use Java records for immutable data:
- `Deposit(String id, String customerId, BigDecimal balance, ...)`
- `Fund(String id, String customerId, BigDecimal balance, ...)`

### Benefits

- **Type Safety**: The compiler enforces exhaustive type checking
- **Immutability**: Records provide immutable data carriers
- **Pattern Matching**: Use instanceof and pattern matching for type-specific logic
- **MongoDB Support**: Spring Data MongoDB stores the type information for polymorphic queries

## Service Operations

The `FinancialProductService` demonstrates:
- Creating different product types
- Filtering by concrete types
- Calculating aggregated values using polymorphism

## Running Tests

```bash
./gradlew :m11-polymorphism-inheritance:test
```
