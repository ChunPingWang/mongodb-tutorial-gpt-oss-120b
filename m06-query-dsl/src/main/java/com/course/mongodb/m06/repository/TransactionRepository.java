package com.course.mongodb.m06.repository;

import com.course.mongodb.m06.domain.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountId);
    List<Transaction> findByAccountIdAndType(String accountId, String type);
    List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
