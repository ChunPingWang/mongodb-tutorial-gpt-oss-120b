package com.course.mongodb.m06.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDate;

@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;

    @Field("account_id")
    private String accountId;

    private Double amount;

    private LocalDate date;

    private String type;

    public Transaction() {
    }

    public Transaction(String accountId, Double amount, LocalDate date, String type) {
        this.accountId = accountId;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
