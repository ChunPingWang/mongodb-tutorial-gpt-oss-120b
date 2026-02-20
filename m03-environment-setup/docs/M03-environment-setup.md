# M03: 開發環境與測試基礎設施

## 學習目標
建立標準化的 MongoDB 開發與測試環境

---

## M03-DOC-01: Gradle 多模組專案建置指南

### Root build.gradle.kts 設定

```kotlin
plugins {
    id("org.springframework.boot") version "3.4.0" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
}

allprojects {
    group = "com.course.mongodb"
    version = "1.0.0"
}
```

### Module build.gradle.kts 範例

```kotlin
plugins {
    id("java")
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:junit-jupiter")
}
```

---

## M03-DOC-02: Testcontainers + MongoDB 測試策略

### Singleton Container Pattern

```java
@Container
static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
        .withReuse(true);

@DynamicPropertySource
static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
}
```

### 測試資料生命週期

```java
@BeforeEach
void setUp() {
    mongoTemplate.dropCollection(BankAccount.class);
}
```

---

## M03-DOC-03: BDD + TDD 雙軌測試流程

### 測試金字塔

```
        ┌─────────────┐
        │  BDD (E2E)  │  ← Cucumber Feature Files
        ├─────────────┤
        │ Integration │  ← Service + Repository
        ├─────────────┤
        │   Unit      │  ← Repository Tests
        └─────────────┘
```

### 目錄結構

```
src/
├── main/java/           # Production Code
└── test/
    ├── java/           # TDD Tests
    └── resources/
        └── features/  # BDD Feature Files
```

---

## M03-LAB-01: 專案骨架建立

### 驗證：全模組建置

```bash
./gradlew build
```

### 第一個冒煙測試

```java
@Test
void shouldConnectToMongoDB() {
    BankAccount account = new BankAccount("A001", 10000);
    repository.save(account);
    
    Optional<BankAccount> found = repository.findById("A001");
    assertThat(found).isPresent();
    assertThat(found.get().getBalance()).isEqualTo(10000);
}
```

---

## M03-LAB-02: BDD 基礎架構

### 第一個 Feature File

```gherkin
Feature: MongoDB 連線驗證
  Scenario: 成功連線至 MongoDB
    Given MongoDB container 已啟動
    When 執行 ping 命令
    Then 回傳成功狀態
```
