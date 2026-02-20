# MongoDB for Java Spring Developers - 完整教學課程

這是一個針對 Java Spring 開發人員的 MongoDB 完整教學課程，採用情境驅動的學習方式，涵蓋金融、保險與電商等真實應用場景。

## 技術規格

| 項目 | 版本 |
|------|------|
| Java | 23 |
| Spring Boot | 4 |
| MongoDB | 7+ |
| 構建工具 | Gradle |
| 測試框架 | Testcontainers, JUnit 5, Cucumber |

## 教學方法

- **Test-First (BDD/TDD)**: 先寫測試再實作，以情境驅動學習
- **情境驅動**: 金融（銀行帳戶）、保險（保單管理）、電商（訂單處理）
- **領域驅動設計 (DDD)**: 遵循 OOP、SOLID 與 Hexagonal Architecture 原則

## 課程架構

### Phase 1: 基礎建設與思維轉換
| Module | 主題 | 說明 |
|--------|------|------|
| M01 | RDB vs NoSQL 思維轉換 | 理解關聯式與文件式資料庫的差異 |
| M02 | NoSQL 版圖 | MongoDB vs Redis vs Cassandra |
| M03 | 開發環境與測試基礎設施 | Gradle、Testcontainers、BDD 測試策略 |
| M04 | Document 思維與基礎建模 | 嵌入 vs 引用、BSON 資料類型 |

### Phase 2: Spring Data MongoDB 核心
| Module | 主題 | 說明 |
|--------|------|------|
| M05 | Spring Data CRUD | 基本資料操作 |
| M06 | Query DSL | 靈活查詢建構 |
| M07 | Aggregation Pipeline | 聚合管道與分析 |
| M08 | Schema Validation | 資料驗證規則 |
| M09 | Transactions | 多文件交易 |

### Phase 3: 領域驅動與進階建模
| Module | 主題 | 說明 |
|--------|------|------|
| M10 | DDD Aggregate Modeling | 領域驅動設計聚合根 |
| M11 | Polymorphism & Inheritance | 多型與繼承建模 |
| M12 | Event Sourcing | 事件溯源模式 |
| M13 | CQRS Read Model | 命令查詢職責分離 |
| M14 | Saga Pattern | 分散式交易協調 |

### Phase 4: 效能、可觀測性與維運
| Module | 主題 | 說明 |
|--------|------|------|
| M15 | Indexing & Performance | 索引策略與效能優化 |
| M16 | Change Streams | 變更資料流 |
| M17 | Observability | 可觀測性與監控 |
| M18 | Migration & Versioning | 資料庫遷移與版本管理 |

### Phase 5: 整合專案與架構決策
| Module | 主題 | 說明 |
|--------|------|------|
| M19 | Banking Capstone | 銀行帳戶管理系統 |
| M20 | Insurance Capstone | 保險保單管理系統 |
| M21 | E-commerce Capstone | 電商訂單管理系統 |

## 專案結構

```
mongodb-spring-course/
├── build.gradle.kts              # Root 構建配置
├── settings.gradle.kts           # 包含所有子模組
├── mongodb-spring-course-curriculum.md  # 完整課程大綱
├── m01-rdb-vs-nosql/            # 理論課程（文件為主）
├── m02-nosql-landscape/
├── m03-environment-setup/
├── m04-document-thinking/        # 開始有實際程式碼
├── m05-spring-data-crud/
├── m06-query-dsl/
├── m07-aggregation-pipeline/
├── m08-schema-validation/
├── m09-transactions/
├── m10-ddd-aggregate-modeling/
├── m11-polymorphism-inheritance/
├── m12-event-sourcing/
├── m13-cqrs-read-model/
├── m14-saga-pattern/
├── m15-indexing-performance/
├── m16-change-streams/
├── m17-observability/
├── m18-migration-versioning/
├── m19-banking-capstone/         # 整合專案
├── m20-insurance-capstone/
└── m21-ecommerce-capstone/
```

## 快速開始

### 前置需求
- Java 23
- Docker Desktop（用於 Testcontainers）

### 執行測試

```bash
# 執行所有模組測試
./gradlew test

# 執行特定模組測試
./gradlew :m04-document-thinking:test

# 執行特定模組並顯示詳細輸出
./gradlew :m04-document-thinking:test --info
```

### 執行應用程式

```bash
# 執行特定模組
./gradlew :m04-document-thinking:bootRun
```

## 學習路徑建議

### 初學者（1-2 週）
1. 閱讀 M01-M02 理論基礎
2. 完成 M03 環境設定
3. 動手做 M04 Document 思維

### 中級開發者（2-3 週）
1. 深入 M05-M09 Spring Data MongoDB 核心
2. 完成 M10-M11 領域建模
3. 動手做 M19 Banking Capstone 專案

### 進階（3-4 週）
1. 完成 M12-M14 進階模式
2. 完成 M15-M18 效能與維運
3. 完成 M20-M21 整合專案

## 每個 Module 的結構

每個 Module 都是獨立的 Gradle 子模組，包含：

```
module-name/
├── build.gradle.kts          # 模組構建配置
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/course/mongodb/
│   │   │       └── mXX/          # XX 為模組編號
│   │   │           ├── MXXApplication.java
│   │   │           ├── domain/       # 領域模型
│   │   │           ├── repository/    # 資料存取層
│   │   │           ├── service/       # 業務邏輯層
│   │   │           └── ...            # 其他層
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/
│       │   └── com/course/mongodb/
│       │       └── mXX/          # 單元與整合測試
│       └── resources/
│           └── features/         # BDD 測試案例
├── docs/                     # 教學文件
│   └── MXX-topic-name.md
└── README.md                # 模組說明
```

## 相關資源

- [MongoDB 官方文檔](https://docs.mongodb.com/)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [Testcontainers MongoDB](https://www.testcontainers.org/modules/mongodb/)

## 授權

本教學課程僅供學習使用。
