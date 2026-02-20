package com.course.mongodb.m20;

import com.course.mongodb.m20.domain.Claim;
import com.course.mongodb.m20.service.ClaimService;
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
class ClaimServiceTest {

    @Mock
    private ClaimRepository repository;

    @InjectMocks
    private ClaimService service;

    @Test
    void fileClaim_shouldCreateNewClaim() {
        Claim claim = new Claim(
            "CLM123", "POL456", "cust789",
            Claim.ClaimType.AUTO
        );
        claim.setClaimAmount(new BigDecimal("5000.00"));
        claim.setDescription("Car accident");
        
        when(repository.save(any(Claim.class))).thenReturn(claim);

        Claim result = service.fileClaim(
            "POL456", "cust789", 
            Claim.ClaimType.AUTO, 
            "Car accident", 
            new BigDecimal("5000.00")
        );

        assertNotNull(result);
        assertEquals("cust789", result.getCustomerId());
        assertEquals(Claim.ClaimStatus.SUBMITTED, result.getClaimStatus());
    }

    @Test
    void assignAdjuster_shouldUpdateStatus() {
        Claim claim = new Claim("CLM123", "POL456", "cust789", Claim.ClaimType.AUTO);
        when(repository.findByClaimNumber("CLM123")).thenReturn(Optional.of(claim));
        when(repository.save(any(Claim.class))).thenReturn(claim);

        Claim result = service.assignAdjuster("CLM123", "adj001");

        assertEquals(Claim.ClaimStatus.UNDER_REVIEW, result.getClaimStatus());
        assertEquals("adj001", result.getAdjusterId());
    }

    @Test
    void approveClaim_shouldSetApprovedAmount() {
        Claim claim = new Claim("CLM123", "POL456", "cust789", Claim.ClaimType.AUTO);
        claim.setClaimAmount(new BigDecimal("5000.00"));
        
        when(repository.findByClaimNumber("CLM123")).thenReturn(Optional.of(claim));
        when(repository.save(any(Claim.class))).thenReturn(claim);

        Claim result = service.approveClaim("CLM123", new BigDecimal("4500.00"));

        assertEquals(Claim.ClaimStatus.APPROVED, result.getClaimStatus());
        assertEquals(new BigDecimal("4500.00"), result.getApprovedAmount());
    }

    @Test
    void denyClaim_shouldUpdateStatus() {
        Claim claim = new Claim("CLM123", "POL456", "cust789", Claim.ClaimType.AUTO);
        when(repository.findByClaimNumber("CLM123")).thenReturn(Optional.of(claim));
        when(repository.save(any(Claim.class))).thenReturn(claim);

        Claim result = service.denyClaim("CLM123", "Policy excluded coverage");

        assertEquals(Claim.ClaimStatus.DENIED, result.getClaimStatus());
    }

    @Test
    void findByClaimNumber_shouldReturnClaim() {
        Claim claim = new Claim("CLM123", "POL456", "cust789", Claim.ClaimType.HOME);
        when(repository.findByClaimNumber("CLM123")).thenReturn(Optional.of(claim));

        Optional<Claim> result = service.findByClaimNumber("CLM123");

        assertTrue(result.isPresent());
        assertEquals("CLM123", result.get().getClaimNumber());
    }
}
