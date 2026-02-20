package com.course.mongodb.m02.repository;

import com.course.mongodb.m02.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategory(String category);
    List<Product> findByNameContaining(String name);
}
