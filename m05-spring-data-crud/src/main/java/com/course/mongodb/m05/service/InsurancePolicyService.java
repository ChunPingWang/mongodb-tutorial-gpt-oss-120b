package com.course.mongodb.m05.service;

import com.course.mongodb.m05.domain.InsurancePolicy;
import com.course.mongodb.m05.repository.InsurancePolicyRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InsurancePolicyService {

    private final InsurancePolicyRepository repository;

    public InsurancePolicyService(InsurancePolicyRepository repository) {
        this.repository = repository;
    }

    public InsurancePolicy createPolicy(String policyNumber, Double premium, String status,
            InsurancePolicy.InsuredPerson insured) {
        InsurancePolicy policy = new InsurancePolicy(policyNumber, premium, status, insured);
        return repository.save(policy);
    }

    public List<InsurancePolicy> findAll() {
        return repository.findAll();
    }

    public InsurancePolicy findById(String id) {
        return repository.findById(id).orElse(null);
    }

    public InsurancePolicy updateStatus(String id, String status) {
        InsurancePolicy policy = findById(id);
        if (policy != null) {
            policy.setStatus(status);
            return repository.save(policy);
        }
        return null;
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public List<InsurancePolicy> findByStatus(String status) {
        return repository.findByStatus(status);
    }
}
