package com.course.mongodb.m11.repository;

import com.course.mongodb.m11.domain.FinancialProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialProductRepository extends MongoRepository<FinancialProduct, String> {
    List<FinancialProduct> findByCustomerId(String customerId);
}
