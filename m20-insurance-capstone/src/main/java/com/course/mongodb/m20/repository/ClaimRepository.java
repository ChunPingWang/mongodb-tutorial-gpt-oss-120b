package com.course.mongodb.m20.repository;

import com.course.mongodb.m20.domain.Claim;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends MongoRepository<Claim, String> {
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByPolicyId(String policyId);
    List<Claim> findByCustomerId(String customerId);
    List<Claim> findByClaimStatus(Claim.ClaimStatus status);
    List<Claim> findByClaimType(Claim.ClaimType claimType);
    List<Claim> findByAdjusterId(String adjusterId);
}
