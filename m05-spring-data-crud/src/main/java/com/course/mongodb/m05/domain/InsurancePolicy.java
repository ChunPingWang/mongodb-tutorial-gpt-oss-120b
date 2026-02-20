package com.course.mongodb.m05.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "insurance_policies")
public class InsurancePolicy {
    @Id
    private String id;

    @Field("policy_number")
    private String policyNumber;

    private Double premium;

    private String status;

    @Field("insured")
    private InsuredPerson insured;

    public InsurancePolicy() {
    }

    public InsurancePolicy(String policyNumber, Double premium, String status, InsuredPerson insured) {
        this.policyNumber = policyNumber;
        this.premium = premium;
        this.status = status;
        this.insured = insured;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public Double getPremium() { return premium; }
    public void setPremium(Double premium) { this.premium = premium; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public InsuredPerson getInsured() { return insured; }
    public void setInsured(InsuredPerson insured) { this.insured = insured; }

    public static class InsuredPerson {
        private String name;
        private Integer age;
        private String email;

        public InsuredPerson() {
        }

        public InsuredPerson(String name, Integer age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
