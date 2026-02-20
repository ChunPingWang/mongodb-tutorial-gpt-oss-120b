package com.course.mongodb.m08.repository;

import com.course.mongodb.m08.domain.ValidatedPolicy;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ValidatedPolicyRepository extends MongoRepository<ValidatedPolicy, String> {
    List<ValidatedPolicy> findByPolicyNumber(String policyNumber);
    List<ValidatedPolicy> findByActive(boolean active);
    List<ValidatedPolicy> findByHolderName(String holderName);
}
