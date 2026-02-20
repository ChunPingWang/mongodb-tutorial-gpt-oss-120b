package com.course.mongodb.m10.service;

import com.course.mongodb.m10.domain.LoanApplication;
import com.course.mongodb.m10.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class LoanApplicationService {

    private final LoanApplicationRepository repository;

    public LoanApplicationService(LoanApplicationRepository repository) {
        this.repository = repository;
    }

    public LoanApplication createApplication(String customerId, BigDecimal amount, 
                                              BigDecimal interestRate, Integer termMonths) {
        LoanApplication application = new LoanApplication(customerId, amount, interestRate, termMonths);
        return repository.save(application);
    }

    public Optional<LoanApplication> findById(String id) {
        return repository.findById(id);
    }

    public List<LoanApplication> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    public List<LoanApplication> findByStatus(LoanApplication.LoanStatus status) {
        return repository.findByStatus(status);
    }

    public LoanApplication submitApplication(String id) {
        LoanApplication application = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        application.submit();
        return repository.save(application);
    }

    public LoanApplication approveApplication(String id) {
        LoanApplication application = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        application.approve();
        return repository.save(application);
    }

    public LoanApplication rejectApplication(String id, String reason) {
        LoanApplication application = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        application.reject(reason);
        return repository.save(application);
    }

    public LoanApplication disburseApplication(String id) {
        LoanApplication application = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        application.disburse();
        return repository.save(application);
    }

    public LoanApplication addDocument(String id, String documentId) {
        LoanApplication application = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        application.addDocument(documentId);
        return repository.save(application);
    }

    public BigDecimal calculateMonthlyPayment(String id) {
        LoanApplication application = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        return application.calculateMonthlyPayment();
    }
}
