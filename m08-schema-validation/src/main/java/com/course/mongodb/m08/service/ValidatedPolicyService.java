package com.course.mongodb.m08.service;

import com.course.mongodb.m08.domain.ValidatedPolicy;
import com.course.mongodb.m08.repository.ValidatedPolicyRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ValidatedPolicyService {

    private final ValidatedPolicyRepository repository;

    public ValidatedPolicyService(ValidatedPolicyRepository repository) {
        this.repository = repository;
    }

    public ValidatedPolicy createPolicy(String policyNumber, boolean active, Double premiumAmount, String holderName) {
        ValidatedPolicy policy = new ValidatedPolicy(policyNumber, active, premiumAmount, holderName);
        return repository.save(policy);
    }

    public List<ValidatedPolicy> findAll() {
        return repository.findAll();
    }

    public ValidatedPolicy findById(String id) {
        return repository.findById(id).orElse(null);
    }

    public List<ValidatedPolicy> findByPolicyNumber(String policyNumber) {
        return repository.findByPolicyNumber(policyNumber);
    }

    public List<ValidatedPolicy> findByActive(boolean active) {
        return repository.findByActive(active);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
