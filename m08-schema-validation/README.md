# Schema Validation

This module covers MongoDB Schema Validation with Spring Data.

## Domain Model

- `ValidatedPolicy`: Entity with policyNumber, active, premiumAmount, holderName

## Repository

- `ValidatedPolicyRepository`: MongoRepository with query methods

## Service

- `ValidatedPolicyService`: CRUD operations for validated policies
