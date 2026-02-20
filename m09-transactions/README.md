# Transactions

This module covers MongoDB Transactions with Spring Data.

## Domain Model

- `Transfer`: Entity with fromAccount, toAccount, amount, status

## Repository

- `TransferRepository`: MongoRepository with query methods

## Service

- `TransferService`: CRUD and transaction operations for transfers
