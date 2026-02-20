package com.course.mongodb.m19.repository;

import com.course.mongodb.m19.domain.BankingAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankingAccountRepository extends MongoRepository<BankingAccount, String> {
    Optional<BankingAccount> findByAccountNumber(String accountNumber);
    List<BankingAccount> findByCustomerId(String customerId);
    List<BankingAccount> findByStatus(BankingAccount.AccountStatus status);
    List<BankingAccount> findByAccountType(BankingAccount.AccountType accountType);
    boolean existsByAccountNumber(String accountNumber);
}
