# M19: Banking Capstone

This module demonstrates a comprehensive banking domain model using MongoDB.

## Domain Model: BankingAccount

The `BankingAccount` aggregate represents a complete banking account with:

### Account Types
- CHECKING - Standard checking account
- SAVINGS - Savings account with interest
- FIXED_DEPOSIT - Time deposit with fixed term
- MONEY_MARKET - Money market account
- CREDIT_CARD - Credit card account

### Account Status
- ACTIVE - Account is active and operational
- INACTIVE - Account is inactive
- FROZEN - Account is frozen (no transactions)
- CLOSED - Account is closed
- PENDING_VERIFICATION - Awaiting verification

### Components

1. **Transactions**: List of all account transactions
   - Transaction ID, Type, Amount, Description
   - Reference number, Date, Status
   - Merchant name, Category

2. **Beneficiaries**: Authorized beneficiaries
   - Beneficiary ID, Name, Account Number
   - Bank Name, Relationship

3. **Statements**: Monthly/account statements
   - Statement ID, Period dates
   - Opening/Closing Balance
   - Total Credits/Debits

### Operations

1. **Account Management**
   - Create new account
   - Close account
   - Freeze/Unfreeze account

2. **Transaction Operations**
   - Credit (deposit)
   - Debit (withdrawal)
   - Transfer between accounts

3. **Beneficiary Management**
   - Add/Remove beneficiaries

4. **Statement Generation**
   - Generate periodic statements

## Design Patterns

1. **Aggregate Pattern**: BankingAccount as root aggregate
2. **Embedded Documents**: Transactions, beneficiaries, statements
3. **Value Objects**: Transaction types, account status

## Running Tests

```bash
./gradlew :m19-banking-capstone:test
```
