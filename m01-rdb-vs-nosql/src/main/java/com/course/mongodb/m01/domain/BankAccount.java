package com.course.mongodb.m01.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "bank_accounts")
public class BankAccount {
    @Id
    private String id;
    private String accountNumber;
    private String customerId;
    private Money balance;
    private AccountStatus status;
    private List<Transaction> transactions;

    public BankAccount() {
        this.balance = Money.ZERO;
        this.status = AccountStatus.ACTIVE;
        this.transactions = new java.util.ArrayList<>();
    }

    public BankAccount(String accountNumber, String customerId, Money initialBalance) {
        this();
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = initialBalance;
    }

    public void deposit(Money amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(Money amount) {
        if (!this.balance.isGreaterThanOrEqual(amount)) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public Money getBalance() { return balance; }
    public void setBalance(Money balance) { this.balance = balance; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
}
