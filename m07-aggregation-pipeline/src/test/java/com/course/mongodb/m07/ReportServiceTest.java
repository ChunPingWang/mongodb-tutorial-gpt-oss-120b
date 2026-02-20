package com.course.mongodb.m07;

import com.course.mongodb.m07.domain.Report;
import com.course.mongodb.m07.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCreateReport() {
        Report report = reportService.createReport("ACC-001", "2024-01", 5000.00, 3000.00);
        
        assertNotNull(report.getId());
        assertEquals("ACC-001", report.getAccountId());
        assertEquals("2024-01", report.getMonth());
        assertEquals(5000.00, report.getIncome());
        assertEquals(3000.00, report.getExpense());
    }

    @Test
    public void testFindByAccountId() {
        reportService.createReport("ACC-002", "2024-02", 4000.00, 2000.00);
        
        var reports = reportService.findByAccountId("ACC-002");
        assertTrue(reports.size() >= 1);
    }
}
