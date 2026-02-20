package com.course.mongodb.m13.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "customer_360_views")
public class Customer360View {

    @Id
    private String id;

    @Field("customer_id")
    private String customerId;

    private String name;

    private String email;

    @Field("total_balance")
    private BigDecimal totalBalance;

    @Field("account_count")
    private int accountCount;

    @Field("active_loans")
    private int activeLoans;

    @Field("credit_score")
    private Integer creditScore;

    private List<AccountSummary> accounts;

    private List<LoanSummary> loans;

    @Field("last_updated")
    private Instant lastUpdated;

    public Customer360View() {
    }

    public Customer360View(String customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.totalBalance = BigDecimal.ZERO;
        this.accountCount = 0;
        this.activeLoans = 0;
        this.lastUpdated = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public BigDecimal getTotalBalance() { return totalBalance; }
    public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }
    public int getAccountCount() { return accountCount; }
    public void setAccountCount(int accountCount) { this.accountCount = accountCount; }
    public int getActiveLoans() { return activeLoans; }
    public void setActiveLoans(int activeLoans) { this.activeLoans = activeLoans; }
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
    public List<AccountSummary> getAccounts() { return accounts; }
    public void setAccounts(List<AccountSummary> accounts) { this.accounts = accounts; }
    public List<LoanSummary> getLoans() { return loans; }
    public void setLoans(List<LoanSummary> loans) { this.loans = loans; }
    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }

    public void updateAccounts(List<AccountSummary> accounts) {
        this.accounts = accounts;
        this.accountCount = accounts.size();
        this.totalBalance = accounts.stream()
            .map(AccountSummary::balance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.lastUpdated = Instant.now();
    }

    public void updateLoans(List<LoanSummary> loans) {
        this.loans = loans;
        this.activeLoans = (int) loans.stream()
            .filter(l -> "ACTIVE".equals(l.status()))
            .count();
        this.lastUpdated = Instant.now();
    }

    public record AccountSummary(
        String accountId,
        String accountType,
        BigDecimal balance,
        String status
    ) {}

    public record LoanSummary(
        String loanId,
        BigDecimal amount,
        String status,
        BigDecimal remainingBalance
    ) {}
}
