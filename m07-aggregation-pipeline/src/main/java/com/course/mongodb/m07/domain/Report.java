package com.course.mongodb.m07.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "reports")
public class Report {
    @Id
    private String id;

    @Field("account_id")
    private String accountId;

    private String month;

    private Double income;

    private Double expense;

    public Report() {
    }

    public Report(String accountId, String month, Double income, Double expense) {
        this.accountId = accountId;
        this.month = month;
        this.income = income;
        this.expense = expense;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public Double getIncome() { return income; }
    public void setIncome(Double income) { this.income = income; }
    public Double getExpense() { return expense; }
    public void setExpense(Double expense) { this.expense = expense; }
}
