package com.course.mongodb.m06;

import com.course.mongodb.m06.domain.Transaction;
import com.course.mongodb.m06.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCreateTransaction() {
        Transaction transaction = transactionService.createTransaction(
            "ACC-001", 500.00, LocalDate.of(2024, 1, 15), "DEPOSIT");
        
        assertNotNull(transaction.getId());
        assertEquals("ACC-001", transaction.getAccountId());
        assertEquals(500.00, transaction.getAmount());
        assertEquals("DEPOSIT", transaction.getType());
    }

    @Test
    public void testFindByAccountId() {
        transactionService.createTransaction("ACC-002", 100.00, LocalDate.now(), "WITHDRAWAL");
        
        List<Transaction> transactions = transactionService.findByAccountId("ACC-002");
        assertTrue(transactions.size() >= 1);
    }
}
