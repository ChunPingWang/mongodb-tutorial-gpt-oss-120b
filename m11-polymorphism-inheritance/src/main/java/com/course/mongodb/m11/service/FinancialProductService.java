package com.course.mongodb.m11.service;

import com.course.mongodb.m11.domain.FinancialProduct;
import com.course.mongodb.m11.repository.FinancialProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class FinancialProductService {

    private final FinancialProductRepository repository;

    public FinancialProductService(FinancialProductRepository repository) {
        this.repository = repository;
    }

    public FinancialProduct createDeposit(String customerId, BigDecimal amount, 
                                          BigDecimal interestRate, Integer termMonths) {
        FinancialProduct.Deposit deposit = FinancialProduct.Deposit.create(
            customerId, amount, interestRate, termMonths);
        return repository.save(deposit);
    }

    public FinancialProduct createFund(String customerId, BigDecimal amount, String fundType) {
        FinancialProduct.Fund fund = FinancialProduct.Fund.create(customerId, amount, fundType);
        return repository.save(fund);
    }

    public Optional<FinancialProduct> findById(String id) {
        return repository.findById(id);
    }

    public List<FinancialProduct> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    public BigDecimal calculateTotalValue(String customerId) {
        return findByCustomerId(customerId).stream()
            .map(FinancialProduct::calculateValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<FinancialProduct.Deposit> getDeposits(String customerId) {
        return findByCustomerId(customerId).stream()
            .filter(p -> p instanceof FinancialProduct.Deposit)
            .map(p -> (FinancialProduct.Deposit) p)
            .toList();
    }

    public List<FinancialProduct.Fund> getFunds(String customerId) {
        return findByCustomerId(customerId).stream()
            .filter(p -> p instanceof FinancialProduct.Fund)
            .map(p -> (FinancialProduct.Fund) p)
            .toList();
    }
}
