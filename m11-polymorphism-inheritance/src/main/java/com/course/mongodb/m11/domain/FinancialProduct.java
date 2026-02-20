package com.course.mongodb.m11.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "financial_products")
public sealed interface FinancialProduct permits FinancialProduct.Deposit, FinancialProduct.Fund {

    @Id
    String id();

    @Field("customer_id")
    String customerId();

    BigDecimal balance();

    @Field("created_at")
    Instant createdAt();

    record Deposit(
        @Id String id,
        @Field("customer_id") String customerId,
        BigDecimal balance,
        @Field("created_at") Instant createdAt,
        @Field("interest_rate") BigDecimal interestRate,
        @Field("term_months") Integer termMonths,
        @Field("compounding_frequency") String compoundingFrequency
    ) implements FinancialProduct {
        public static Deposit create(String customerId, BigDecimal balance, 
                                     BigDecimal interestRate, Integer termMonths) {
            return new Deposit(null, customerId, balance, Instant.now(), 
                              interestRate, termMonths, "MONTHLY");
        }
    }

    record Fund(
        @Id String id,
        @Field("customer_id") String customerId,
        BigDecimal balance,
        @Field("created_at") Instant createdAt,
        @Field("nav") BigDecimal nav,
        @Field("shares") BigDecimal shares,
        @Field("fund_type") String fundType
    ) implements FinancialProduct {
        public static Fund create(String customerId, BigDecimal amount, String fundType) {
            BigDecimal nav = new BigDecimal("100.00");
            BigDecimal shares = amount.divide(nav, 4, java.math.RoundingMode.HALF_UP);
            return new Fund(null, customerId, amount, Instant.now(), nav, shares, fundType);
        }
    }

    default BigDecimal calculateValue() {
        return balance();
    }
}
