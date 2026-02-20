package com.course.mongodb.m09;

import com.course.mongodb.m09.domain.Transfer;
import com.course.mongodb.m09.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransferServiceTest {

    @Autowired
    private TransferService transferService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCreateTransfer() {
        Transfer transfer = transferService.createTransfer("ACC-001", "ACC-002", 1000.00, "PENDING");
        
        assertNotNull(transfer.getId());
        assertEquals("ACC-001", transfer.getFromAccount());
        assertEquals("ACC-002", transfer.getToAccount());
        assertEquals(1000.00, transfer.getAmount());
        assertEquals("PENDING", transfer.getStatus());
    }

    @Test
    public void testFindByStatus() {
        transferService.createTransfer("ACC-003", "ACC-004", 500.00, "COMPLETED");
        
        var transfers = transferService.findByStatus("COMPLETED");
        assertTrue(transfers.size() >= 1);
    }
}
