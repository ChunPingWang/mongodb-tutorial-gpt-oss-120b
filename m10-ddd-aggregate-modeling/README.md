# M10: DDD Aggregate Modeling

This module demonstrates Domain-Driven Design (DDD) aggregate modeling with MongoDB.

## Domain Model: LoanApplication

The `LoanApplication` class is a rich domain model that encapsulates:

- **Attributes**: id, customerId, amount, interestRate, termMonths, status, submittedAt, approvedAt, rejectionReason, documents
- **Lifecycle Methods**: submit(), approve(), reject(), disburse()
- **Business Logic**: addDocument(), calculateMonthlyPayment()
- **State Queries**: isApproved(), isPending()

### Aggregate Pattern

The LoanApplication follows the DDD aggregate pattern:
- It serves as a transactional boundary
- Maintains invariants through state transitions
- Encapsulates business logic within the entity

### LoanStatus Enum

- DRAFT - Initial state
- PENDING - Submitted for review
- APPROVED - Approved by lender
- REJECTED - Rejected by lender
- DISBURSED - Funds released
- DEFAULTED - Payment defaulted

## Repository

`LoanApplicationRepository` extends Spring Data MongoDB's `MongoRepository` with custom query methods:
- `findByCustomerId(String customerId)`
- `findByStatus(LoanStatus status)`

## Service

`LoanApplicationService` provides application services that orchestrate domain operations:
- Creating new loan applications
- Submitting applications for review
- Approving/rejecting applications
- Disbursing approved loans
- Managing documents

## Running Tests

```bash
./gradlew :m10-ddd-aggregate-modeling:test
```
