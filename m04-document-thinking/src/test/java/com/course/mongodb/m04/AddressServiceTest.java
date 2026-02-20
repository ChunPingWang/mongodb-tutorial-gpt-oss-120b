package com.course.mongodb.m04;

import com.course.mongodb.m04.domain.Address;
import com.course.mongodb.m04.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AddressServiceTest {

    @Autowired
    private AddressService addressService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCreateAddress() {
        Address address = addressService.createAddress("New York", "Manhattan", "123 Broadway");
        assertNotNull(address.getId());
        assertEquals("New York", address.getCity());
        assertEquals("Manhattan", address.getDistrict());
        assertEquals("123 Broadway", address.getDetail());
    }

    @Test
    public void testFindByCity() {
        addressService.createAddress("Los Angeles", "Downtown", "456 Main St");
        
        var addresses = addressService.findByCity("Los Angeles");
        assertTrue(addresses.size() >= 1);
    }
}
