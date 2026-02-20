package com.course.mongodb.m05;

import com.course.mongodb.m05.domain.InsurancePolicy;
import com.course.mongodb.m05.service.InsurancePolicyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InsurancePolicyServiceTest {

    @Autowired
    private InsurancePolicyService policyService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCreatePolicy() {
        InsurancePolicy.InsuredPerson insured = new InsurancePolicy.InsuredPerson("John Doe", 35, "john@example.com");
        InsurancePolicy policy = policyService.createPolicy("POL-001", 1500.00, "ACTIVE", insured);
        
        assertNotNull(policy.getId());
        assertEquals("POL-001", policy.getPolicyNumber());
        assertEquals(1500.00, policy.getPremium());
        assertEquals("ACTIVE", policy.getStatus());
        assertNotNull(policy.getInsured());
        assertEquals("John Doe", policy.getInsured().getName());
    }

    @Test
    public void testFindByStatus() {
        InsurancePolicy.InsuredPerson insured = new InsurancePolicy.InsuredPerson("Jane Smith", 28, "jane@example.com");
        policyService.createPolicy("POL-002", 2000.00, "ACTIVE", insured);
        
        var policies = policyService.findByStatus("ACTIVE");
        assertTrue(policies.size() >= 1);
    }
}
