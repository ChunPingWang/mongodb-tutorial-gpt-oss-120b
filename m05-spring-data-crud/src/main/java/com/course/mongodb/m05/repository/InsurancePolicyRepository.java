package com.course.mongodb.m05.repository;

import com.course.mongodb.m05.domain.InsurancePolicy;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface InsurancePolicyRepository extends MongoRepository<InsurancePolicy, String> {
    List<InsurancePolicy> findByPolicyNumber(String policyNumber);
    List<InsurancePolicy> findByStatus(String status);
    List<InsurancePolicy> findByInsuredName(String name);
}
