package com.course.mongodb.m10.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "loan_applications")
public class LoanApplication {

    @Id
    private String id;

    @Field("customer_id")
    private String customerId;

    private BigDecimal amount;

    private LoanStatus status;

    @Field("interest_rate")
    private BigDecimal interestRate;

    @Field("term_months")
    private Integer termMonths;

    @Field("submitted_at")
    private Instant submittedAt;

    @Field("approved_at")
    private Instant approvedAt;

    @Field("rejection_reason")
    private String rejectionReason;

    private List<String> documents;

    public LoanApplication() {
        this.documents = new ArrayList<>();
        this.status = LoanStatus.DRAFT;
    }

    public LoanApplication(String customerId, BigDecimal amount, BigDecimal interestRate, Integer termMonths) {
        this();
        this.customerId = customerId;
        this.amount = amount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.submittedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
    public Instant getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public List<String> getDocuments() { return documents; }
    public void setDocuments(List<String> documents) { this.documents = documents; }

    public void submit() {
        if (this.status != LoanStatus.DRAFT) {
            throw new IllegalStateException("Only draft applications can be submitted");
        }
        if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.status = LoanStatus.PENDING;
    }

    public void approve() {
        if (this.status != LoanStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be approved");
        }
        this.status = LoanStatus.APPROVED;
        this.approvedAt = Instant.now();
    }

    public void reject(String reason) {
        if (this.status != LoanStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be rejected");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }
        this.status = LoanStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public void addDocument(String documentId) {
        if (documentId == null || documentId.isBlank()) {
            throw new IllegalArgumentException("Document ID cannot be empty");
        }
        this.documents.add(documentId);
    }

    public boolean isApproved() {
        return this.status == LoanStatus.APPROVED;
    }

    public boolean isPending() {
        return this.status == LoanStatus.PENDING;
    }

    public BigDecimal calculateMonthlyPayment() {
        if (this.amount == null || this.interestRate == null || this.termMonths == null) {
            return BigDecimal.ZERO;
        }
        if (this.termMonths == 0) {
            return this.amount;
        }
        BigDecimal monthlyRate = this.interestRate.divide(BigDecimal.valueOf(12), 10, java.math.RoundingMode.HALF_UP);
        BigDecimal factor = BigDecimal.valueOf(Math.pow(1 + monthlyRate.doubleValue(), this.termMonths));
        return this.amount.multiply(monthlyRate).multiply(factor).divide(factor.subtract(BigDecimal.ONE), 2, java.math.RoundingMode.HALF_UP);
    }

    public void disburse() {
        if (this.status != LoanStatus.APPROVED) {
            throw new IllegalStateException("Only approved applications can be disbursed");
        }
        this.status = LoanStatus.DISBURSED;
    }

    public enum LoanStatus {
        DRAFT,
        PENDING,
        APPROVED,
        REJECTED,
        DISBURSED,
        DEFAULTED
    }
}
