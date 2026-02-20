package com.course.mongodb.m10;

import com.course.mongodb.m10.domain.LoanApplication;
import com.course.mongodb.m10.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    @InjectMocks
    private LoanApplicationService service;

    @Test
    void createApplication_shouldCreateWithDraftStatus() {
        LoanApplication result = service.createApplication(
            "customer123", 
            new BigDecimal("50000"), 
            new BigDecimal("0.05"), 
            36
        );

        assertNotNull(result);
        assertEquals("customer123", result.getCustomerId());
        assertEquals(new BigDecimal("50000"), result.getAmount());
        assertEquals(LoanApplication.LoanStatus.DRAFT, result.getStatus());
        verify(repository).save(any(LoanApplication.class));
    }

    @Test
    void submitApplication_shouldChangeStatusToPending() {
        LoanApplication application = new LoanApplication("customer123", 
            new BigDecimal("50000"), new BigDecimal("0.05"), 36);
        when(repository.findById("app1")).thenReturn(Optional.of(application));
        when(repository.save(any())).thenReturn(application);

        LoanApplication result = service.submitApplication("app1");

        assertEquals(LoanApplication.LoanStatus.PENDING, result.getStatus());
    }

    @Test
    void submitApplication_shouldThrowWhenNotDraft() {
        LoanApplication application = new LoanApplication("customer123", 
            new BigDecimal("50000"), new BigDecimal("0.05"), 36);
        application.submit();
        when(repository.findById("app1")).thenReturn(Optional.of(application));

        assertThrows(IllegalStateException.class, () -> service.submitApplication("app1"));
    }

    @Test
    void approveApplication_shouldChangeStatusToApproved() {
        LoanApplication application = new LoanApplication("customer123", 
            new BigDecimal("50000"), new BigDecimal("0.05"), 36);
        application.submit();
        when(repository.findById("app1")).thenReturn(Optional.of(application));
        when(repository.save(any())).thenReturn(application);

        LoanApplication result = service.approveApplication("app1");

        assertEquals(LoanApplication.LoanStatus.APPROVED, result.getStatus());
        assertNotNull(result.getApprovedAt());
    }

    @Test
    void rejectApplication_shouldRequireRejectionReason() {
        LoanApplication application = new LoanApplication("customer123", 
            new BigDecimal("50000"), new BigDecimal("0.05"), 36);
        application.submit();
        when(repository.findById("app1")).thenReturn(Optional.of(application));

        assertThrows(IllegalArgumentException.class, 
            () -> service.rejectApplication("app1", null));
    }

    @Test
    void calculateMonthlyPayment_shouldReturnCorrectValue() {
        LoanApplication application = new LoanApplication("customer123", 
            new BigDecimal("120000"), new BigDecimal("0.12"), 12);
        when(repository.findById("app1")).thenReturn(Optional.of(application));

        BigDecimal payment = service.calculateMonthlyPayment("app1");

        assertNotNull(payment);
        assertTrue(payment.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void addDocument_shouldAddToDocumentsList() {
        LoanApplication application = new LoanApplication("customer123", 
            new BigDecimal("50000"), new BigDecimal("0.05"), 36);
        when(repository.findById("app1")).thenReturn(Optional.of(application));
        when(repository.save(any())).thenReturn(application);

        LoanApplication result = service.addDocument("app1", "doc123");

        assertTrue(result.getDocuments().contains("doc123"));
    }
}
