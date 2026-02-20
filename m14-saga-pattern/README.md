# M14: SAGA Pattern

This module demonstrates the SAGA pattern for distributed transactions with MongoDB.

## Domain Model: OrderSaga

The `OrderSaga` manages the lifecycle of a distributed order process:

### Saga Status
- `STARTED` - Saga initiated
- `IN_PROGRESS` - Currently executing a step
- `COMPLETED` - All steps completed successfully
- `FAILED` - Saga failed
- `COMPENSATING` - Rolling back completed steps
- `COMPENSATED` - Rollback complete

### Saga Steps
1. `CREATE_ORDER` - Create the order
2. `RESERVE_INVENTORY` - Reserve items in inventory
3. `PROCESS_PAYMENT` - Process customer payment
4. `SHIP_ORDER` - Ship the order to customer
5. `NOTIFY_CUSTOMER` - Send notification

## SAGA Pattern

The SAGA pattern manages distributed transactions through:

1. **Choreography**: Each service publishes events; other services react
2. **Orchestration**: A central coordinator manages the workflow

### Compensation

When a step fails, the saga executes compensating transactions in reverse order:
- If payment fails after inventory reservation, release the inventory
- If shipping fails after payment, refund the customer

### Benefits

1. **Distributed Transactions**: Handles transactions across multiple services
2. **Eventual Consistency**: Accepts temporary inconsistency for performance
3. **Scalability**: Each step is independent
4. **Recoverability**: Automatic compensation on failure

## Service Operations

The `OrderSagaService` provides:
- Starting a new saga
- Advancing through saga steps
- Compensating on failure
- Querying saga status

## Running Tests

```bash
./gradlew :m14-saga-pattern:test
```
