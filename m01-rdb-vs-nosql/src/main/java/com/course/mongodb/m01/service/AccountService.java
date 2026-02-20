package com.course.mongodb.m01.service;

import com.course.mongodb.m01.domain.BankAccount;
import com.course.mongodb.m01.domain.Money;
import com.course.mongodb.m01.domain.AccountStatus;
import com.course.mongodb.m01.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final BankAccountRepository repository;

    public AccountService(BankAccountRepository repository) {
        this.repository = repository;
    }

    public BankAccount createAccount(String accountNumber, String customerId, Money initialBalance) {
        BankAccount account = new BankAccount(accountNumber, customerId, initialBalance);
        return repository.save(account);
    }

    public BankAccount findByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber).orElse(null);
    }

    public void deposit(String accountNumber, Money amount) {
        BankAccount account = findByAccountNumber(accountNumber);
        if (account != null) {
            account.deposit(amount);
            repository.save(account);
        }
    }

    public void withdraw(String accountNumber, Money amount) {
        BankAccount account = findByAccountNumber(accountNumber);
        if (account != null) {
            account.withdraw(amount);
            repository.save(account);
        }
    }

    public void freezeAccount(String accountNumber) {
        BankAccount account = findByAccountNumber(accountNumber);
        if (account != null) {
            account.setStatus(AccountStatus.FROZEN);
            repository.save(account);
        }
    }
}
