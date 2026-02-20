# Spring Data CRUD

This module covers Spring Data MongoDB CRUD operations.

## Domain Model

- `InsurancePolicy`: Entity with policyNumber, premium, status, and embedded InsuredPerson

## Repository

- `InsurancePolicyRepository`: MongoRepository with query methods

## Service

- `InsurancePolicyService`: CRUD operations for insurance policies
