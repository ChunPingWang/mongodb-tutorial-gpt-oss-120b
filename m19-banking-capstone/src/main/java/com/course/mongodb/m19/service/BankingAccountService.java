package com.course.mongodb.m19.service;

import com.course.mongodb.m19.domain.BankingAccount;
import com.course.mongodb.m19.domain.BankingAccount.Transaction;
import com.course.mongodb.m19.domain.BankingAccount.Beneficiary;
import com.course.mongodb.m19.domain.BankingAccount.Statement;
import com.course.mongodb.m19.repository.BankingAccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BankingAccountService {

    private final BankingAccountRepository repository;

    public BankingAccountService(BankingAccountRepository repository) {
        this.repository = repository;
    }

    public BankingAccount createAccount(String customerId, BankingAccount.AccountType accountType, String currency) {
        String accountNumber = generateAccountNumber();
        BankingAccount account = new BankingAccount(accountNumber, customerId, accountType, currency);
        
        if (accountType == BankingAccount.AccountType.SAVINGS) {
            account.setInterestRate(new BigDecimal("0.025"));
        } else if (accountType == BankingAccount.AccountType.FIXED_DEPOSIT) {
            account.setInterestRate(new BigDecimal("0.045"));
        }
        
        return repository.save(account);
    }

    public Optional<BankingAccount> findByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber);
    }

    public List<BankingAccount> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    public BankingAccount credit(String accountNumber, BigDecimal amount, String description) {
        BankingAccount account = repository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        
        if (account.getStatus() != BankingAccount.AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        
        Transaction tx = new Transaction(Transaction.TransactionType.DEPOSIT, amount, description);
        account.credit(amount);
        account.addTransaction(tx);
        
        return repository.save(account);
    }

    public BankingAccount debit(String accountNumber, BigDecimal amount, String description) {
        BankingAccount account = repository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        
        BigDecimal maxDebit = account.getAvailableBalance().add(
            account.getOverdraftLimit() != null ? account.getOverdraftLimit() : BigDecimal.ZERO
        );
        
        if (amount.compareTo(maxDebit) > 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        Transaction tx = new Transaction(Transaction.TransactionType.WITHDRAWAL, amount, description);
        account.debit(amount);
        account.addTransaction(tx);
        
        return repository.save(account);
    }

    public BankingAccount transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        BankingAccount fromAccount = repository.findByAccountNumber(fromAccountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        
        debit(fromAccountNumber, amount, "Transfer to " + toAccountNumber);
        
        BankingAccount toAccount = repository.findByAccountNumber(toAccountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));
        
        credit(toAccountNumber, amount, "Transfer from " + fromAccountNumber);
        
        return fromAccount;
    }

    public BankingAccount addBeneficiary(String accountNumber, String name, String beneficiaryAccountNumber) {
        BankingAccount account = repository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        
        Beneficiary beneficiary = new Beneficiary(
            UUID.randomUUID().toString(),
            name,
            beneficiaryAccountNumber
        );
        
        account.addBeneficiary(beneficiary);
        return repository.save(account);
    }

    public BankingAccount generateStatement(String accountNumber, Instant periodStart, Instant periodEnd) {
        BankingAccount account = repository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        
        Statement statement = new Statement(UUID.randomUUID().toString(), periodStart, periodEnd);
        statement.setOpeningBalance(account.getBalance());
        
        BigDecimal credits = account.getTransactions().stream()
            .filter(tx -> tx.getTransactionDate().isAfter(periodStart) && tx.getTransactionDate().isBefore(periodEnd))
            .filter(tx -> tx.getType() == Transaction.TransactionType.DEPOSIT)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal debits = account.getTransactions().stream()
            .filter(tx -> tx.getTransactionDate().isAfter(periodStart) && tx.getTransactionDate().isBefore(periodEnd))
            .filter(tx -> tx.getType() == Transaction.TransactionType.WITHDRAWAL)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        statement.setTotalCredits(credits);
        statement.setTotalDebits(debits);
        statement.setClosingBalance(credits.subtract(debits));
        
        account.getStatements().add(statement);
        return repository.save(account);
    }

    public BankingAccount closeAccount(String accountNumber) {
        BankingAccount account = repository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }
        
        account.close();
        return repository.save(account);
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
}
