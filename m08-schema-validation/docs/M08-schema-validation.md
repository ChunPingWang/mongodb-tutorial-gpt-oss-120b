# M08: Schema Validation 與資料治理

## 學習目標
在 MongoDB 彈性 Schema 中建立適度的資料品質控制

---

## M08-DOC-01: MongoDB JSON Schema Validation

### $jsonSchema 驗證規則

```javascript
{
  $jsonSchema: {
    bsonType: "object",
    required: ["policyNumber", "insured", "premium"],
    properties: {
      policyNumber: {
        bsonType: "string",
        pattern: "^POL-[0-9]{4}-[0-9]+$"
      },
      premium: {
        bsonType: "decimal",
        minimum: 0
      },
      status: {
        enum: ["PENDING", "ACTIVE", "EXPIRED", "CANCELLED"]
      }
    }
  }
}
```

### Validation Level

| Level | 說明 |
|-------|------|
| strict | 驗證所有文件（預設）|
| moderate | 只驗證符合 schema 的文件 |

### Validation Action

| Action | 說明 |
|--------|------|
| error | 拒絕不符合的文件 |
| warn | 允許寫入但記錄警告 |

---

## M08-DOC-02: Schema-on-Read vs Schema-on-Write 混合策略

### 金融場景策略

```json
{
  "_id": "policy_001",
  "policyNumber": "POL-2024-001",           // Schema-on-Write
  "premium": 5000,                           // Schema-on-Write
  "status": "ACTIVE",                       // Schema-on-Write
  "metadata": {                              // Schema-on-Read
    "source": "agent_system",
    "version": "v2",
    "customFields": {}                       // 彈性擴展
  }
}
```

### 版本化 Schema

```json
{
  "_id": "prod_001",
  "schemaVersion": "3.0",
  "name": "iPhone 15",
  "price": 36900,
  "variants": []
}
```

---

## M08-LAB-01: 金融合規資料驗證

### BDD Feature

```gherkin
Feature: 保單資料完整性驗證
  Scenario: 缺少必要欄位的保單被拒絕
    Given MongoDB Collection 已設定 Schema Validation
    When 嘗試建立缺少投保人資訊的保單
    Then 寫入被拒絕並回傳驗證錯誤

  Scenario: 保費金額必須為正數
    Given MongoDB Collection 已設定金額驗證規則
    When 嘗試建立保費為負數的保單
    Then 寫入被拒絕
```

### 程式碼實作

```java
// 建立 Collection 並設定 Validator
Document validator = new Document("$jsonSchema",
    Document.parse("{ " +
        "bsonType: 'object', " +
        "required: ['policyNumber', 'insured', 'premium'], " +
        "properties: { " +
        "  policyNumber: { bsonType: 'string' }, " +
        "  premium: { bsonType: 'decimal', minimum: 0 } " +
        "} " +
    "}")
);

mongoTemplate.createCollection("insurance_policies")
    .collectionOptions(CollectionOptions.validator(validator)
        .validationLevel(ValidationLevel.STRICT)
        .validationAction(ValidationAction.ERROR));
```

---

## M08-LAB-02: Schema 版本演進

### 情境：電商商品 Schema V1 → V2 → V3

```json
// V1
{ "_id": "p1", "name": "Product A", "price": 100 }

// V2 - 新增 category
{ "_id": "p2", "name": "Product B", "price": 200, "category": "Electronics" }

// V3 - 重構為 variants
{ "_id": "p3", "name": "Product C", "price": 300, 
  "variants": [{ "sku": "C-BLK", "stock": 50 }] }
```

### DocumentMigrator

```java
public class DocumentMigrator {
    public Document migrate(Document doc) {
        String version = doc.getString("schemaVersion");
        if (version == null) {
            doc = migrateV1toV2(doc);
            doc = migrateV2toV3(doc);
        } else if ("2.0".equals(version)) {
            doc = migrateV2toV3(doc);
        }
        doc.put("schemaVersion", "3.0");
        return doc;
    }
}
```
