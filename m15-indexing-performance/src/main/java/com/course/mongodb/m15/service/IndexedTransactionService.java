package com.course.mongodb.m15.service;

import com.course.mongodb.m15.domain.IndexedTransaction;
import com.course.mongodb.m15.repository.IndexedTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IndexedTransactionService {

    private final IndexedTransactionRepository repository;

    public IndexedTransactionService(IndexedTransactionRepository repository) {
        this.repository = repository;
    }

    public IndexedTransaction createTransaction(String accountId, IndexedTransaction.TransactionType type,
                                                 BigDecimal amount, String category) {
        IndexedTransaction tx = new IndexedTransaction(
            accountId,
            UUID.randomUUID().toString(),
            type,
            amount,
            category
        );
        return repository.save(tx);
    }

    public Optional<IndexedTransaction> findByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId);
    }

    public List<IndexedTransaction> findByAccountId(String accountId) {
        return repository.findByAccountId(accountId);
    }

    public List<IndexedTransaction> findAccountTransactionsInRange(String accountId, 
                                                                     Instant start, Instant end) {
        return repository.findByAccountIdAndTransactionDateBetween(accountId, start, end);
    }

    public List<IndexedTransaction> findByStatus(IndexedTransaction.TransactionStatus status) {
        return repository.findByStatus(status);
    }

    public List<IndexedTransaction> findByCategory(String category) {
        return repository.findByCategory(category);
    }

    public IndexedTransaction processTransaction(String transactionId, BigDecimal balanceAfter) {
        IndexedTransaction tx = repository.findByTransactionId(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        
        tx.setBalanceAfter(balanceAfter);
        tx.process();
        
        return repository.save(tx);
    }

    public IndexedTransaction failTransaction(String transactionId, String reason) {
        IndexedTransaction tx = repository.findByTransactionId(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        
        tx.fail(reason);
        return repository.save(tx);
    }

    public List<IndexedTransaction> findForeignTransactions() {
        return repository.findByIsForeign(true);
    }
}
