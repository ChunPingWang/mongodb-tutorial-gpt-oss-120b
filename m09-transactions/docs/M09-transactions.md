# M09: Multi-Document Transactions

## 學習目標
理解 MongoDB Transaction 的能力與限制

---

## M09-DOC-01: MongoDB Transaction 深度解析

### Transaction 支援環境

- **支援**：Replica Set（至少 3 節點）、Sharded Cluster
- **不支援**：單一 MongoDB 實例

### Write Concern / Read Concern / Read Preference

```
Write Concern (寫入確認)
├── w: 0      - 不等待確認
├── w: 1      - 等待主要節點確認
├── w: majority - 等待多數節點確認
└── j: true   - 等待寫入日誌

Read Concern (讀取一致性)
├── local    - 讀取本地最新數據
├── majority - 讀取大多數節點確認的數據
└── linearizable - 線性一致性

Read Preference (讀取節點)
├── primary           - 只讀主要節點
├── primaryPreferred  - 優先主要節點
├── secondary        - 只讀次要節點
└── nearest          - 讀取延遲最低節點
```

### Transaction 與 RDB 比較

| 特性 | RDB Transaction | MongoDB Transaction |
|------|----------------|---------------------|
| 隔離級別 | 完整支援 | 快照隔離 |
| 支援範圍 | 單一資料庫 | 多 Collection |
| 效能影響 | 中等 | 較高 |
| 建議使用 | 金融轉帳 | 必要的場景 |

---

## M09-DOC-02: Spring @Transactional 與 MongoDB

### MongoTransactionManager 配置

```java
@Configuration
public class MongoConfig {
    @Bean
    public MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
```

### @Transactional 使用

```java
@Service
public class TransferService {
    
    @Transactional
    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        // 扣款
        BankAccount from = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new AccountNotFoundException());
        from.withdraw(amount);
        accountRepository.save(from);
        
        // 存款
        BankAccount to = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new AccountNotFoundException());
        to.deposit(amount);
        accountRepository.save(to);
    }
}
```

### 常見陷阱

1. **單節點不支援 Transaction**
2. **Transaction 內避免網路呼叫**
3. **Transaction  timeout 設定**

---

## M09-LAB-01: 銀行轉帳交易

### BDD Feature

```gherkin
Feature: 帳戶間轉帳
  Scenario: 成功轉帳
    Given 帳戶 "A001" 餘額 50000 元
    And 帳戶 "A002" 餘額 10000 元
    When 從 "A001" 轉帳 20000 元至 "A002"
    Then "A001" 餘額為 30000 元
    And "A002" 餘額為 30000 元
    And 產生兩筆交易記錄

  Scenario: 餘額不足轉帳失敗
    Given 帳戶 "A001" 餘額 5000 元
    When 從 "A001" 轉帳 20000 元至 "A002"
    Then 轉帳失敗並回傳餘額不足錯誤
    And 兩帳戶餘額維持不變
```

### Testcontainers: MongoDB Replica Set

```java
@Container
static MongoDBContainer mongoDBContainer = 
    new MongoDBContainer("mongo:7.0")
        .withReplicaSetName("rs0");
```

### 測試案例

```java
@Test
void shouldTransferSuccessfully() {
    transferService.transfer("A001", "A002", new BigDecimal("20000"));
    
    BankAccount a1 = accountRepository.findById("A001").get();
    BankAccount a2 = accountRepository.findById("A002").get();
    
    assertThat(a1.getBalance()).isEqualTo(new BigDecimal("30000"));
    assertThat(a2.getBalance()).isEqualTo(new BigDecimal("30000"));
}

@Test
void shouldRollbackOnInsufficientFunds() {
    assertThatThrownBy(() -> 
        transferService.transfer("A001", "A002", new BigDecimal("20000"))
    ).isInstanceOf(InsufficientFundsException.class);
    
    BankAccount a1 = accountRepository.findById("A001").get();
    assertThat(a1.getBalance()).isEqualTo(new BigDecimal("5000")); // 未變動
}
```

---

## M09-LAB-02: 保險多文件一致性

### 情境：核保通過

```
1. 建立保單 (InsurancePolicy)
2. 建立收費排程 (PaymentSchedule)
3. 更新客戶狀態 (Customer)
4. 產生領域事件 (DomainEvent)
```

### SAGA 替代方案

由於 Transaction 效能較高，建議使用 SAGA Pattern：

```java
@Saga
public class UnderwritingSaga {
    
    @Step(name = "createPolicy")
    public void createPolicy() { }
    
    @Compensate(name = "createPolicy")
    public void compensatePolicy() { }
    
    @Step(name = "createPaymentSchedule")
    public void createPaymentSchedule() { }
    
    @Compensate(name = "createPaymentSchedule")
    public void compensatePaymentSchedule() { }
}
```
