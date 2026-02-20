# M19: 銀行核心系統 Capstone

## 學習目標
整合所有概念，建構完整的銀行帳戶管理系統

---

## M19-DOC-01: 銀行帳戶管理系統架構設計

### Bounded Context Map

```
┌─────────────────────────────────────────────────────────────────┐
│                      銀行系統架構                                │
├─────────────────┬─────────────────┬─────────────────┬───────────┤
│   帳戶管理 Context │  交易處理 Context │  客戶管理 Context │  報表 Context │
├─────────────────┼─────────────────┼─────────────────┼───────────┤
│ BankAccount     │ Transaction     │ Customer        │ Report    │
│ AccountService  │ TransferService │ CustomerService │ ReportGen │
└────────┬────────┴────────┬────────┴────────┬────────┴────┬────┘
         │                 │                  │             │
         └─────────────────┴──────────────────┴─────────────┘
                            │
                   Polyglot Persistence
                            │
         ┌──────────────────┼──────────────────┐
         ▼                  ▼                  ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  MongoDB    │    │   Redis     │    │  Cassandra  │
│ 帳戶/交易   │    │ Session/快取 │    │  交易日誌   │
└─────────────┘    └─────────────┘    └─────────────┘
```

### Aggregate 識別與邊界定義

| Aggregate | Collection | 職責 |
|-----------|------------|------|
| BankAccount | accounts | 帳戶餘額、狀態管理 |
| Transaction | transactions | 交易記錄、歷史 |
| Customer | customers | 客戶資訊、KYC |
| Loan | loans | 貸款管理 |

### Event Flow 設計

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  存款事件   │───▶│ Event Store  │───▶│ CQRS Read   │
│             │    │              │    │   Model     │
└─────────────┘    └──────┬──────┘    └─────────────┘
                          │
         ┌────────────────┼────────────────┐
         ▼                ▼                ▼
  ┌────────────┐  ┌────────────┐  ┌────────────┐
  │ Change      │  │ 通知Service │  │ 風控Service │
  │ Stream      │  │             │  │            │
  └────────────┘  └────────────┘  └────────────┘
```

---

## M19-LAB-01: 完整系統實作

### 實作重點

1. **Hexagonal Architecture 全模組實作**
2. **Event Sourcing + CQRS 帳戶交易**
3. **SAGA: 跨帳戶轉帳**
4. **Change Streams: 即時餘額更新**
5. **完整 BDD Test Suite (10+ Scenarios)**
6. **效能測試: 10 萬帳戶、100 萬交易**
7. **可觀測性: Metrics + Logging + Tracing**

### 程式碼結構

```
src/main/java/com/course/banking/
├── domain/
│   ├── model/
│   │   ├── BankAccount.java
│   │   ├── Transaction.java
│   │   └── Money.java
│   ├── repository/
│   │   ├── AccountRepository.java (Port)
│   │   └── TransactionRepository.java (Port)
│   ├── service/
│   │   ├── AccountService.java
│   │   └── TransferService.java
│   └── events/
│       ├── AccountOpenedEvent.java
│       ├── FundsDepositedEvent.java
│       └── FundsTransferredEvent.java
├── infrastructure/
│   └── persistence/
│       ├── MongoAccountRepository.java
│       ├── MongoTransactionRepository.java
│       └── EventStoreAdapter.java
├── application/
│   ├── command/
│   │   ├── OpenAccountCommand.java
│   │   └── TransferCommand.java
│   └── query/
│       ├── AccountSummaryQuery.java
│       └── TransactionHistoryQuery.java
└── api/
    ├── AccountController.java
    └── TransferController.java
```

### BDD Test Suite

```gherkin
Feature: 銀行帳戶管理
  Scenario: 開戶並存款
    Given 新客戶 "張三" 通過 KYC
    When 開立帳戶並存款 10000 元
    Then 帳戶狀態為 ACTIVE
    And 餘額為 10000 元

  Scenario: 帳戶間轉帳
    Given 帳戶 A001 餘額 50000 元
    And 帳戶 A002 餘額 10000 元
    When A001 轉帳 20000 元至 A002
    Then A001 餘額為 30000 元
    And A002 餘額為 30000 元
    And 產生轉帳交易記錄

  Scenario: 轉帳失敗餘額不足
    Given 帳戶 A001 餘額 5000 元
    When 轉帳 10000 元至 A002
    Then 轉帳失敗
    And 餘額維持不變

  Scenario: 帳戶冻结
    Given 帳戶 A001 狀態 ACTIVE
    When 因可疑交易冻结帳戶
    Then 帳戶狀態為 FROZEN
    And 無法提款
```

### 效能測試

```java
@Test
void performanceTest_100kAccounts_1mTransactions() {
    // 建立 10 萬帳戶
    // 建立 100 萬交易記錄
    // 查詢效能 < 100ms
}
```

### 可觀測性

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, prometheus, loggers
  metrics:
    tags:
      application: banking-service
    export:
      prometheus:
        enabled: true
```
