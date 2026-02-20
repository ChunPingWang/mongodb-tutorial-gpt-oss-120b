# M15: 索引策略與效能調優

## 學習目標
設計有效的索引策略，優化查詢效能

---

## M15-DOC-01: MongoDB 索引類型全解

### 索引類型

| 類型 | 說明 | 範例 |
|------|------|------|
| Single Field Index | 單一欄位索引 | `{ status: 1 }` |
| Compound Index | 複合索引 | `{ status: 1, date: -1 }` |
| Multikey Index | 陣列欄位索引 | `{ tags: 1 }` |
| Text Index | 全文搜尋 | `{ description: "text" }` |
| Geospatial Index | 地理空間 | `{ location: "2dsphere" }` |
| Hashed Index | 雜湊索引 | `{ _id: "hashed" }` |
| Partial Index | 部分索引 | `{ status: 1 }` (只索引 status: ACTIVE) |
| Sparse Index | 稀疏索引 | `{ email: 1 }` (只索引非 null) |
| TTL Index | 時間戳索引 | `{ createdAt: 1 }` (自動刪除) |
| Unique Index | 唯一索引 | `{ accountNumber: 1 }` (unique: true) |

### 建立索引

```java
@Document(collection = "transactions")
public class Transaction {
    @Indexed
    private String accountId;
    
    @Indexed(direction = IndexDirection.DESCENDING)
    private LocalDateTime date;
    
    @CompoundIndex(name = "account_date_idx", 
                   def = "{'accountId': 1, 'date': -1}")
    private String type;
    
    @Text
    private String description;
}
```

---

## M15-DOC-02: ESR 規則與索引設計

### ESR 原則

> **E**quality → **S**ort → **R**ange

1. **Equality**: 精確匹配的欄位放最前面
2. **Sort**: 排序的欄位放中間
3. **Range**: 範圍查詢的欄位放最後

### 範例

```java
// 查詢: 找出 status=ACTIVE 的帳戶，按餘額排序
// 正確索引: { status: 1, balance: 1 }
// 錯誤索引: { balance: 1, status: 1 }
```

### Covered Query

```javascript
// 索引: { accountId: 1, balance: 1 }
// 查詢: db.accounts.find({ accountId: "A001" }, { balance: 1, _id: 0 })
// 結果: IXSCAN (不需讀取文件)
```

### explain() 輸出解讀

```json
{
  "stage": "IXSCAN",
  "indexName": "accountId_1_date_-1",
  "docsExamined": 1000,
  "nReturned": 100
}
```

| 欄位 | 說明 |
|------|------|
| COLLSCAN | 全 Collection 掃描 (需優化) |
| IXSCAN | 使用索引 |
| docsExamined | 檢查的文件數 |
| nReturned | 回傳的文件數 |

---

## M15-DOC-03: 效能基準測試方法論

### 測試環境標準化

```java
@Testcontainers
public class PerformanceTest {
    
    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");
    
    @BeforeEach
    void setup() {
        // 生成 100 萬筆測試資料
        bulkInsert(1_000_000);
    }
    
    @Test
    void testQueryPerformance() {
        Instant start = Instant.now();
        
        List<Transaction> results = repository
            .findByAccountIdAndDateBetween("A001", startDate, endDate);
        
        Instant end = Instant.now();
        long ms = Duration.between(start, end).toMillis();
        
        assertThat(ms).isLessThan(100); // < 100ms
    }
}
```

---

## M15-LAB-01: 銀行交易查詢效能調優

### BDD Feature

```gherkin
Feature: 交易查詢效能
  Scenario: 大量資料下的查詢效能
    Given 帳戶 "A001" 有 1000000 筆交易記錄
    When 查詢最近 30 天的交易記錄
    Then 查詢回應時間小於 100ms
    And 使用 IXSCAN 而非 COLLSCAN
```

### Compound Index 最佳化

```java
@CompoundIndex(
    name = "account_date_type_idx",
    def = "{'accountId': 1, 'date': -1, 'type': 1}"
)
public class Transaction { }
```

### TTL Index: 自動清理

```java
@Indexed(value = IndexDirection.ASCENDING, 
         expireAfterSeconds = 90 * 24 * 60 * 60) // 90 天
private LocalDateTime createdAt;
```

---

## M15-LAB-02: 電商商品搜尋效能

### Text Index + Compound Index

```java
@CompoundIndex(name = "category_price_idx",
               def = "{'category': 1, 'price': 1}")

@Text
private String name;
@Text
private String description;
```

### Partial Index

```java
// 只索引上架中的商品
@Indexed(partialFilter = "{ 'status': { $eq: 'AVAILABLE' } }")
private ProductStatus status;
```

### 測試：10 萬商品效能

```java
@Test
void searchPerformance() {
    // 建立索引前: 5000ms
    // 建立索引後: 50ms
}
```
