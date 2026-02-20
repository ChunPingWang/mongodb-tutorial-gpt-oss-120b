# M01: RDB vs NoSQL 思維轉換

## 學習目標
理解關聯式與文件式資料庫的本質差異，建立正確的選型思維。

---

## M01-DOC-01: Data Model 哲學比較

### 關聯式資料庫 (RDB) 的設計哲學

**核心原則：正規化 (Normalization)**

RDB 採用正規化設計，將資料拆分到多個表中，透過外鍵建立關聯：

```
┌─────────────────┐       ┌─────────────────┐
│    accounts    │       │   transactions  │
├─────────────────┤       ├─────────────────┤
│ id (PK)        │──┐    │ id (PK)        │
│ customer_id    │──┼───▶│ account_id (FK)│
│ account_number │       │ amount         │
│ balance        │       │ type           │
│ status         │       │ created_at     │
└─────────────────┘       └─────────────────┘
         ▲
         │
┌────────┴────────┐
│   customers    │
├─────────────────┤
│ id (PK)        │
│ name           │
│ email          │
│ created_at     │
└─────────────────┘
```

**特點：**
- Schema-on-Write：寫入時必須符合預定義結構
- 參照完整性：透過外鍵約束確保資料一致性
- 適合複雜查詢：JOIN 操作能力強

### MongoDB 的設計哲學

**核心原則：反正規化 (Denormalization)**

MongoDB 採用嵌入式文件設計，將相關資料嵌入同一文件：

```json
{
  "_id": "acc_001",
  "accountNumber": "A001",
  "balance": 50000,
  "status": "ACTIVE",
  "customer": {
    "name": "張三",
    "email": "zhangsan@example.com"
  },
  "transactions": [
    {
      "id": "txn_001",
      "amount": 10000,
      "type": "DEPOSIT",
      "createdAt": "2024-01-15T10:30:00Z"
    },
    {
      "id": "txn_002",
      "amount": 5000,
      "type": "WITHDRAWAL",
      "createdAt": "2024-01-16T14:20:00Z"
    }
  ]
}
```

**特點：**
- Schema-on-Read：讀取時才決定資料結構
- 彈性結構：同一 Collection 可以有不同結構的文件
- 嵌入 vs 引用：依據存取模式選擇

### 嵌入 vs 引用決策矩陣

| 關係類型 | 建議策略 | 範例 |
|---------|---------|------|
| 1:1 (經常一起讀取) | 嵌入 | 帳戶 + 帳戶狀態 |
| 1:N (數量有限) | 嵌入 | 客戶 + 地址列表 |
| 1:N (數量無限) | 引用 | 客戶 + 交易記錄 |
| N:N | 引用 或 嵌入(依數據量) | 學生 + 課程 |

### 銀行帳戶建模對比

**RDB 模型：**
```sql
CREATE TABLE customers (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE accounts (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id),
    account_number VARCHAR(20),
    balance DECIMAL(15,2),
    status VARCHAR(20)
);

CREATE TABLE transactions (
    id BIGINT PRIMARY KEY,
    account_id BIGINT REFERENCES accounts(id),
    amount DECIMAL(15,2),
    type VARCHAR(20),
    created_at TIMESTAMP
);
```

**MongoDB 模型：**
```javascript
// 嵌入式設計 - 適合低交易量場景
{
  "_id": "acc_001",
  "customerId": "cust_001",
  "accountNumber": "A001",
  "balance": 50000,
  "status": "ACTIVE",
  "transactions": [
    { "amount": 10000, "type": "DEPOSIT", "createdAt": "2024-01-15" },
    { "amount": 5000, "type": "WITHDRAWAL", "createdAt": "2024-01-16" }
  ]
}

// 引用式設計 - 適合高交易量場景
// accounts collection
{
  "_id": "acc_001",
  "customerId": "cust_001",
  "accountNumber": "A001",
  "balance": 50000,
  "status": "ACTIVE"
}

// transactions collection
{
  "_id": "txn_001",
  "accountId": "acc_001",
  "amount": 10000,
  "type": "DEPOSIT",
  "createdAt": "2024-01-15"
}
```

---

## M01-DOC-02: CAP 定理與一致性模型

### CAP 定理

CAP 定理指出分散式系統無法同時滿足三個特性：

```
           Consistency (一致性)
                ╱    △
               ╱     ╲
              ╱      ╲
             ╱   P    ╲
            ╱         ╲
           ╱          ╲
    Availability ────── Partition Tolerance
           (可用性)      (分割容忍)
```

| 特性 | 說明 |
|------|------|
| **Consistency** | 所有節點看到相同的資料 |
| **Availability** | 每個請求都能得到回應 |
| **Partition Tolerance** | 系統能在網路分割時繼續運作 |

**關鍵理解：** 在網路分割發生時，必須在一致性與可用性之間權衡。

### ACID vs BASE

