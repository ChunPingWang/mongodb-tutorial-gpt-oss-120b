package com.course.mongodb.m03;

import com.course.mongodb.m03.domain.Customer;
import com.course.mongodb.m03.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCreateCustomer() {
        Customer customer = customerService.createCustomer("John Doe", "john@example.com");
        assertNotNull(customer.getId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
    }

    @Test
    public void testFindByEmail() {
        customerService.createCustomer("Jane Doe", "jane@example.com");
        
        var customer = customerService.findByEmail("jane@example.com");
        assertNotNull(customer);
        assertEquals("Jane Doe", customer.getName());
    }
}
