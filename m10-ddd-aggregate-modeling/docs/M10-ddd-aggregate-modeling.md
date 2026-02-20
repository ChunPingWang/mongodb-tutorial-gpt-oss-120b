# M10: DDD Aggregate 建模

## 學習目標
將 DDD Aggregate Pattern 落實於 MongoDB Document 設計

---

## M10-DOC-01: Aggregate Root 與 MongoDB Collection 映射

### Aggregate 邊界 = Document 邊界 = Transaction 邊界

```
┌─────────────────────────────────────────────────┐
│  Aggregate Root (BankAccount)                   │
│  ┌───────────────────────────────────────────┐  │
│  │  id: String                               │  │
│  │  accountNumber: String                    │  │
│  │  balance: Money                           │  │
│  │  transactions: List<Transaction>         │  │ ← 同一 Document
│  │  status: AccountStatus                    │  │
│  └───────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

### Aggregate 內部一致性

```java
@Document(collection = "bank_accounts")
public class BankAccount {
    @Id
    private String id;
    private Money balance;
    private AccountStatus status;
    
    public void withdraw(Money amount) {
        if (status == AccountStatus.FROZEN) {
            throw new AccountFrozenException();
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        this.balance = this.balance.subtract(amount);
    }
}
```

### Aggregate 間的最終一致性

```
BankAccount Aggregate          LoanApplication Aggregate
┌─────────────────┐           ┌─────────────────┐
│ 帳戶 A001       │           │ 貸款申請 L001   │
│ 餘額: 50000     │           │ 申請人: 張三    │
└────────┬────────┘           └────────┬────────┘
         │                             │
         │    Domain Event:            │
         │    LoanApproved ────────────┼────► 帳戶連動
```

---

## M10-DOC-02: Hexagonal Architecture + MongoDB

### 架構圖

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                         │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              TransferService                         │    │
│  │  - transfer(from, to, amount)                      │    │
│  └──────────────────────┬──────────────────────────────┘    │
│                         │                                     │
│  ┌──────────────────────┴──────────────────────────────┐    │
│  │                    Port (Interface)                   │    │
│  │  ┌─────────────────────────────────────────────┐     │    │
│  │  │    AccountRepository                         │     │    │
│  │  │    - save(Account)                           │     │    │
│  │  │    - findById(id)                            │     │    │
│  │  └─────────────────────────────────────────────┘     │    │
│  └──────────────────────┬──────────────────────────────┘    │
└─────────────────────────┼────────────────────────────────────┘
                          │
┌─────────────────────────┼────────────────────────────────────┐
│                         ▼                                     │
│  ┌─────────────────────────────────────────────────────┐     │
│  │              Adapter (Implementation)               │     │
│  │  ┌─────────────────────────────────────────────┐   │     │
│  │  │    MongoAccountRepository                    │   │     │
│  │  │    extends MongoRepository<Account, String> │   │     │
│  │  └─────────────────────────────────────────────┘   │     │
│  └─────────────────────────────────────────────────────┘     │
│                    Infrastructure Layer                       │
└──────────────────────────────────────────────────────────────┘
```

### 目錄結構

```
src/main/java/com/course/
├── domain/
│   ├── model/
│   │   ├── BankAccount.java
│   │   └── Money.java
│   ├── repository/
│   │   └── AccountRepository.java (Port)
│   └── service/
│       └── TransferService.java
├── infrastructure/
│   └── persistence/
│       └── MongoAccountRepository.java (Adapter)
└── application/
    └── TransferApplicationService.java
```

---

## M10-DOC-03: Rich Domain Model 在 MongoDB 的實踐

### Domain Entity 行為方法

```java
@Document
public class BankAccount {
    @Id
    private String id;
    private Money balance;
    private AccountStatus status;
    
    // 行為方法 - 不是 Anemic Model
    public void deposit(Money amount) {
        this.balance = this.balance.add(amount);
    }
    
    public void withdraw(Money amount) {
        if (this.status == AccountStatus.FROZEN) {
            throw new AccountFrozenException();
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        this.balance = this.balance.subtract(amount);
    }
    
    public void freeze() {
        this.status = AccountStatus.FROZEN;
    }
}
```

### Domain Event

```java
public record AccountCreatedEvent(
    String accountId,
    String accountNumber,
    Money initialBalance,
    Instant timestamp
) implements DomainEvent { }

public class BankAccount {
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    public void deposit(Money amount) {
        this.balance = this.balance.add(amount);
        this.domainEvents.add(
            new FundsDepositedEvent(this.id, amount, Instant.now())
        );
    }
    
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
}
```

---

## M10-LAB-01: 銀行貸款申請 Aggregate

### BDD Feature

```gherkin
Feature: 貸款申請流程
  Scenario: 提交貸款申請
    Given 客戶 "C001" 信用評分 750
    When 提交房貸申請 金額 5000000 元 期限 20 年
    Then 貸款申請狀態為 SUBMITTED
    And 產生 LoanApplicationSubmitted 領域事件

  Scenario: 自動初審
    Given 貸款申請 "L001" 狀態為 SUBMITTED
    When 系統執行自動初審
    And 申請人年收入大於年還款額的 3 倍
    Then 初審結果為 PASSED
    And 進入人工複審階段
```

### Hexagonal Architecture 實作

```java
// Domain Layer
@Document(collection = "loan_applications")
public class LoanApplication {
    @Id
    private String id;
    private String customerId;
    private Money amount;
    private Integer termYears;
    private LoanApplicationStatus status;
    
    public void submit() {
        this.status = LoanApplicationStatus.SUBMITTED;
    }
}

// Port
public interface LoanApplicationRepository {
    Optional<LoanApplication> findById(String id);
    LoanApplication save(LoanApplication application);
}

// Adapter
@Repository
public class MongoLoanApplicationRepository 
    implements LoanApplicationRepository {
    // MongoDB 實作
}
```

---

## M10-LAB-02: 保險理賠 Aggregate

### Claim Aggregate Root

```java
@Document(collection = "claims")
public class Claim {
    @Id
    private String id;
    private String policyId;
    private String claimantId;
    private ClaimStatus status;
    private List<ClaimItem> items;
    private Money totalAmount;
    
    public void addItem(ClaimItem item) {
        this.items.add(item);
        recalculateTotal();
    }
    
    private void recalculateTotal() {
        this.totalAmount = items.stream()
            .map(ClaimItem::getAmount)
            .reduce(Money.ZERO, Money::add);
    }
}
```

---

## M10-LAB-03: 電商訂單 Aggregate

### Order Aggregate Root

```java
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String customerId;
    private List<OrderLine> lines;
    private ShippingAddress shippingAddress;
    private PaymentInfo paymentInfo;
    private OrderStatus status;
    
    // 狀態機
    public void pay() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Order must be in CREATED status");
        }
        this.status = OrderStatus.PAID;
    }
    
    public void ship() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("Order must be PAID to ship");
        }
        this.status = OrderStatus.SHIPPED;
    }
}
```

### 訂單狀態機

```
CREATED → PAID → SHIPPED → DELIVERED → COMPLETED
                        ↓
                      CANCELLED (from CREATED, PAID)
                        ↓
                      RETURNED (from DELIVERED)
```
