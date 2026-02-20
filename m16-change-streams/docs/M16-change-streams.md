# M16: Change Streams 與事件驅動

## 學習目標
使用 Change Streams 實作即時資料同步與事件驅動

---

## M16-DOC-01: Change Streams 原理與應用

### Change Streams vs Polling

| 特性 | Change Streams | Polling |
|------|--------------|---------|
| 即時性 | 即時 | 延遲 |
| 資源消耗 | 低 | 高 |
| 斷線重連 | 自動 (Resume Token) | 需自行處理 |
| 複雜度 | 中 | 低 |

### Change Stream 事件類型

```json
{
  "operationType": "insert",
  "fullDocument": { "_id": "acc_001", "balance": 50000 },
  "documentKey": { "_id": "acc_001" },
  "ns": { "db": "banking", "coll": "accounts" }
}
```

### Resume Token

```
Change Stream
     │
     ├─ insert ──> { _id: Token1 }
     │
     ├─ update ──> { _id: Token2 } ──► 儲存 Token2
     │
     └─ 斷線 ──► 從 Token2 恢復
```

---

## M16-DOC-02: Spring Data MongoDB Change Streams API

### Reactive Change Stream

```java
@Configuration
public class ChangeStreamConfig {
    
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    
    @Bean
    public Flux<ChangeStreamEvent<Document>> changeStream() {
        return mongoTemplate.changeStream(
            "accounts",
            ChangeStreamOptions.builder()
                .filter(Aggregation.newAggregation(
                    match(Criteria.where("operationType").in("insert", "update"))
                ))
                .build(),
            Document.class
        );
    }
}
```

### @Tailable Cursor

```java
@Tailable
@Query("{ 'accountId': ?0 }")
Flux<Transaction> findByAccountId(String accountId);
```

### MessageListener Container

```java
@Component
public class AccountChangeListener {
    
    @EventListener
    public void handleChange(ChangeStreamEvent<Document> event) {
        String operationType = event.getOperationType();
        Document fullDocument = event.getFullDocument();
        
        switch (operationType) {
            case "insert" -> onInsert(fullDocument);
            case "update" -> onUpdate(fullDocument);
            case "delete" -> onDelete(event.getDocumentKey());
        }
    }
}
```

---

## M16-LAB-01: 即時帳戶餘額通知

### BDD Feature

```gherkin
Feature: 即時餘額變動通知
  Scenario: 存款後即時通知
    Given 客戶 "C001" 訂閱帳戶 "A001" 的餘額變動
    When 帳戶收到一筆 10000 元存款
    Then 在 1 秒內收到餘額變動通知
    And 通知包含變動前後餘額
```

### Change Stream 監聽

```java
@Service
public class BalanceNotificationService {
    
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    
    @PostConstruct
    public void startListening() {
        mongoTemplate.changeStream("accounts")
            .filter(aggregation)
            .doOnNext(this::processChange)
            .subscribe();
    }
    
    private void processChange(ChangeStreamEvent<Document> event) {
        if ("update".equals(event.getOperationType())) {
            Document updated = event.getFullDocument();
            notificationService.send(
                updated.getString("customerId"),
                "餘額變動: " + updated.get("balance")
            );
        }
    }
}
```

---

## M16-LAB-02: CDC 驅動的 CQRS Projection

### Change Streams 作為 Event Source

```java
@Component
public class ProjectionListener {
    
    @Autowired
    private Customer360Repository viewRepository;
    
    @EventListener
    public void onChange(ChangeStreamEvent<Document> event) {
        String collection = event.getNamespace().getCollection();
        
        switch (collection) {
            case "accounts" -> updateCustomerView(event);
            case "transactions" -> updateTransactionView(event);
        }
    }
}
```

### Resume Token 持久化

```java
@Service
public class ResumeTokenService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public void saveResumeToken(ChangeStreamEvent<?> event) {
        Document token = new Document("_id", event.getResumeToken());
        mongoTemplate.save(token, "resume_tokens");
    }
    
    public BsonDocument getResumeToken() {
        Document token = mongoTemplate
            .findOne(new Query(), Document.class, "resume_tokens");
        return token != null ? (BsonDocument) token.get("_id") : null;
    }
}
```
