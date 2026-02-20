package com.course.mongodb.m20.service;

import com.course.mongodb.m20.domain.Claim;
import com.course.mongodb.m20.domain.Claim.ClaimDocument;
import com.course.mongodb.m20.domain.Claim.Payment;
import com.course.mongodb.m20.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClaimService {

    private final ClaimRepository repository;

    public ClaimService(ClaimRepository repository) {
        this.repository = repository;
    }

    public Claim fileClaim(String policyId, String customerId, Claim.ClaimType claimType, 
                          String description, BigDecimal claimAmount) {
        String claimNumber = generateClaimNumber();
        Claim claim = new Claim(claimNumber, policyId, customerId, claimType);
        claim.setDescription(description);
        claim.setClaimAmount(claimAmount);
        claim.setCurrency("USD");
        
        return repository.save(claim);
    }

    public Optional<Claim> findByClaimNumber(String claimNumber) {
        return repository.findByClaimNumber(claimNumber);
    }

    public List<Claim> findByPolicyId(String policyId) {
        return repository.findByPolicyId(policyId);
    }

    public List<Claim> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    public List<Claim> findByStatus(Claim.ClaimStatus status) {
        return repository.findByClaimStatus(status);
    }

    public Claim assignAdjuster(String claimNumber, String adjusterId) {
        Claim claim = repository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        
        claim.assignAdjuster(adjusterId);
        return repository.save(claim);
    }

    public Claim approveClaim(String claimNumber, BigDecimal approvedAmount) {
        Claim claim = repository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        
        claim.approve(approvedAmount);
        
        return repository.save(claim);
    }

    public Claim denyClaim(String claimNumber, String reason) {
        Claim claim = repository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        
        claim.deny(reason);
        
        return repository.save(claim);
    }

    public Claim addDocument(String claimNumber, String documentType, String documentName, String fileUrl) {
        Claim claim = repository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        
        ClaimDocument document = new ClaimDocument(
            UUID.randomUUID().toString(),
            documentType,
            documentName
        );
        document.setFileUrl(fileUrl);
        
        claim.addDocument(document);
        
        return repository.save(claim);
    }

    public Claim addNote(String claimNumber, String note, String createdBy) {
        Claim claim = repository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        
        claim.addNote(note);
        
        if (claim.getNotes().size() > 0) {
            claim.getNotes().get(claim.getNotes().size() - 1).setCreatedBy(createdBy);
        }
        
        return repository.save(claim);
    }

    public Claim processPayment(String claimNumber, BigDecimal amount, Payment.PaymentMethod method) {
        Claim claim = repository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        
        if (claim.getClaimStatus() != Claim.ClaimStatus.APPROVED) {
            throw new IllegalStateException("Claim must be approved before payment");
        }
        
        Payment payment = new Payment(UUID.randomUUID().toString(), amount, method);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        
        claim.addPayment(payment);
        
        BigDecimal totalPaid = claim.getPayments().stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalPaid.compareTo(claim.getApprovedAmount()) >= 0) {
            claim.setClaimStatus(Claim.ClaimStatus.PAID);
        }
        
        return repository.save(claim);
    }

    public Claim closeClaim(String claimNumber) {
        Claim claim = repository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        
        claim.close();
        
        return repository.save(claim);
    }

    private String generateClaimNumber() {
        return "CLM" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
}
