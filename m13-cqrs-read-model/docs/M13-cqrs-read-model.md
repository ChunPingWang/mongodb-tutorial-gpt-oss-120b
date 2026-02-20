# M13: CQRS Read Model

## 學習目標
實作 Command/Query 分離，優化讀取效能

---

## M13-DOC-01: CQRS 架構與 MongoDB

### Write Model vs Read Model

```
┌─────────────────────────────────────────────────────────────┐
│                       Write Model                          │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  BankAccount Aggregate                               │   │
│  │  - id, accountNumber, balance, transactions         │   │
│  │  - deposit(), withdraw() 方法                       │   │
│  └─────────────────────────────────────────────────────┘   │
│                           │                                 │
│                           ▼ Domain Events                   │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│ AccountSummary│   │ Customer360   │   │ Transaction   │
│    View       │   │    View       │   │    History    │
│               │   │               │   │               │
│ 帳戶餘額摘要  │   │  客戶360視圖  │   │  交易歷史     │
└───────────────┘   └───────────────┘   └───────────────┘
```

### Projection: 從 Event 建構 Read Model

```java
@Component
public class Customer360Projection {
    
    @EventListener
    public void handle(AccountCreatedEvent event) {
        Customer360View view = new Customer360View();
        view.setCustomerId(event.getCustomerId());
        view.setTotalAccounts(1);
        view.setTotalBalance(event.getInitialBalance());
        customerViewRepository.save(view);
    }
    
    @EventListener
    public void handle(FundsDepositedEvent event) {
        customerViewRepository.findByCustomerId(event.getCustomerId())
            .ifPresent(view -> {
                view.setTotalBalance(
                    view.getTotalBalance().add(event.getAmount())
                );
                customerViewRepository.save(view);
            });
    }
}
```

### Eventually Consistent Read Model

```
Command → Write Model → Event → Projection → Read Model
                              │
                              └── 延遲同步 (ms ~ sec)
```

---

## M13-DOC-02: Read Model 設計策略

### Query-Driven Design

1. 先定義查詢需求
2. 設計對應的 Read Model
3. 實作 Projection

### Denormalization 策略

```json
// Read Model: 客戶 360 視圖
{
  "customerId": "C001",
  "customerName": "張三",
  "totalAccounts": 3,
  "totalBalance": 500000,
  "totalLoans": 2,
  "loanBalance": 300000,
  "recentTransactions": [
    { "date": "2024-01-20", "type": "DEPOSIT", "amount": 10000 }
  ],
  "products": ["基金", "保險", "定存"]
}
```

### Multiple Read Model

```
Write Model (BankAccount)
        │
        ├──→ AccountSummaryView (列表頁)
        ├──→ AccountDetailView (詳情頁)
        └──→ AccountHistoryView (歷史頁)
```

---

## M13-LAB-01: 銀行客戶 360 度視圖

### BDD Feature

```gherkin
Feature: 客戶 360 度視圖
  Scenario: 查詢客戶綜合資訊
    Given 客戶 "C001" 有帳戶、貸款、投資等多種業務
    When 查詢客戶 360 度視圖
    Then 回傳整合後的客戶資訊，包含:
      | 區塊     | 內容               |
      | 基本資料 | 姓名、等級、VIP 狀態 |
      | 資產摘要 | 存款總額、投資總額   |
      | 負債摘要 | 貸款餘額、信用卡額度 |
      | 近期活動 | 最近 5 筆交易       |
```

### Projection Handler

```java
@Component
public class Customer360ProjectionHandler {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @EventListener
    public void handleAccountEvent(DomainEvent event) {
        switch (event) {
            case AccountOpenedEvent e -> updateView(e);
            case FundsDepositedEvent e -> updateBalance(e);
            case FundsWithdrawnEvent e -> updateBalance(e);
            // ...
        }
    }
    
    private void updateView(AccountOpenedEvent event) {
        Query query = new Query(Criteria.where("customerId").is(event.getCustomerId()));
        Update update = new Update()
            .inc("totalAccounts", 1)
            .set("lastUpdated", Instant.now());
        mongoTemplate.updateFirst(query, update, "customer_360_views");
    }
}
```

---

## M13-LAB-02: 電商商品列表頁 Read Model

### Write Model: Product Aggregate

```java
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private List<ProductImage> images;
    private List<Variant> variants;
    private List<Review> reviews;
    private ProductStatus status;
}
```

### Read Model: ProductListView

```java
@Document(collection = "product_list_views")
public class ProductListView {
    @Id
    private String id;
    private String name;
    private BigDecimal price;
    private String primaryImage;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private Boolean inStock;
}
```

### Read Model: ProductSearchView

```java
@Document(collection = "product_search_views")
public class ProductSearchView {
    @Id
    private String id;
    private String name;
    private String description;
    private String category;
    private List<String> keywords;
    private BigDecimal price;
    private Map<String, Object> facets;
}
```
