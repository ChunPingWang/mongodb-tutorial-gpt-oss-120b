package com.course.mongodb.m01;

import com.course.mongodb.m01.domain.BankAccount;
import com.course.mongodb.m01.domain.Money;
import com.course.mongodb.m01.domain.AccountStatus;
import com.course.mongodb.m01.repository.BankAccountRepository;
import com.course.mongodb.m01.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AccountServiceTest extends MongoIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private BankAccountRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateAccount() {
        BankAccount account = accountService.createAccount(
            "A001", "C001", Money.of(new BigDecimal("10000"))
        );

        assertThat(account.getId()).isNotNull();
        assertThat(account.getAccountNumber()).isEqualTo("A001");
        assertThat(account.getBalance().amount()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    void shouldDepositMoney() {
        accountService.createAccount("A001", "C001", Money.of(new BigDecimal("10000")));
        
        accountService.deposit("A001", Money.of(new BigDecimal("5000")));

        BankAccount account = accountService.findByAccountNumber("A001");
        assertThat(account.getBalance().amount()).isEqualByComparingTo(new BigDecimal("15000"));
    }

    @Test
    void shouldWithdrawMoney() {
        accountService.createAccount("A001", "C001", Money.of(new BigDecimal("10000")));
        
        accountService.withdraw("A001", Money.of(new BigDecimal("3000"));

        BankAccount account = accountService.findByAccountNumber("A001");
        assertThat(account.getBalance().amount()).isEqualByComparingTo(new BigDecimal("7000"));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        accountService.createAccount("A001", "C001", Money.of(new BigDecimal("1000")));

        assertThatThrownBy(() -> 
            accountService.withdraw("A001", Money.of(new BigDecimal("2000")))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldFreezeAccount() {
        accountService.createAccount("A001", "C001", Money.of(new BigDecimal("10000")));
        
        accountService.freezeAccount("A001");

        BankAccount account = accountService.findByAccountNumber("A001");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);
    }
}
