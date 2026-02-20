package com.course.mongodb.m06.service;

import com.course.mongodb.m06.domain.Transaction;
import com.course.mongodb.m06.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction createTransaction(String accountId, Double amount, LocalDate date, String type) {
        Transaction transaction = new Transaction(accountId, amount, date, type);
        return repository.save(transaction);
    }

    public List<Transaction> findAll() {
        return repository.findAll();
    }

    public Transaction findById(String id) {
        return repository.findById(id).orElse(null);
    }

    public List<Transaction> findByAccountId(String accountId) {
        return repository.findByAccountId(accountId);
    }

    public List<Transaction> findByAccountIdAndType(String accountId, String type) {
        return repository.findByAccountIdAndType(accountId, type);
    }

    public List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return repository.findByDateBetween(startDate, endDate);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
