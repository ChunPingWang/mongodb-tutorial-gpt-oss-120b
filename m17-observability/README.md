# M17: Observability

This module demonstrates MongoDB-based observability patterns for monitoring and metrics.

## Domain Model: MetricsData

The `MetricsData` entity captures application and system metrics:

### Metric Types

- `RESPONSE_TIME` - API response times in milliseconds
- `THROUGHPUT` - Requests per second
- `ERROR_RATE` - Percentage of failed requests
- `CPU_USAGE` - CPU utilization percentage
- `MEMORY_USAGE` - Memory utilization percentage
- `DISK_USAGE` - Disk utilization percentage
- `ACTIVE_CONNECTIONS` - Number of active connections
- `CUSTOM` - Custom application metrics

### Indexes

1. **Compound Index**: `{metricType: 1, timestamp: -1}` - For metric type analysis
2. **Compound Index**: `{serviceName: 1, timestamp: -1}` - For service-specific queries

## Observability Patterns

### Metrics Collection

1. **Application Metrics**: Response times, throughput, error rates
2. **System Metrics**: CPU, memory, disk usage
3. **Business Metrics**: Custom metrics relevant to business logic

### Key Metrics

1. **RED Metrics** (Rate, Errors, Duration)
   - Rate: Requests per second
   - Errors: Error rate percentage
   - Duration: Response time distribution

2. **USE Metrics** (Utilization, Saturation, Errors)
   - Utilization: Resource usage percentage
   - Saturation: Queue depths, load
   - Errors: Error counts

## Use Cases

1. **Performance Monitoring**: Track API performance over time
2. **Capacity Planning**: Analyze resource usage trends
3. **Alerting**: Trigger alerts on threshold violations
4. **Troubleshooting**: Identify performance bottlenecks

## Best Practices

1. Use time-series appropriate queries
2. Implement data retention policies
3. Use aggregation pipelines for analytics
4. Consider downsampling for historical data

## Running Tests

```bash
./gradlew :m17-observability:test
```
