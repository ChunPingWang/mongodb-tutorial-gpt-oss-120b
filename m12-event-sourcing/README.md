# M12: Event Sourcing

This module demonstrates Event Sourcing pattern with MongoDB as the event store.

## Domain Events

The `DomainEvent` sealed interface represents domain events:
- `AccountOpenedEvent` - Triggered when a new account is created
- `FundsDepositedEvent` - Triggered when funds are deposited

### Event Structure

Each event contains:
- Unique event ID
- Aggregate ID (the entity the event applies to)
- Occurred timestamp
- Version number (for ordering)
- Event-specific data

## Event Store

The `EventStore` entity persists domain events to MongoDB:
- Stores event data as JSON
- Maintains aggregate ID and version for ordering
- Supports querying by aggregate or event type

## Event Sourcing Service

The `EventSourcingService` provides:
- Creating and saving domain events
- Reconstructing aggregate state from events
- Querying events by aggregate or type
- Version management

### Benefits of Event Sourcing

1. **Complete Audit Trail**: Every state change is captured as an event
2. **Temporal Queries**: Can query the system state at any point in time
3. **Event Replay**: Can replay events to rebuild state
4. **Scalability**: Append-only event store is highly scalable
5. **Debugging**: Can trace through all state changes

## Running Tests

```bash
./gradlew :m12-event-sourcing:test
```
