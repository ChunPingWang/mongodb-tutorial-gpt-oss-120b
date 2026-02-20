package com.course.mongodb.m03.repository;

import com.course.mongodb.m03.domain.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByNameContaining(String name);
}
