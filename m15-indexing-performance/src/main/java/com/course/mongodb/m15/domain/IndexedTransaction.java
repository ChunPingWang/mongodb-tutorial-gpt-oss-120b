package com.course.mongodb.m15.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "indexed_transactions")
@CompoundIndexes({
    @CompoundIndex(name = "account_date_idx", def = "{'accountId': 1, 'transactionDate': -1}"),
    @CompoundIndex(name = "status_type_idx", def = "{'status': 1, 'type': 1}")
})
public class IndexedTransaction {

    @Id
    private String id;

    @Indexed
    @Field("account_id")
    private String accountId;

    @Indexed
    @Field("transaction_id")
    private String transactionId;

    @Indexed
    private TransactionType type;

    private BigDecimal amount;

    @Field("transaction_date")
    @Indexed
    private Instant transactionDate;

    @Indexed
    private TransactionStatus status;

    @Field("description")
    private String description;

    @Field("merchant_id")
    private String merchantId;

    @Field("category")
    @Indexed
    private String category;

    @Field("balance_after")
    private BigDecimal balanceAfter;

    @Field("is_foreign")
    @Indexed
    private boolean isForeign;

    @Field("processed_at")
    private Instant processedAt;

    public IndexedTransaction() {
    }

    public IndexedTransaction(String accountId, String transactionId, TransactionType type, 
                              BigDecimal amount, String category) {
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.transactionDate = Instant.now();
        this.status = TransactionStatus.PENDING;
        this.isForeign = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Instant getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Instant transactionDate) { this.transactionDate = transactionDate; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public boolean isForeign() { return isForeign; }
    public void setForeign(boolean foreign) { isForeign = foreign; }
    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }

    public void process() {
        this.status = TransactionStatus.COMPLETED;
        this.processedAt = Instant.now();
    }

    public void fail(String reason) {
        this.status = TransactionStatus.FAILED;
        this.description = reason;
    }

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER,
        PAYMENT,
        REFUND,
        FEE
    }

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}
