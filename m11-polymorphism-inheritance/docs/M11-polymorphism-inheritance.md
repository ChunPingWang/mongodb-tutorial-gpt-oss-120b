# M11: 多型與繼承建模

## 學習目標
運用 OOP 多型概念在 MongoDB 實現靈活的文件結構

---

## M11-DOC-01: MongoDB 多型文件策略

### Single Collection Polymorphism

使用 `_class` 欄位區分文件類型：

```json
{ "_id": "d1", "_class": "com.course.Deposit", "rate": 1.5 }
{ "_id": "d2", "_class": "com.course.Fund", "nav": 15.32 }
```

### Multiple Collection Inheritance

每個子類型一個 Collection：

```
products/
├── deposits/
├── funds/
└── insurance/
```

### 與 JPA 繼承策略比較

| JPA Strategy | MongoDB 對應 |
|--------------|-------------|
| SINGLE_TABLE | Single Collection + _class |
| TABLE_PER_CLASS | Multiple Collection |
| JOINED | Single Collection + 巢狀 |

---

## M11-DOC-02: Java Sealed Interface + MongoDB

### Sealed Interface 定義

```java
public sealed interface FinancialProduct 
    permits Deposit, Fund, InsuranceProduct {
    
    String getProductId();
    String getProductName();
    BigDecimal calculateValue();
}
```

### 實作類別

```java
public record Deposit(
    String productId,
    String productName,
    BigDecimal principal,
    BigDecimal rate,
    LocalDate maturity
) implements FinancialProduct {
    
    @Override
    public BigDecimal calculateValue() {
        // 定存利息計算
        return principal.multiply(rate)
            .divide(BigDecimal.valueOf(100));
    }
}

public record Fund(
    String productId,
    String productName,
    BigDecimal nav,
    BigDecimal units,
    RiskLevel riskLevel
) implements FinancialProduct {
    
    @Override
    public BigDecimal calculateValue() {
        return nav.multiply(units);
    }
}
```

### @TypeAlias 控制 _class 欄位

```java
@TypeAlias("deposit")
public record Deposit(...) {}

@TypeAlias("fund")
public record Fund(...) {}
```

---

## M11-LAB-01: 銀行金融商品多型

### BDD Feature

```gherkin
Feature: 多型金融商品管理
  Scenario: 儲存不同類型的金融商品
    Given 以下金融商品:
      | 類型   | 名稱     | 特有屬性             |
      | 定存   | 一年定存 | 年利率: 1.5%         |
      | 基金   | 全球股票 | 淨值: 15.32, 風險等級: 5 |
      | 保險   | 年金險   | 繳費年期: 20, 保額: 1000000 |
    When 全部存入 products Collection
    Then 可用統一介面查詢所有商品
    And 可依類型篩選特定商品
```

### Repository 實作

```java
public interface FinancialProductRepository 
    extends MongoRepository<FinancialProduct, String> {
    
    // 查詢所有商品
    List<FinancialProduct> findAll();
    
    // 依類型查詢 - 使用 _class 欄位
    List<FinancialProduct> findByProductType(String type);
}
```

### Pattern Matching 處理多型

```java
public BigDecimal calculateTotalValue(List<FinancialProduct> products) {
    return products.stream()
        .map(this::calculateProductValue)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}

private BigDecimal calculateProductValue(FinancialProduct product) {
    return switch (product) {
        case Deposit d -> d.principal()
            .multiply(d.rate())
            .divide(BigDecimal.valueOf(100));
        case Fund f -> f.nav().multiply(f.units());
        case InsuranceProduct i -> i.coverageAmount();
    };
}
```

---

## M11-LAB-02: 保險多險種保單

### 車險、壽險、健康險

```java
public sealed interface Policy permits CarPolicy, LifePolicy, HealthPolicy {
    String getPolicyNumber();
    PolicyStatus getStatus();
}

@TypeAlias("car")
public record CarPolicy(
    String policyNumber,
    PolicyStatus status,
    String vehicleNumber,
    BigDecimal premium,
    CarCoverage coverage
) implements Policy {}

@TypeAlias("life")
public record LifePolicy(
    String policyNumber,
    PolicyStatus status,
    String insuredName,
    BigDecimal premium,
    int termYears,
    BigDecimal deathBenefit
) implements Policy {}
```

### Pattern Matching 計算保費

```java
public BigDecimal calculatePremium(Policy policy) {
    return switch (policy) {
        case CarPolicy cp -> calculateCarPremium(cp);
        case LifePolicy lp -> calculateLifePremium(lp);
        case HealthPolicy hp -> calculateHealthPremium(hp);
    };
}
```
