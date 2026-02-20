package com.course.mongodb.m15;

import com.course.mongodb.m15.domain.IndexedTransaction;
import com.course.mongodb.m15.service.IndexedTransactionService;
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
class IndexedTransactionServiceTest {

    @Mock
    private IndexedTransactionRepository repository;

    @InjectMocks
    private IndexedTransactionService service;

    @Test
    void createTransaction_shouldSaveNewTransaction() {
        IndexedTransaction tx = new IndexedTransaction(
            "acc123", "tx456", 
            IndexedTransaction.TransactionType.DEPOSIT, 
            new BigDecimal("100.00"), 
            "SALARY"
        );
        when(repository.save(any(IndexedTransaction.class))).thenReturn(tx);

        IndexedTransaction result = service.createTransaction(
            "acc123", 
            IndexedTransaction.TransactionType.DEPOSIT, 
            new BigDecimal("100.00"), 
            "SALARY"
        );

        assertNotNull(result);
        assertEquals("acc123", result.getAccountId());
        assertEquals(IndexedTransaction.TransactionType.DEPOSIT, result.getType());
        assertEquals(IndexedTransaction.TransactionStatus.PENDING, result.getStatus());
    }

    @Test
    void processTransaction_shouldMarkAsCompleted() {
        IndexedTransaction tx = new IndexedTransaction(
            "acc123", "tx456",
            IndexedTransaction.TransactionType.WITHDRAWAL,
            new BigDecimal("50.00"),
            "SHOPPING"
        );
        when(repository.findByTransactionId("tx456")).thenReturn(Optional.of(tx));
        when(repository.save(any(IndexedTransaction.class))).thenReturn(tx);

        IndexedTransaction result = service.processTransaction("tx456", new BigDecimal("450.00"));

        assertEquals(IndexedTransaction.TransactionStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getProcessedAt());
    }

    @Test
    void failTransaction_shouldMarkAsFailed() {
        IndexedTransaction tx = new IndexedTransaction(
            "acc123", "tx456",
            IndexedTransaction.TransactionType.TRANSFER,
            new BigDecimal("1000.00"),
            "TRANSFER"
        );
        when(repository.findByTransactionId("tx456")).thenReturn(Optional.of(tx));
        when(repository.save(any(IndexedTransaction.class))).thenReturn(tx);

        IndexedTransaction result = service.failTransaction("tx456", "Insufficient funds");

        assertEquals(IndexedTransaction.TransactionStatus.FAILED, result.getStatus());
        assertEquals("Insufficient funds", result.getDescription());
    }

    @Test
    void findByTransactionId_shouldReturnTransaction() {
        IndexedTransaction tx = new IndexedTransaction(
            "acc123", "tx456",
            IndexedTransaction.TransactionType.DEPOSIT,
            new BigDecimal("100.00"),
            "SALARY"
        );
        when(repository.findByTransactionId("tx456")).thenReturn(Optional.of(tx));

        Optional<IndexedTransaction> result = service.findByTransactionId("tx456");

        assertTrue(result.isPresent());
        assertEquals("tx456", result.get().getTransactionId());
    }
}
