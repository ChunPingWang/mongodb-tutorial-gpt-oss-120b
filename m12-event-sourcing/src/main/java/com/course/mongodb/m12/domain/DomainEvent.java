package com.course.mongodb.m12.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public sealed interface DomainEvent permits DomainEvent.AccountOpenedEvent, DomainEvent.FundsDepositedEvent {

    String eventId();
    String aggregateId();
    Instant occurredAt();
    int version();

    record AccountOpenedEvent(
        String eventId,
        String aggregateId,
        Instant occurredAt,
        int version,
        String accountHolderName,
        String accountType,
        BigDecimal initialBalance
    ) implements DomainEvent {
        public static AccountOpenedEvent create(String accountHolderName, String accountType, BigDecimal initialBalance) {
            return new AccountOpenedEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Instant.now(),
                1,
                accountHolderName,
                accountType,
                initialBalance
            );
        }
    }

    record FundsDepositedEvent(
        String eventId,
        String aggregateId,
        Instant occurredAt,
        int version,
        BigDecimal amount,
        String depositMethod,
        String reference
    ) implements DomainEvent {
        public static FundsDepositedEvent create(String aggregateId, int version, 
                                                  BigDecimal amount, String depositMethod) {
            return new FundsDepositedEvent(
                UUID.randomUUID().toString(),
                aggregateId,
                Instant.now(),
                version,
                amount,
                depositMethod,
                UUID.randomUUID().toString()
            );
        }
    }
}
