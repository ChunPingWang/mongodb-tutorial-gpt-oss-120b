package com.course.mongodb.m19.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "banking_accounts")
@CompoundIndexes({
    @CompoundIndex(name = "customer_id_idx", def = "{'customerId': 1}"),
    @CompoundIndex(name = "account_status_idx", def = "{'status': 1, 'accountType': 1}")
})
public class BankingAccount {

    @Id
    private String id;

    @Indexed
    @Field("account_number")
    private String accountNumber;

    @Indexed
    @Field("customer_id")
    private String customerId;

    @Field("account_type")
    private AccountType accountType;

    private AccountStatus status;

    @Field("currency")
    private String currency;

    @Field("balance")
    private BigDecimal balance;

    @Field("available_balance")
    private BigDecimal availableBalance;

    @Field("overdraft_limit")
    private BigDecimal overdraftLimit;

    @Field("interest_rate")
    private BigDecimal interestRate;

    @Field("opened_at")
    private Instant openedAt;

    @Field("closed_at")
    private Instant closedAt;

    @Field("last_transaction_at")
    private Instant lastTransactionAt;

    @Field("transactions")
    private List<Transaction> transactions;

    @Field("beneficiaries")
    private List<Beneficiary> beneficiaries;

    @Field("statements")
    private List<Statement> statements;

    private List<String> tags;

    public BankingAccount() {
        this.transactions = new ArrayList<>();
        this.beneficiaries = new ArrayList<>();
        this.statements = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public BankingAccount(String accountNumber, String customerId, AccountType accountType, String currency) {
        this();
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.availableBalance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
        this.openedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    public BigDecimal getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(BigDecimal overdraftLimit) { this.overdraftLimit = overdraftLimit; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public Instant getOpenedAt() { return openedAt; }
    public void setOpenedAt(Instant openedAt) { this.openedAt = openedAt; }
    public Instant getClosedAt() { return closedAt; }
    public void setClosedAt(Instant closedAt) { this.closedAt = closedAt; }
    public Instant getLastTransactionAt() { return lastTransactionAt; }
    public void setLastTransactionAt(Instant lastTransactionAt) { this.lastTransactionAt = lastTransactionAt; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
    public List<Beneficiary> getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(List<Beneficiary> beneficiaries) { this.beneficiaries = beneficiaries; }
    public List<Statement> getStatements() { return statements; }
    public void setStatements(List<Statement> statements) { this.statements = statements; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.availableBalance = this.availableBalance.add(amount);
        this.lastTransactionAt = Instant.now();
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
        this.availableBalance = this.availableBalance.subtract(amount);
        this.lastTransactionAt = Instant.now();
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        this.lastTransactionAt = Instant.now();
    }

    public void addBeneficiary(Beneficiary beneficiary) {
        this.beneficiaries.add(beneficiary);
    }

    public void close() {
        this.status = AccountStatus.CLOSED;
        this.closedAt = Instant.now();
    }

    public enum AccountType {
        CHECKING,
        SAVINGS,
        FIXED_DEPOSIT,
        MONEY_MARKET,
        CREDIT_CARD
    }

    public enum AccountStatus {
        ACTIVE,
        INACTIVE,
        FROZEN,
        CLOSED,
        PENDING_VERIFICATION
    }

    public static class Transaction {
        @Field("transaction_id")
        private String transactionId;

        private TransactionType type;
        private BigDecimal amount;
        private String description;

        @Field("reference_number")
        private String referenceNumber;

        @Field("transaction_date")
        private Instant transactionDate;

        private TransactionStatus status;

        @Field("merchant_name")
        private String merchantName;

        private String category;

        public Transaction() {}

        public Transaction(TransactionType type, BigDecimal amount, String description) {
            this.transactionId = java.util.UUID.randomUUID().toString();
            this.type = type;
            this.amount = amount;
            this.description = description;
            this.transactionDate = Instant.now();
            this.status = TransactionStatus.COMPLETED;
        }

        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public TransactionType getType() { return type; }
        public void setType(TransactionType type) { this.type = type; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getReferenceNumber() { return referenceNumber; }
        public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
        public Instant getTransactionDate() { return transactionDate; }
        public void setTransactionDate(Instant transactionDate) { this.transactionDate = transactionDate; }
        public TransactionStatus getStatus() { return status; }
        public void setStatus(TransactionStatus status) { this.status = status; }
        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public enum TransactionType {
            DEPOSIT,
            WITHDRAWAL,
            TRANSFER,
            PAYMENT,
            REFUND,
            FEE,
            INTEREST
        }

        public enum TransactionStatus {
            PENDING,
            COMPLETED,
            FAILED,
            CANCELLED
        }
    }

    public static class Beneficiary {
        @Field("beneficiary_id")
        private String beneficiaryId;

        private String name;
        private String accountNumber;

        @Field("bank_name")
        private String bankName;

        private String relationship;

        public Beneficiary() {}

        public Beneficiary(String beneficiaryId, String name, String accountNumber) {
            this.beneficiaryId = beneficiaryId;
            this.name = name;
            this.accountNumber = accountNumber;
        }

        public String getBeneficiaryId() { return beneficiaryId; }
        public void setBeneficiaryId(String beneficiaryId) { this.beneficiaryId = beneficiaryId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }
    }

    public static class Statement {
        @Field("statement_id")
        private String statementId;

        @Field("statement_date")
        private Instant statementDate;

        @Field("period_start")
        private Instant periodStart;

        @Field("period_end")
        private Instant periodEnd;

        @Field("opening_balance")
        private BigDecimal openingBalance;

        @Field("closing_balance")
        private BigDecimal closingBalance;

        @Field("total_credits")
        private BigDecimal totalCredits;

        @Field("total_debits")
        private BigDecimal totalDebits;

        public Statement() {}

        public Statement(String statementId, Instant periodStart, Instant periodEnd) {
            this.statementId = statementId;
            this.periodStart = periodStart;
            this.periodEnd = periodEnd;
            this.statementDate = Instant.now();
        }

        public String getStatementId() { return statementId; }
        public void setStatementId(String statementId) { this.statementId = statementId; }
        public Instant getStatementDate() { return statementDate; }
        public void setStatementDate(Instant statementDate) { this.statementDate = statementDate; }
        public Instant getPeriodStart() { return periodStart; }
        public void setPeriodStart(Instant periodStart) { this.periodStart = periodStart; }
        public Instant getPeriodEnd() { return periodEnd; }
        public void setPeriodEnd(Instant periodEnd) { this.periodEnd = periodEnd; }
        public BigDecimal getOpeningBalance() { return openingBalance; }
        public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }
        public BigDecimal getClosingBalance() { return closingBalance; }
        public void setClosingBalance(BigDecimal closingBalance) { this.closingBalance = closingBalance; }
        public BigDecimal getTotalCredits() { return totalCredits; }
        public void setTotalCredits(BigDecimal totalCredits) { this.totalCredits = totalCredits; }
        public BigDecimal getTotalDebits() { return totalDebits; }
        public void setTotalDebits(BigDecimal totalDebits) { this.totalDebits = totalDebits; }
    }
}
