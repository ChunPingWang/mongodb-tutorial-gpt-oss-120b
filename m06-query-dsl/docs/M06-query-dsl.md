# M06: 查詢 DSL 與方法名稱推導

## 學習目標
精通 Spring Data MongoDB 的多種查詢方式

---

## M06-DOC-01: 查詢方法四層體系

### Level 1: Method Name Derivation

```java
// 自動轉換為 MongoDB 查詢
List<BankAccount> findByStatusAndBalanceGreaterThan(AccountStatus status, BigDecimal balance);

// 轉換為: { status: "ACTIVE", balance: { $gt: 10000 } }
```

### Level 2: @Query 手寫 JSON

```java
@Query("{ 'accountNumber': { $regex: ?0 } }")
List<BankAccount> findByAccountNumberPattern(String pattern);
```

### Level 3: Criteria API 動態組合

```java
Criteria criteria = new Criteria();
if (status != null) {
    criteria.and("status").is(status);
}
if (minBalance != null) {
    criteria.and("balance").gte(minBalance);
}
Query query = new Query(criteria);
```

### Level 4: MongoTemplate 完全控制

```java
Query query = new Query();
query.addCriteria(Criteria.where("status").is(AccountStatus.ACTIVE));
query.with(Sort.by(Sort.Direction.DESC, "balance"));
query.limit(10);
List<BankAccount> accounts = mongoTemplate.find(query, BankAccount.class);
```

---

## M06-DOC-02: 複雜查詢模式

### 巢狀文件查詢

```java
// 查詢地址城市為台北的客戶
List<Customer> findByAddressCity(String city);

// $條件
@Query("{ $or: [ {or  'status': ?0 }, { 'balance': { $gt: ?1 } } ] }")
List<BankAccount> findByStatusOrBalance(AccountStatus status, BigDecimal minBalance);
```

### 陣列查詢

```java
// $elemMatch - 陣列中至少一個元素符合條件
@Query("{ 'transactions': { $elemMatch: { 'type': 'DEPOSIT', 'amount': { $gte': 10000 } } } }")
List<BankAccount> findWithLargeDeposits();

// $in - 值在列表中
List<BankAccount> findByStatusIn(List<AccountStatus> statuses);
```

### 地理空間查詢

```java
@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
private Point location;

Near query = new NearQuery(Distance.of(5, Metrics.KILOMETERS))
    .maxDistance(new Point(new GeoJsonPoint(lng, lat)).getGeometry());
List<Branch> branches = mongoTemplate.find(query, Branch.class);
```

---

## M06-LAB-01: 銀行交易查詢

### BDD Feature

```gherkin
Feature: 交易記錄查詢
  Scenario: 依日期區間查詢交易
    Given 帳戶 "A001" 有 100 筆交易記錄
    When 查詢 2024-01-01 至 2024-03-31 的交易
    Then 回傳該區間內的交易記錄
    And 按交易日期降冪排序

  Scenario: 依條件組合查詢
    Given 帳戶 "A001" 有多種類型交易
    When 查詢金額大於 50000 的轉帳交易
    Then 只回傳符合條件的交易記錄
```

---

## M06-LAB-02: 保險理賠查詢

### 複雜查詢實作

```java
// 巢狀查詢：查詢含特定保障項目的保單
@Query("{ 'coverages.type': ?0, 'claims.status': ?1, 'claims.amount': { $gte: ?2 } }")
List<InsurancePolicy> findByCoverageAndClaimStatus(String coverageType, ClaimStatus status, BigDecimal minAmount);
```

---

## M06-LAB-03: 電商商品搜尋

### 全文搜尋

```java
@Indexed(type = Text)
private String name;
@Indexed(type = Text)
private String description;

// 搜尋
@Query("{ $text: { $search: ?0 } }")
List<Product> searchByKeyword(String keyword);
```

### 多維篩選

```java
List<Product> findByCategoryAndPriceBetweenAndStatus(
    String category,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    ProductStatus status
);
```