| 特性 | ACID (RDB) | BASE (NoSQL) |
|------|------------|--------------|
| **Atomicity** | 原子性 - 交易全有或全無 | 基本可用 |
| **Consistency** | 一致性 - 交易後狀態一致 | 軟狀態 - 狀態可能變化 |
| **Isolation** | 隔離性 - 並發交易互不干擾 | 最終一致 - 最終達到一致 |
| **Durability** | 持久性 - 交易結果持久保存 |

### 金融場景一致性需求

**轉帳場景 - 需要強一致性：**

```
帳戶 A (餘額 1000)  →  轉帳 500 元  →  帳戶 B (餘額 2000)
     ↓                                          ↓
  餘額變為 500                            餘額變為 2500
     ↓                                          ↓
     └────────────── 必須同時發生 ──────────────┘
```

**解決方案：**
- MongoDB 4.0+ 多文件交易
- 或採用補償機制（SAGA Pattern）

### 電商場景 - 最終一致性可接受

```
訂單建立  →  庫存扣減  →  付款  →  出貨
    │           │           │         │
    └───────────┴───────────┴─────────┘
                    ↓
            最終狀態一致即可
            (允許短暫不一致)
```

### 金融 vs 電商 vs 保險場景

| 場景 | 一致性需求 | 建議策略 |
|------|-----------|---------|
| **銀行轉帳** | 強一致性 | 多文件交易 |
| **銀行帳單** | 最終一致 | 異動資料庫 + 查詢資料庫 |
| **電商庫存** | 最終一致 | 樂觀鎖/悲觀鎖 |
| **電商訂單** | 強一致性 | SAGA Pattern |
| **保險保單** | 強一致性 | 交易或補償機制 |
| **保險理賠** | 強一致性 | 狀態機 + 審計日誌 |

---

## M01-DOC-03: 選型決策框架

### 何時選擇 MongoDB

```
                    ┌─────────────────────┐
                    │  需要彈性 Schema?    │
                    └──────────┬──────────┘
                               │
                    ┌──────────┴──────────┐
                    │         是          │
                    └──────────┬──────────┘
                               │
                    ┌──────────┴──────────┐
                    │  資料結構複雜/巢狀?  │
                    └──────────┬──────────┘
                               │
                    ┌──────────┴──────────┐
                    │         是          │
                    └──────────┬──────────┘
                               │
         ┌─────────────────────┼─────────────────────┐
         ▼                     ▼                     ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  大量文件載入   │  │  需要快速開發    │  │  需要水平擴展   │
│  (CMS, 部落格) │  │  (原型, MVP)    │  │  (大數據, IoT) │
└────────┬────────┘  └────────┬────────┘  └────────┬────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              │
                    ┌──────────┴──────────┐
                    │    考慮 MongoDB    │
                    └─────────────────────┘
```

### 何時選擇 RDB

- 需要複雜的 JOIN 查詢
- 需要強一致性（金融、帳務）
- 需要複雜的的交易支援
- 資料結構固定且穩定

### 混合架構：Polyglot Persistence

**銀行系統範例：**

```
┌─────────────────────────────────────────────────────────────┐
│                        應用層                                │
└─────────────────────────────────────────────────────────────┘
         │              │              │              │
         ▼              ▼              ▼              ▼
   ┌──────────┐   ┌──────────┐  ┌──────────┐  ┌──────────┐
   │  核心帳務 │   │  客戶 360 │  │  即時風控 │  │ 交易日誌 │
   │  (PostgreSQL) │ (MongoDB) │  │  (Redis) │  │ (Cassandra)│
   └──────────┘   └──────────┘  └──────────┘  └──────────┘
```

| 資料領域 | 資料庫 | 理由 |
|---------|--------|------|
| 核心帳務 | PostgreSQL | 強一致性、複雜查詢 |
| 客戶資料 | MongoDB | 彈性結構、複雜文件 |
| 快取/Session | Redis | 極速存取 |
| 時序資料 | Cassandra | 高寫入吞吐 |

---

## M01-LAB-01: 比較用測試案例

### 實驗目標
使用 Testcontainers 同時啟動 PostgreSQL + MongoDB，比較 RDB 和 MongoDB 的查詢效能與彈性。

### BDD Scenario

```gherkin
Feature: RDB vs MongoDB 效能比較
  Scenario: 相同的客戶訂單資料存入不同資料庫
    Given 客戶訂單資料包含巢狀地址與多筆明細
    When 分別存入 PostgreSQL 和 MongoDB
    Then 比較兩種 DB 的查詢彈性與效能
```

### 實作重點

1. **建立測試容器**
2. **建立 JPA Entity vs MongoDB Document 對照**
3. **測試巢狀查詢在兩種 DB 的寫法差異**

---

## M01-LAB-02: Schema Evolution 對比實驗

### 實驗目標
測試新增欄位時 RDB 需要 ALTER TABLE，MongoDB 直接寫入的差異。

### 情境：保險保單新增「附加條款」欄位

```gherkin
Feature: Schema Evolution 測試
  Scenario: 新增欄位的遷移成本比較
    Given 現有 1000 筆保單資料
    When 新增「附加條款」欄位
    Then 比較 RDB 和 MongoDB 的遷移成本
```
