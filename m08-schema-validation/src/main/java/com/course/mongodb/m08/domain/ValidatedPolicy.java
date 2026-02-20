package com.course.mongodb.m08.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.bson.types.ObjectId;

@Document(collection = "validated_policies")
public class ValidatedPolicy {
    @Id
    private ObjectId id;

    @Field("policy_number")
    private String policyNumber;

    private boolean active;

    @Field("premium_amount")
    private Double premiumAmount;

    private String holderName;

    public ValidatedPolicy() {
    }

    public ValidatedPolicy(String policyNumber, boolean active, Double premiumAmount, String holderName) {
        this.policyNumber = policyNumber;
        this.active = active;
        this.premiumAmount = premiumAmount;
        this.holderName = holderName;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Double getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(Double premiumAmount) { this.premiumAmount = premiumAmount; }
    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
}
