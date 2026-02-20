package com.course.mongodb.m01.domain;

import java.time.Instant;

public record Transaction(
    String id,
    String accountId,
    Money amount,
    TransactionType type,
    Instant timestamp
) {
    public static Transaction create(String accountId, Money amount, TransactionType type) {
        return new Transaction(null, accountId, amount, type, Instant.now());
    }
}
