# M16: Change Streams

This module demonstrates MongoDB Change Streams for real-time data change notifications.

## Domain Model: ChangeEvent

The `ChangeEvent` entity represents a MongoDB change stream event:

### Operation Types

- `INSERT` - A new document was inserted
- `UPDATE` - An existing document was updated
- `REPLACE` - An existing document was replaced
- `DELETE` - A document was deleted
- `DROP` - The collection was dropped
- `RENAME` - The collection was renamed
- `INVALIDATE` - The cursor was invalidated

### Event Fields

1. **operationType**: Type of operation performed
2. **collectionName**: Name of the affected collection
3. **documentKey**: Unique identifier of the affected document
4. **fullDocument**: The full document (for insert, replace, update)
5. **updateDescription**: Fields updated/removed (for update operations)
6. **wallTime**: Wall clock time on the server
7. **clusterTime**: Logical time of the operation
8. **txnNumber**: Transaction number (if in transaction)
9. **lsid**: Session identifier (if in session)

## Change Streams

Change Streams provide real-time notifications of changes:

1. **Resume Tokens**: Track position in the stream
2. **Full Document**: Option to retrieve full document on updates
3. **Aggregation Pipelines**: Filter/transform events
4. **Change Stream Events**: Full, updateDescription, etc.

## Use Cases

1. **Microservices Sync**: Propagate changes across services
2. **Audit Logging**: Track all database changes
3. **Real-time Analytics**: Update dashboards in real-time
4. **Cache Invalidation**: Keep caches synchronized
5. **Trigger Actions**: Execute logic on specific changes

## Best Practices

1. Handle resume tokens for reliability
2. Use proper error handling for network issues
3. Consider batching for high-volume changes
4. Filter events at the stream level when possible

## Running Tests

```bash
./gradlew :m16-change-streams:test
```
