# M12: Event Sourcing with MongoDB

## 學習目標
使用 MongoDB 實作 Event Sourcing Pattern

---

## M12-DOC-01: Event Sourcing 概念與 MongoDB 實作

### Event Store 設計

```json
// events collection
{
  "_id": "evt_001",
  "aggregateId": "acc_001",
  "aggregateType": "BankAccount",
  "eventType": "AccountOpened",
  "version": 1,
  "payload": {
    "accountNumber": "A001",
    "initialBalance": 0
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### Aggregate 重建：Event Replay

```java
public class BankAccount {
    private String id;
    private Money balance;
    private int version;
    
    public static BankAccount reconstruct(List<DomainEvent> events) {
        BankAccount account = new BankAccount();
        for (DomainEvent event : events) {
            account.apply(event);
        }
        return account;
    }
    
    private void apply(DomainEvent event) {
        switch (event) {
            case AccountOpenedEvent e -> apply(e);
            case FundsDepositedEvent e -> apply(e);
            case FundsWithdrawnEvent e -> apply(e);
        }
    }
    
    private void apply(AccountOpenedEvent event) {
        this.id = event.accountId();
        this.balance = Money.ZERO;
        this.version = 1;
    }
    
    private void apply(FundsDepositedEvent event) {
        this.balance = this.balance.add(event.amount());
        this.version++;
    }
}
```

### Snapshot 優化

```java
// 每 100 個事件產生一次 Snapshot
if (events.size() >= 100) {
    Snapshot snapshot = Snapshot.builder()
        .aggregateId(aggregateId)
        .aggregateType(aggregateType)
        .snapshotData(serialize(aggregate))
        .version(version)
        .build();
    eventStore.saveSnapshot(snapshot);
}
```

### Capped Collection

```javascript
db.createCollection("events", { 
    capped: true, 
    size: 1073741824,  // 1GB
    max: 1000000 
})
```

---

## M12-DOC-02: Domain Event 設計原則

### Event 命名慣例

- 使用過去式：`AccountOpened`, `FundsDeposited`, `FundsTransferred`
- 包含業務語意：不是 `AccountUpdated` 而是 `AccountFrozen`

### Event Payload 設計

```java
// 好的設計：包含足夠重現事件的資訊
public record FundsDepositedEvent(
    String aggregateId,
    BigDecimal amount,
    String depositType,
    Instant timestamp,
    String idempotencyKey  // 支援冪等性
) {}

// 避免：只傳遞 delta
// public record FundsDepositedEvent(String accountId, BigDecimal newBalance) {}
```

### Event Versioning

```json
{
  "_id": "evt_001",
  "eventType": "AccountOpened",
  "eventVersion": "2.0",
  "payload": {
    "accountNumber": "A001",
    "initialBalance": 0,
    "accountType": "CHECKING"  // V2 新增欄位
  }
}
```

---

## M12-LAB-01: 銀行帳戶 Event Sourcing

### BDD Feature

```gherkin
Feature: 帳戶 Event Sourcing
  Scenario: 從事件重建帳戶狀態
    Given 帳戶 "A001" 的事件序列:
      | 序號 | 事件類型           | 資料                    |
      | 1    | AccountOpened      | balance: 0              |
      | 2    | FundsDeposited     | amount: 50000           |
      | 3    | FundsWithdrawn     | amount: 10000           |
      | 4    | FundsTransferred   | amount: 5000, to: A002  |
    When 重建帳戶狀態
    Then 帳戶餘額為 35000 元
    And 事件版本號為 4
```

### EventStore Repository

```java
public interface EventStore {
    void saveEvent(DomainEvent event);
    List<DomainEvent> getEventsForAggregate(String aggregateId);
    Optional<Snapshot> getLatestSnapshot(String aggregateId);
    void saveSnapshot(Snapshot snapshot);
}
```

### MongoDB EventStore 實作

```java
@Repository
public class MongoEventStore implements EventStore {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public void saveEvent(DomainEvent event) {
        Document doc = new Document()
            .append("aggregateId", event.aggregateId())
            .append("aggregateType", event.aggregateType())
            .append("eventType", event.getClass().getSimpleName())
            .append("version", event.version())
            .append("payload", event)
            .append("timestamp", Instant.now());
        mongoTemplate.insert(doc, "events");
    }
}
```

---

## M12-LAB-02: 保險理賠 Event Sourcing

### 理賠生命週期事件

```
ClaimReported → ClaimInvestigated → ClaimAssessed → ClaimApproved → ClaimPaid
```

### 審計追蹤

```json
{
  "_id": "evt_003",
  "aggregateId": "claim_001",
  "eventType": "ClaimAssessed",
  "payload": {
    "assessedAmount": 50000,
    "assessor": "john.doe",
    "notes": "經審核理賠成立"
  },
  "timestamp": "2024-01-15T14:30:00Z"
}
```
