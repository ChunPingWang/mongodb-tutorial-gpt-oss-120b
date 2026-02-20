package com.course.mongodb.m01.repository;

import com.course.mongodb.m01.domain.BankAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface BankAccountRepository extends MongoRepository<BankAccount, String> {
    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
