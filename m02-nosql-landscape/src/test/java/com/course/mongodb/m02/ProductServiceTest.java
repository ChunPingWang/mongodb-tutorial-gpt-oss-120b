package com.course.mongodb.m02;

import com.course.mongodb.m02.domain.Product;
import com.course.mongodb.m02.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class M02ApplicationTests {

    @Autowired
    private ProductService productService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCreateProduct() {
        Product product = productService.createProduct("Laptop", 999.99, "Electronics");
        assertNotNull(product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(999.99, product.getPrice());
        assertEquals("Electronics", product.getCategory());
    }

    @Test
    public void testFindByCategory() {
        productService.createProduct("Phone", 699.99, "Electronics");
        productService.createProduct("Tablet", 499.99, "Electronics");
        
        var products = productService.findByCategory("Electronics");
        assertTrue(products.size() >= 2);
    }
}
