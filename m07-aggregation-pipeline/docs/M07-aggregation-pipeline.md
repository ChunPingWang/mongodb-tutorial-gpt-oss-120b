# M07: Aggregation Pipeline

## 學習目標
掌握 MongoDB Aggregation Framework 進行複雜資料分析

---

## M07-DOC-01: Aggregation Pipeline 概念與階段

### Pipeline 思維

```
┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐
│  $match │ → │  $group │ → │ $sort   │ → │ $project│
│  篩選   │   │  分組   │   │  排序   │   │  投影   │
└─────────┘   └─────────┘   └─────────┘   └─────────┘
```

### 核心階段

| 階段 | 說明 | SQL 對應 |
|------|------|---------|
| $match | 篩選文件 | WHERE |
| $group | 分組彙總 | GROUP BY |
| $project | 投影/選擇欄位 | SELECT |
| $sort | 排序 | ORDER BY |
| $limit | 限制數量 | LIMIT |
| $skip | 跳過數量 | OFFSET |
| $lookup | 關聯查詢 | JOIN |
| $unwind | 展開陣列 | - |
| $facet | 多維度分析 | - |
| $bucket | 分組統計 | - |

---

## M07-DOC-02: Spring Data MongoDB Aggregation API

### 基本用法

```java
Aggregation aggregation = Aggregation.newAggregation(
    match(Criteria.where("status").is(AccountStatus.ACTIVE)),
    group("accountNumber")
        .sum("balance").as("totalBalance")
        .count().as("accountCount"),
    project("totalBalance", "accountCount")
        .and("_id").as("accountNumber")
);

AggregationResults<MonthlySummary> results = 
    mongoTemplate.aggregate(aggregation, "transactions", MonthlySummary.class);
```

### TypedAggregation

```java
TypedAggregation<Transaction> aggregation = Aggregation.newAggregation(
    Transaction.class,
    match(Criteria.where("accountId").is(accountId)),
    group("type")
        .sum("amount").as("totalAmount")
        .count().as("count")
);
```

---

## M07-LAB-01: 銀行報表 Aggregation

### BDD Feature

```gherkin
Feature: 月度交易報表
  Scenario: 帳戶月度收支摘要
    Given 帳戶 "A001" 有 2024 年度的交易記錄
    When 產出月度收支摘要報表
    Then 每月顯示收入總額、支出總額、淨額
    And 包含交易筆數統計

  Scenario: 客戶資產配置分析
    Given 客戶 "C001" 持有多個帳戶
    When 執行資產配置分析
    Then 顯示各帳戶類型的餘額占比
```

### 實作：月度彙總

```java
Aggregation aggregation = Aggregation.newAggregation(
    match(Criteria.where("accountId").is(accountId)
        .and("date").gte(startDate).lte(endDate)),
    project("type", "amount", "date")
        .and("month").extractMonth("date"),
    group("month")
        .sum(ConditionalOperators
            .when(Criteria.where("type").is("DEPOSIT"))
            .then("$amount")
            .otherwise(0))
        .as("totalIncome")
        .sum(ConditionalOperators
            .when(Criteria.where("type").is("WITHDRAWAL"))
            .then("$amount")
            .otherwise(0))
        .as("totalExpense")
);
```

---

## M07-LAB-02: 保險理賠統計

### $facet 多維度統計

```java
Aggregation aggregation = Aggregation.newAggregation(
    facet(
        // 按險種統計
        group("coverageType")
            .sum("amount").as("totalAmount")
            .count().as("claimCount"),
        // 按地區統計
        group("region")
            .sum("amount").as("totalAmount")
    ).as("byCoverage", "byRegion")
);
```

---

## M07-LAB-03: 電商銷售分析

### 漏斗分析

```java
Aggregation funnelAggregation = Aggregation.newAggregation(
    // 瀏覽
    group("sessionId").first("view").as("eventType"),
    // 加入購物車
    group().addToSet("cart").as("events"),
    // 下單
    group().addToSet("order").as("events"),
    // 付款
    group().addToSet("payment").as("events")
);
```

### 商品排行榜

```java
Aggregation rankAggregation = Aggregation.newAggregation(
    match(Criteria.where("status").is("COMPLETED")),
    group("productId")
        .sum("quantity").as("totalSold")
        .sum("totalPrice").as("totalRevenue"),
    sort(Sort.Direction.DESC, "totalSold"),
    limit(100)
);
```
