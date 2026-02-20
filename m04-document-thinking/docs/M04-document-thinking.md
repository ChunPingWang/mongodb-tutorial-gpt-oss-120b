# M04: Document 思維與基礎建模

## 學習目標
從 RDB 正規化思維轉換至 Document 建模思維

---

## M04-DOC-01: Document 建模原則

### 核心原則
> 「一起讀取的資料，一起儲存」

### Embedding vs Referencing 決策矩陣

| 關係類型 | 建議策略 | 時機 |
|---------|---------|------|
| 1:1 (經常一起讀取) | 嵌入 | 帳戶 + 帳戶狀態 |
| 1:N (數量有限, < 100) | 嵌入 | 客戶 + 地址列表 |
| 1:N (數量無限) | 引用 | 客戶 + 交易記錄 |
| N:N | 引用 | 學生 + 課程 |

### Anti-Pattern

1. **過度嵌入** - 文件過大，影響效能
2. **無限增長的陣列** - 交易記錄應該獨立 Collection

---

## M04-DOC-02: BSON 資料類型與 Java 映射

### BSON Types → Java Types

| BSON Type | Java Type |
|-----------|-----------|
| String | String |
| Int32 | Integer |
| Int64 | Long |
| Double | Double |
| Boolean | Boolean |
| Date | java.util.Date / java.time.LocalDateTime |
| ObjectId | org.bson.types.ObjectId |
| Decimal128 | java.math.BigDecimal |
| Array | List |
| Object | Document |

### ObjectId 生成策略

```java
@Id
private String id; // MongoDB 自動生成 ObjectId

// 或手動指定
@Id
private ObjectId id;
```

### Decimal128 - 金融計算

```java
// 使用 BigDecimal 避免浮點數精度問題
private BigDecimal balance;
```

---

## M04-DOC-03: 金融場景建模實戰

### 案例 1: 銀行客戶 Profile

```json
{
  "_id": "cust_001",
  "name": "張三",
  "email": "zhangsan@example.com",
  "kyc": {
    "verified": true,
    "verifiedAt": "2024-01-01T00:00:00Z",
    "riskLevel": "LOW"
  },
  "addresses": [
    {
      "type": "REGISTERED",
      "city": "台北市",
      "district": "信義區",
      "detail": "忠孝東路一段100號"
    }
  ]
}
```

### 案例 2: 保險保單

```json
{
  "_id": "policy_001",
  "policyNumber": "POL-2024-001",
  "status": "ACTIVE",
  "insured": {
    "name": "李四",
    "idNumber": "A123456789"
  },
  "coverages": [
    {
      "type": "火灾险",
      "amount": 1000000,
      "premium": 5000
    }
  ],
  "claims": [] // 理赔记录 - 引用
}
```

### 案例 3: 電商商品

```json
{
  "_id": "prod_001",
  "name": "iPhone 15 Pro",
  "category": "手機",
  "variants": [
    {
      "sku": "IP15P-256-BLK",
      "color": "黑色",
      "storage": "256GB",
      "price": 36900,
      "stock": 100
    }
  ],
  "images": [
    "/images/iphone15-1.jpg",
    "/images/iphone15-2.jpg"
  ]
}
```

---

## M04-LAB-01: Embedding vs Referencing 實驗

### TDD: 銀行客戶 + 帳戶建模

**方案 A: 帳戶嵌入客戶文件**

```json
{
  "_id": "cust_001",
  "accounts": [
    { "accountNumber": "A001", "balance": 50000 },
    { "accountNumber": "A002", "balance": 100000 }
  ]
}
```

**方案 B: 帳戶獨立 Collection + 客戶引用**

```json
// customers collection
{ "_id": "cust_001", "name": "張三" }

// accounts collection
{ "_id": "acc_001", "customerId": "cust_001", "balance": 50000 }
```

### 測試比較

1. 查詢效能
2. 更新成本
3. 資料一致性

---

## M04-LAB-02: Java 23 特性與 MongoDB 整合

### Record Class 作為 Value Object

```java
public record Money(
    BigDecimal amount,
    String currency
) {}
```

### Sealed Interface 定義有限的文件類型

```java
public sealed interface FinancialProduct 
    permits Deposit, Fund, InsuranceProduct {
    String getProductId();
}
```

### Pattern Matching 處理多型

```java
public BigDecimal calculateValue(FinancialProduct product) {
    return switch (product) {
        case Deposit d -> d.getPrincipal()
            .multiply(d.getRate())
            .divide(BigDecimal.valueOf(100));
        case Fund f -> f.getNav().multiply(f.getUnits());
        case InsuranceProduct i -> i.getCoverageAmount();
    };
}
```
