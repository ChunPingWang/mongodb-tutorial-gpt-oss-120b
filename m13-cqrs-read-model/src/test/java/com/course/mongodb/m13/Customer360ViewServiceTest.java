package com.course.mongodb.m13;

import com.course.mongodb.m13.domain.Customer360View;
import com.course.mongodb.m13.service.Customer360ViewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Customer360ViewServiceTest {

    @Mock
    private Customer360ViewRepository repository;

    @InjectMocks
    private Customer360ViewService service;

    @Test
    void createCustomerView_shouldCreateWithInitialValues() {
        Customer360View view = new Customer360View("customer123", "John Doe", "john@example.com");
        when(repository.save(any(Customer360View.class))).thenReturn(view);

        Customer360View result = service.createCustomerView("customer123", "John Doe", "john@example.com");

        assertNotNull(result);
        assertEquals("customer123", result.getCustomerId());
        assertEquals("John Doe", result.getName());
        assertEquals(BigDecimal.ZERO, result.getTotalBalance());
        assertEquals(0, result.getAccountCount());
    }

    @Test
    void findByCustomerId_shouldReturnCustomerView() {
        Customer360View view = new Customer360View("customer123", "John Doe", "john@example.com");
        when(repository.findByCustomerId("customer123")).thenReturn(Optional.of(view));

        Optional<Customer360View> result = service.findByCustomerId("customer123");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    void updateAccounts_shouldUpdateAccountsAndBalance() {
        Customer360View view = new Customer360View("customer123", "John Doe", "john@example.com");
        when(repository.findByCustomerId("customer123")).thenReturn(Optional.of(view));
        when(repository.save(any(Customer360View.class))).thenReturn(view);

        List<Customer360View.AccountSummary> accounts = List.of(
            new Customer360View.AccountSummary("acc1", "SAVINGS", new BigDecimal("5000"), "ACTIVE"),
            new Customer360View.AccountSummary("acc2", "CHECKING", new BigDecimal("3000"), "ACTIVE")
        );

        Customer360View result = service.updateAccounts("customer123", accounts);

        assertEquals(2, result.getAccountCount());
        assertEquals(new BigDecimal("8000"), result.getTotalBalance());
    }

    @Test
    void updateLoans_shouldUpdateLoanCount() {
        Customer360View view = new Customer360View("customer123", "John Doe", "john@example.com");
        when(repository.findByCustomerId("customer123")).thenReturn(Optional.of(view));
        when(repository.save(any(Customer360View.class))).thenReturn(view);

        List<Customer360View.LoanSummary> loans = List.of(
            new Customer360View.LoanSummary("loan1", new BigDecimal("10000"), "ACTIVE", new BigDecimal("8000")),
            new Customer360View.LoanSummary("loan2", new BigDecimal("5000"), "CLOSED", new BigDecimal("0"))
        );

        Customer360View result = service.updateLoans("customer123", loans);

        assertEquals(1, result.getActiveLoans());
    }

    @Test
    void deleteByCustomerId_shouldDeleteIfExists() {
        Customer360View view = new Customer360View("customer123", "John Doe", "john@example.com");
        when(repository.findByCustomerId("customer123")).thenReturn(Optional.of(view));
        doNothing().when(repository).delete(view);

        service.deleteByCustomerId("customer123");

        verify(repository).delete(view);
    }
}
