# M01: RDB vs NoSQL 思維轉換

## 實作內容

### Domain Model
- `Money` - Value Object for monetary values
- `BankAccount` - Entity with embedded transactions
- `Transaction` - Value Object for account transactions
- `AccountStatus` - Enum for account states
- `TransactionType` - Enum for transaction types

### Repository
- `BankAccountRepository` - MongoDB repository interface

### Service
- `AccountService` - Account management service

## 測試

### TDD Tests
- `AccountServiceTest` - Unit tests for account service

### BDD Features
- `account-management.feature` - Behavior-driven test scenarios

## 執行測試

```bash
./gradlew :m01-rdb-vs-nosql:test
```
