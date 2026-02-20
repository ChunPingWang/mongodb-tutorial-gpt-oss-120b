package com.course.mongodb.m10.repository;

import com.course.mongodb.m10.domain.LoanApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends MongoRepository<LoanApplication, String> {
    List<LoanApplication> findByCustomerId(String customerId);
    List<LoanApplication> findByStatus(LoanApplication.LoanStatus status);
}
