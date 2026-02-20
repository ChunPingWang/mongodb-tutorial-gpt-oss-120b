# M15: Indexing and Performance

This module demonstrates MongoDB indexing strategies and performance optimization.

## Domain Model: IndexedTransaction

The `IndexedTransaction` entity showcases various indexing strategies:

### Indexes

1. **Single Field Indexes**
   - `@Indexed` on `accountId` - For account lookups
   - `@Indexed` on `transactionId` - For transaction lookups
   - `@Indexed` on `transactionDate` - For date range queries
   - `@Indexed` on `status` - For status filtering
   - `@Indexed` on `category` - For category filtering
   - `@Indexed` on `isForeign` - For boolean filtering

2. **Compound Indexes**
   - `account_date_idx`: `{accountId: 1, transactionDate: -1}` - Compound index for account history
   - `status_type_idx`: `{status: 1, type: 1}` - Compound index for filtered queries

### Performance Benefits

1. **Query Optimization**: Indexes dramatically improve query performance
2. **Covered Queries**: Compound indexes can cover entire queries
3. **Sort Performance**: Indexes enable efficient in-memory sorting
4. **Index Intersection**: MongoDB can use multiple indexes together

### Index Types

- **Single Field**: Simple equality queries
- **Compound Index**: Multi-field queries with defined field order
- **Multikey Index**: For array fields
- **Text Index**: For text search
- **Geospatial Index**: For location queries

## Best Practices

1. Create indexes based on query patterns
2. Use compound indexes for frequently combined fields
3. Place equality fields before range fields in compound indexes
4. Use explain() to analyze query performance
5. Monitor index usage and size

## Running Tests

```bash
./gradlew :m15-indexing-performance:test
```
