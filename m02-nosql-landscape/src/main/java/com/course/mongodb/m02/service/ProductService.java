package com.course.mongodb.m02.service;

import com.course.mongodb.m02.domain.Product;
import com.course.mongodb.m02.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product createProduct(String name, Double price, String category) {
        Product product = new Product(name, price, category);
        return repository.save(product);
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product findById(String id) {
        return repository.findById(id).orElse(null);
    }

    public List<Product> findByCategory(String category) {
        return repository.findByCategory(category);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
