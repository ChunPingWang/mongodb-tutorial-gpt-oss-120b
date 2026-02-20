package com.course.mongodb.m13.repository;

import com.course.mongodb.m13.domain.Customer360View;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Customer360ViewRepository extends MongoRepository<Customer360View, String> {
    Optional<Customer360View> findByCustomerId(String customerId);
}
