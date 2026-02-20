package com.course.mongodb.m19;

import com.course.mongodb.m19.domain.BankingAccount;
import com.course.mongodb.m19.domain.BankingAccount.Transaction;
import com.course.mongodb.m19.service.BankingAccountService;
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
class BankingAccountServiceTest {

    @Mock
    private BankingAccountRepository repository;

    @InjectMocks
    private BankingAccountService service;

    @Test
    void createAccount_shouldCreateNewAccount() {
        BankingAccount account = new BankingAccount(
            "ACC123", "cust456", 
            BankingAccount.AccountType.CHECKING, "USD"
        );
        when(repository.save(any(BankingAccount.class))).thenReturn(account);

        BankingAccount result = service.createAccount(
            "cust456", BankingAccount.AccountType.CHECKING, "USD"
        );

        assertNotNull(result);
        assertEquals("cust456", result.getCustomerId());
        assertEquals(BankingAccount.AccountType.CHECKING, result.getAccountType());
        assertEquals(BankingAccount.AccountStatus.ACTIVE, result.getStatus());
    }

    @Test
    void credit_shouldIncreaseBalance() {
        BankingAccount account = new BankingAccount(
            "ACC123", "cust456",
            BankingAccount.AccountType.CHECKING, "USD"
        );
        account.setBalance(new BigDecimal("1000.00"));
        account.setAvailableBalance(new BigDecimal("1000.00"));
        
        when(repository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
        when(repository.save(any(BankingAccount.class))).thenReturn(account);

        BankingAccount result = service.credit("ACC123", new BigDecimal("500.00"), "Deposit");

        assertEquals(new BigDecimal("1500.00"), result.getBalance());
    }

    @Test
    void debit_shouldDecreaseBalance() {
        BankingAccount account = new BankingAccount(
            "ACC123", "cust456",
            BankingAccount.AccountType.CHECKING, "USD"
        );
        account.setBalance(new BigDecimal("1000.00"));
        account.setAvailableBalance(new BigDecimal("1000.00"));
        
        when(repository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
        when(repository.save(any(BankingAccount.class))).thenReturn(account);

        BankingAccount result = service.debit("ACC123", new BigDecimal("300.00"), "Withdrawal");

        assertEquals(new BigDecimal("700.00"), result.getBalance());
    }

    @Test
    void debit_shouldThrowWhenInsufficientFunds() {
        BankingAccount account = new BankingAccount(
            "ACC123", "cust456",
            BankingAccount.AccountType.CHECKING, "USD"
        );
        account.setBalance(new BigDecimal("100.00"));
        account.setAvailableBalance(new BigDecimal("100.00"));
        
        when(repository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class, () -> 
            service.debit("ACC123", new BigDecimal("500.00"), "Withdrawal")
        );
    }

    @Test
    void findByAccountNumber_shouldReturnAccount() {
        BankingAccount account = new BankingAccount(
            "ACC123", "cust456",
            BankingAccount.AccountType.SAVINGS, "USD"
        );
        when(repository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));

        Optional<BankingAccount> result = service.findByAccountNumber("ACC123");

        assertTrue(result.isPresent());
        assertEquals("ACC123", result.get().getAccountNumber());
    }
}
