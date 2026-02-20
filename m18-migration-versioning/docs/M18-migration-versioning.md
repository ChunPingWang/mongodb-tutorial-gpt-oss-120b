# M18: Schema Migration 與版本管理

## 學習目標
管理 MongoDB Schema 的版本演進

---

## M18-DOC-01: MongoDB Schema Migration 策略

### Migration 類型

| 類型 | 說明 | 適用場景 |
|------|------|---------|
| Eager Migration | 立即執行 | 小資料集、簡單結構變更 |
| Lazy Migration | 讀取時執行 | 大資料集、複雜結構 |
| 混合策略 | 兩者結合 | 大部分場景 |

### Eager Migration

```java
@ChangeUnit(order = "1", id = "add-field")
public class AddFieldMigration {
    
    @Execution
    public void migrate() {
        // 立即更新所有文件
        UpdateResult result = mongoTemplate.updateMulti(
            new Query(),
            new Update().set("newField", "default"),
            "collection"
        );
        System.out.println("Migrated: " + result.getModifiedCount());
    }
    
    @RollbackExecution
    public void rollback() {
        mongoTemplate.updateMulti(
            new Query(),
            new Update().unset("newField"),
            "collection"
        );
    }
}
```

### Lazy Migration

```java
@Component
public class LazyMigrationService {
    
    public Document migrateOnRead(Document doc) {
        if (!doc.containsKey("newField")) {
            doc.put("newField", "default");
            // 可選：回寫資料庫
            // mongoTemplate.save(doc);
        }
        return doc;
    }
}
```

### Mongock

```xml
<dependency>
    <groupId>com.github.mongock</groupId>
    <artifactId>mongock-springboot</artifactId>
    <version>5.0.0</version>
</dependency>
```

```yaml
mongock:
  change-locations:
    - classpath:db/migration/**
  enabled: true
```

---

## M18-DOC-02: 資料版本化最佳實踐

### Document Version Field

```json
{
  "_id": "prod_001",
  "_schemaVersion": "3.0",
  "name": "Product A",
  "price": 100,
  "variants": []
}
```

### Converter Chain: V1 → V2 → V3

```java
@Component
public class DocumentConverter {
    
    public Document convert(Document doc) {
        String version = doc.getString("_schemaVersion");
        
        if (version == null) {
            doc = convertV1toV2(doc);
            version = "2.0";
        }
        
        if ("2.0".equals(version)) {
            doc = convertV2toV3(doc);
            version = "3.0";
        }
        
        return doc;
    }
    
    private Document convertV1toV2(Document doc) {
        // V1: price 是數字
        // V2: price 是物件 { amount: number, currency: string }
        Object price = doc.get("price");
        if (price instanceof Number) {
            doc.put("price", Document.parse(
                "{ amount: " + price + ", currency: 'TWD' }"
            ));
        }
        doc.put("_schemaVersion", "2.0");
        return doc;
    }
    
    private Document convertV2toV3(Document doc) {
        // V2: variants 是空陣列
        // V3: variants 結構變更
        doc.put("_schemaVersion", "3.0");
        return doc;
    }
}
```

### 向後相容設計原則

1. **不要刪除欄位** → 標記為廢棄
2. **不要變更欄位類型** → 新增欄位
3. **保持欄位語意** → 避免破壞現有程式碼

---

## M18-LAB-01: Mongock Migration 實作

### BDD Feature

```gherkin
Feature: Schema Migration
  Scenario: 新增欄位的無痛升級
    Given Collection 中有 V1 格式的保單文件 1000 筆
    When 執行 V1 → V2 Migration (新增 riskScore 欄位)
    Then 所有文件更新為 V2 格式
    And riskScore 欄位預設值為 "UNRATED"
    And 應用程式可正常讀寫 V2 文件

  Scenario: 結構重組 Migration
    Given Collection 中有扁平的地址欄位
    When 執行地址結構重組 Migration
    Then 地址欄位重組為嵌入式 Address 物件
```

### Mongock ChangeUnit

```java
@ChangeUnit(id = "add-risk-score", order = "1")
public class AddRiskScoreMigration {
    
    @Execution
    public void migrate() {
        mongoTemplate.updateMulti(
            new Query(),
            new Update().set("riskScore", "UNRATED"),
            "insurance_policies"
        );
    }
    
    @RollbackExecution
    public void rollback() {
        mongoTemplate.updateMulti(
            new Query(),
            new Update().unset("riskScore"),
            "insurance_policies"
        );
    }
}
```

### 測試矩陣

```java
@Test
void shouldReadV1V2V3Documents() {
    // V1 文件
    Document v1 = Document.parse("{ _id: 1, name: 'A', price: 100 }");
    assertThat(converter.convert(v1).getString("price"))
        .isEqualTo("100 TWD");
    
    // V2 文件
    Document v2 = Document.parse(
        "{ _id: 2, name: 'B', price: { amount: 200, currency: 'TWD' } }"
    );
    assertThat(converter.convert(v2).getString("price"))
        .isEqualTo("200 TWD");
}
```
