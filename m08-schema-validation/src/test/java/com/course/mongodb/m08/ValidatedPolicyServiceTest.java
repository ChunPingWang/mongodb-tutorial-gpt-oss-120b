package com.course.mongodb.m08;

import com.course.mongodb.m08.domain.ValidatedPolicy;
import com.course.mongodb.m08.service.ValidatedPolicyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ValidatedPolicyServiceTest {

    @Autowired
    private ValidatedPolicyService policyService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCreatePolicy() {
        ValidatedPolicy policy = policyService.createPolicy("POL-100", true, 2500.00, "Alice Johnson");
        
        assertNotNull(policy.getId());
        assertEquals("POL-100", policy.getPolicyNumber());
        assertTrue(policy.isActive());
        assertEquals(2500.00, policy.getPremiumAmount());
        assertEquals("Alice Johnson", policy.getHolderName());
    }

    @Test
    public void testFindByActive() {
        policyService.createPolicy("POL-101", true, 3000.00, "Bob Smith");
        
        var policies = policyService.findByActive(true);
        assertTrue(policies.size() >= 1);
    }
}
