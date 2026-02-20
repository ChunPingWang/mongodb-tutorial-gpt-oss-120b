package com.course.mongodb.m15.repository;

import com.course.mongodb.m15.domain.IndexedTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface IndexedTransactionRepository extends MongoRepository<IndexedTransaction, String> {
    Optional<IndexedTransaction> findByTransactionId(String transactionId);
    List<IndexedTransaction> findByAccountId(String accountId);
    List<IndexedTransaction> findByAccountIdAndTransactionDateBetween(String accountId, 
                                                                       Instant start, Instant end);
    List<IndexedTransaction> findByStatus(IndexedTransaction.TransactionStatus status);
    List<IndexedTransaction> findByCategory(String category);
    List<IndexedTransaction> findByIsForeign(boolean isForeign);
}
