# M20: 保險理賠系統 Capstone

## 學習目標
建構保險理賠處理系統，強調多型建模與流程管理

---

## M20-DOC-01: 保險理賠系統架構設計

### 多險種支援

```
┌─────────────────────────────────────────────────────────────┐
│                    保險理賠系統                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │    車險理賠  │  │   健康險理賠 │  │   財產險理陪 │        │
│  │  CarClaim   │  │ HealthClaim │  │ PropertyClaim│        │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘        │
│         │                 │                 │               │
│         └────────────────┼─────────────────┘               │
│                          ▼                                  │
│                 Claim (Polymorphic)                         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 理賠流程 State Machine

```
┌─────────┐     ┌──────────┐     ┌───────────┐     ┌──────────┐
│ REPORTED│ ──▶ │INVESTIGATING│ ──▶ │ASSESSING │ ──▶ │ APPROVED │
└─────────┘     └───────────┘     └───────────┘     └────┬─────┘
      │                                       │             │
      │                                       │             ▼
      │                                       │     ┌──────────┐
      │                                       │     │  PAID    │
      │                                       │     └────┬─────┘
      │                                       │           │
      │                                       ▼           ▼
      │                               ┌───────────┐  ┌──────────┐
      └──────────────────────────────▶│ REJECTED │  │ COMPLETED│
                                      └───────────┘  └──────────┘
```

### 文件管理策略

```java
// 理賠文件儲存
@Document
public class ClaimDocument {
    @Id
    private String id;
    private String claimId;
    private String fileName;
    private String storageType; // GRIDFS, S3
    private String storagePath;
    private Instant uploadedAt;
}
```

---

## M20-LAB-01: 完整系統實作

### 實作重點

1. **Polymorphic Claim Documents (各險種)**
2. **Event Sourcing: 理賠生命週期完整記錄**
3. **Aggregation Pipeline: 理賠統計報表**
4. **Schema Validation: 合規性檢查**
5. **SAGA: 理賠 → 核賠 → 給付流程**
6. **完整 BDD Test Suite**

### Domain Model

```java
// Sealed Interface
public sealed interface Claim 
    permits CarClaim, HealthClaim, PropertyClaim {
    String getClaimId();
    ClaimStatus getStatus();
    Money getClaimAmount();
}

// 車險理賠
@TypeAlias("car_claim")
public record CarClaim(
    String claimId,
    ClaimStatus status,
    String policyNumber,
    String vehicleNumber,
    String accidentDescription,
    Money estimatedAmount,
    List<ClaimDocument> documents
) implements Claim {}

// 健康險理賠
@TypeAlias("health_claim")
public record HealthClaim(
    String claimId,
    ClaimStatus status,
    String policyNumber,
    String diagnosis,
    List<MedicalRecord> records,
    Money claimAmount
) implements Claim {}
```

### Event Sourcing

```java
// 理賠事件
public record ClaimReportedEvent(String claimId, ...) {}
public record ClaimInvestigatedEvent(String claimId, String investigator, ...) {}
public record ClaimAssessedEvent(String claimId, Money assessedAmount, ...) {}
public record ClaimApprovedEvent(String claimId, ...) {}
public record ClaimPaidEvent(String claimId, Money paidAmount, ...) {}
```

### Aggregation Pipeline 理賠統計

```java
// 多維度理賠統計
Aggregation.newAggregation(
    facet(
        // 按險種統計
        group("claimType")
            .sum("claimAmount").as("totalAmount")
            .count().as("claimCount"),
        // 按地區統計
        group("region")
            .sum("claimAmount").as("totalAmount"),
        // 按金額級距
        bucket("claimAmount")
            .boundaries(0, 10000, 50000, 100000, 1000000)
            .defaultBucket("OVER_1M")
    )
);
```

### BDD Test Suite

```gherkin
Feature: 保險理賠流程
  Scenario: 車險理賠申請
    Given 客戶有有效的車險保單
    When 客戶發生事故並申請理賠
    Then 理賠狀態為 REPORTED
    And 產生理賠編號

  Scenario: 理賠審核通過
    Given 理賠狀態為 ASSESSING
    When 核赔人員審核完成
    And 核定理賠金額為 50000 元
    Then 理賠狀態為 APPROVED
    And 產生核赔記錄

  Scenario: 理賠付款
    Given 理賠狀態為 APPROVED
    When 完成付款作業
    Then 理賠狀態為 PAID
    And 產生理赔金匯款記錄
```
