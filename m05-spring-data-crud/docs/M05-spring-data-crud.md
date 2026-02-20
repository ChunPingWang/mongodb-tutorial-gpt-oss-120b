# M05: CRUD 操作與 Repository Pattern

## 學習目標
掌握 Spring Data MongoDB 的基本 CRUD 操作

---

## M05-DOC-01: Spring Data MongoDB Repository 體系

### Repository 層級

```
MongoRepository<T, ID>
    │
    ├── MongoTemplate (底層操作)
    │
    └── 自訂 Repository (Fragment Interface Pattern)
```

### 核心註解

| 註解 | 用途 |
|------|------|
| @Document | 標註 Collection 名稱 |
| @Id | 標註主鍵欄位 |
| @Field | 欄位名稱映射 |
| @Indexed | 建立索引 |
| @CompoundIndex | 複合索引 |

### Repository 範例

```java
public interface BankAccountRepository extends MongoRepository<BankAccount, String> {
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    List<BankAccount> findByStatus(AccountStatus status);
}
```

---

## M05-DOC-02: OOP 與 DDD 映射策略

### Entity vs Value Object

| 類型 | 特性 | MongoDB 表現 |
|------|------|-------------|
| Entity | 有身份、可變 | 獨立 Document，含 @Id |
| Value Object | 無身份、不可變 | 嵌入 Document 或使用 Java Record |

### Aggregate Root → Collection

```java
@Document(collection = "bank_accounts")
public class BankAccount {
    @Id
    private String id;
    private String accountNumber;
    private Money balance;
    private AccountStatus status;
    private List<Transaction> transactions; // 嵌入
}
```

### Java Record 作為 Value Object

```java
public record Money(BigDecimal amount, String currency) {
    public static Money of(BigDecimal amount) {
        return new Money(amount, "TWD");
    }
}
```

---

## M05-LAB-01: 銀行帳戶 CRUD

### BDD Feature

```gherkin
Feature: 銀行帳戶管理
  Scenario: 開立新帳戶
    Given 客戶 "張三" 已通過 KYC 驗證
    When 開立活期存款帳戶 初始餘額 10000 元
    Then 帳戶狀態為 ACTIVE
    And 帳戶餘額為 10000 元

  Scenario: 帳戶凍結
    Given 帳戶 "A001" 狀態為 ACTIVE
    When 因可疑交易執行凍結
    Then 帳戶狀態為 FROZEN
    And 無法執行提款操作
```

### Domain Model

```java
@Document(collection = "bank_accounts")
public class BankAccount {
    @Id
    private String id;
    private String accountNumber;
    private Money balance;
    private AccountStatus status;
    
    public void deposit(Money amount) {
        this.balance = this.balance.add(amount);
    }
    
    public void withdraw(Money amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        this.balance = this.balance.subtract(amount);
    }
}
```

---

## M05-LAB-02: 保險保單 CRUD

### BDD Feature

```gherkin
Feature: 保險保單管理
  Scenario: 建立新保單
    Given 客戶 "李四" 申請保險
    When 建立保單 保費 5000 元
    Then 保單狀態為 PENDING
    And 保單號碼已產生

  Scenario: 保單批改
    Given 保單 "POL-001" 狀態為 ACTIVE
    When 調整保費為 6000 元
    Then 保單更新成功
    And 產生保單異動記錄
```

### 保單狀態機

```
PENDING → ACTIVE → EXPIRED
           ↓
        CANCELLED
```

---

## M05-LAB-03: 電商商品 CRUD

### BDD Feature

```gherkin
Feature: 電商商品管理
  Scenario: 商品上架
    Given 新商品 "iPhone 15"
    When 上架商品 價格 36900 元
    Then 商品狀態為 AVAILABLE
    And 庫存為 0

  Scenario: 庫存更新
    Given 商品 "P001" 庫存 10 件
    When 扣減庫存 5 件
    Then 庫存變為 5 件
    And 庫存不可為負數
```
