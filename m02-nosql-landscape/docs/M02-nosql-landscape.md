# M02: NoSQL 版圖 - MongoDB vs Redis vs Cassandra

## 學習目標
掌握不同 NoSQL 資料庫的定位與適用場景

---

## M02-DOC-01: NoSQL 四大類型全景圖

| 類型 | 代表產品 | 適用場景 |
|------|---------|---------|
| Document Store | MongoDB, CouchDB | 彈性結構、Ad-hoc Query |
| Key-Value Store | Redis, DynamoDB | 快取、Session、排行榜 |
| Wide-Column Store | Cassandra, HBase | 高寫入吞吐、時序資料 |
| Graph Database | Neo4j | 關係遍歷、社群網路 |

---

## M02-DOC-02: MongoDB vs Redis vs Cassandra 深度比較

| 維度 | MongoDB | Redis | Cassandra |
|------|---------|-------|-----------|
| 資料模型 | Document (BSON) | Key-Value / 多種資料結構 | Wide-Column |
| 查詢能力 | 豐富 (Ad-hoc, Aggregation) | 簡單 (Key Lookup) | CQL (類 SQL) |
| 一致性 | 可調 (Write/Read Concern) | 強一致/最終一致 | 可調 (Consistency Level) |
| 擴展方式 | Sharding | Cluster (Hash Slot) | Consistent Hashing |
| 寫入效能 | 中高 | 極高 (in-memory) | 極高 (LSM-Tree) |
| 適用場景 | 通用文件、CMS、目錄 | 快取、Session、Pub/Sub | IoT 時序、日誌 |
| 交易支援 | 多文件 ACID (4.0+) | 單命令原子/Lua Script | 輕量級 Batch |

---

## M02-DOC-03: 金融場景 Polyglot Persistence 架構

### 銀行場景

```
┌─────────────────────────────────────────────┐
│              應用層                          │
└─────────────────────────────────────────────┘
         │        │        │        │
    ┌────┴───┐ ┌──┴──┐ ┌───┴───┐ ┌──┴──┐
    │ 核心帳務│ │客戶360│ │風控快取│ │交易日誌│
    │(RDB)   │ │(MongoDB)│ │(Redis)│ │(Cassandra)│
    └────────┘ └─────┘ └──────┘ └─────┘
```

---

## M02-LAB-01: 三種 NoSQL 併行測試

### BDD Scenario
```gherkin
Feature: 電商訂單多資料庫測試
  Scenario: 訂單資料寫入三種 DB
    Given 電商訂單建立事件
    When 資料分別寫入 MongoDB、Redis、Cassandra
    Then 驗證各自的存取模式
```
