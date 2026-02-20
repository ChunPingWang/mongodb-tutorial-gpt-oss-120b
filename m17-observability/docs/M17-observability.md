# M17: 可觀測性與監控

## 學習目標
建立 MongoDB 應用的完整可觀測性

---

## M17-DOC-01: MongoDB 可觀測性三支柱

### 1. Metrics (指標)

```json
{
  "metrics": {
    "connections": {
      "current": 150,
      "available": 850,
      "totalCreated": 10000
    },
    "operations": {
      "insert": 1000,
      "query": 5000,
      "update": 800,
      "delete": 200
    },
    "queryExecutor": {
      "IXSCAN": 4000,
      "COLLSCAN": 500
    }
  }
}
```

### 2. Logging (日誌)

```javascript
// 慢查詢日誌 (> 100ms)
{
  "ts": "2024-01-20T10:00:00.123Z",
  "millis": 150,
  "planSummary": "IXSCAN { accountId: 1 }",
  "nreturned": 10,
  "docsExamined": 100000
}
```

### 3. Tracing (分散式追蹤)

```
Trace: [trace-id]
  ├── Span: [account-service] POST /accounts
  │   └── Span: [mongodb-driver] find accounts
  └── Span: [notification-service] send notification
```

---

## M17-DOC-02: Spring Boot Actuator + Micrometer + MongoDB

### 配置 Actuator

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    tags:
      application: ${spring.application.name}
```

### MongoDB Health Indicator

```java
@Component
public class MongoDbHealthIndicator implements ReactiveHealthIndicator {
    
    @Autowired
    private MongoDbFactory mongoDbFactory;
    
    @Override
    public Mono<Health> health() {
        try {
            mongoDbFactory.getDb().runCommand(new Document("ping", 1));
            return Mono.just(Health.up()
                .withDetail("replicaSet", "rs0")
                .build());
        } catch (Exception e) {
            return Mono.just(Health.down()
                .withDetail("error", e.getMessage())
                .build());
        }
    }
}
```

### 自訂 Metrics

```java
@Configuration
public class MongoDbMetricsConfig {
    
    @Bean
    public MongoCommandListener metricsListener(MeterRegistry registry) {
        return new MongoCommandListener() {
            @Override
            public void commandStarted(CommandStartedEvent event) {
                Timer.builder("mongodb.command.duration")
                    .tag("command", event.getCommandName())
                    .register(registry)
                    .start();
            }
            
            @Override
            public void commandSucceeded(CommandSucceededEvent event) {
                // 記錄成功
            }
        };
    }
}
```

### Grafana Dashboard

```json
{
  "panels": [
    {
      "title": "MongoDB Connection Pool",
      "targets": [
        { "expr": "mongodb_connections_current" }
      ]
    },
    {
      "title": "Query Latency (p95)",
      "targets": [
        { "expr": "histogram_quantile(0.95, mongodb_command_duration_bucket)" }
      ]
    }
  ]
}
```

---

## M17-LAB-01: 全方位監控實作

### Testcontainers: MongoDB + Prometheus + Grafana

```yaml
# docker-compose.yml
services:
  mongodb:
    image: mongo:7.0
  prometheus:
    image: prom/prometheus
  grafana:
    image: grafana/grafana
```

### 實作自訂 MongoDB Metrics

```java
@Service
public class MongoMetricsService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Timed("mongodb.query.time")
    public <T> List<T> findWithMetrics(Query query, Class<T> entityClass) {
        return mongoTemplate.find(query, entityClass);
    }
}
```

### Distributed Tracing: OpenTelemetry

```java
@Bean
public MongoCommandListener tracingListener(Tracer tracer) {
    return new OpenTelemetryMongoCommandListener(tracer);
}
```

---

## M17-LAB-02: 效能問題診斷演練

### 模擬場景

1. **缺少索引的慢查詢**
2. **連線池耗盡**
3. **Write Concern 延遲**

### 診斷工具

```java
// 分析查詢計劃
Explainable explainable = collection.find(query).explain();

// 檢查連線池
MongoClientOptions options = mongoClient.getMongoClientOptions();
System.out.println("Pool: " + options.getConnectionPoolSettings());
```

### 修復驗證

```java
@Test
void verifyIndexFix() {
    // 建立索引前: COLLSCAN, 2000ms
    // 建立索引後: IXSCAN, 50ms
}
```
