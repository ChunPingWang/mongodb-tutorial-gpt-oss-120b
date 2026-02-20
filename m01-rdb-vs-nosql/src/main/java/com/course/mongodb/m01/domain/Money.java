package com.course.mongodb.m01.domain;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {
    public static Money ZERO = new Money(BigDecimal.ZERO, "TWD");
    
    public static Money of(BigDecimal amount) {
        return new Money(amount, "TWD");
    }
    
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount), this.currency);
    }
    
    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }
}
