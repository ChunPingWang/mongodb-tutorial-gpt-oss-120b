# M18: Migration and Versioning

This module demonstrates database migration and versioning strategies with MongoDB.

## Domain Model: SchemaVersion

The `SchemaVersion` entity tracks database schema migrations:

### Schema States

- `PENDING` - Migration created but not yet applied
- `APPLIED` - Migration successfully applied
- `FAILED` - Migration failed during execution
- `ROLLED_BACK` - Migration was rolled back

### Key Fields

1. **version**: Semantic version string (e.g., "1.0.0")
2. **description**: Human-readable migration description
3. **migrationScript**: The actual migration script/command
4. **checksum**: For verifying migration integrity
5. **appliedBy**: User/system that applied the migration
6. **appliedAt**: Timestamp when migration was applied
7. **executionTimeMs**: How long the migration took
8. **rollbackScript**: Script to reverse the migration

### Indexes

1. **Compound Index**: `{version: -1}` - For version lookups
2. **Compound Index**: `{appliedAt: -1}` - For applied migrations queries

## Migration Strategies

### Version-Based Migration

1. **Create**: Define new migration with version
2. **Apply**: Execute and track migration
3. **Verify**: Confirm migration state
4. **Rollback**: Reverse if needed

### Best Practices

1. **Idempotent Scripts**: Make migrations repeatable
2. **Rollback Support**: Always have rollback plan
3. **Checksum Verification**: Ensure script integrity
4. **Sequential Versions**: Use semantic versioning
5. **Metadata Tracking**: Track who/when/why

### Use Cases

1. **Schema Evolution**: Add/modify collections
2. **Data Migration**: Transform existing data
3. **Index Management**: Create/remove indexes
4. **Collection Migration**: Rename/reorganize collections

## Running Tests

```bash
./gradlew :m18-migration-versioning:test
```
