package com.course.mongodb.m11;

import com.course.mongodb.m11.domain.FinancialProduct;
import com.course.mongodb.m11.service.FinancialProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialProductServiceTest {

    @Mock
    private FinancialProductRepository repository;

    @InjectMocks
    private FinancialProductService service;

    @Test
    void createDeposit_shouldCreateDepositProduct() {
        FinancialProduct.Deposit deposit = FinancialProduct.Deposit.create(
            "customer123", new BigDecimal("10000"), new BigDecimal("0.05"), 12);
        when(repository.save(any(FinancialProduct.Deposit.class))).thenReturn(deposit);

        FinancialProduct result = service.createDeposit("customer123", 
            new BigDecimal("10000"), new BigDecimal("0.05"), 12);

        assertNotNull(result);
        assertTrue(result instanceof FinancialProduct.Deposit);
    }

    @Test
    void createFund_shouldCreateFundProduct() {
        FinancialProduct.Fund fund = FinancialProduct.Fund.create(
            "customer123", new BigDecimal("5000"), "EQUITY");
        when(repository.save(any(FinancialProduct.Fund.class))).thenReturn(fund);

        FinancialProduct result = service.createFund("customer123", 
            new BigDecimal("5000"), "EQUITY");

        assertNotNull(result);
        assertTrue(result instanceof FinancialProduct.Fund);
    }

    @Test
    void calculateTotalValue_shouldSumAllProducts() {
        FinancialProduct.Deposit deposit = FinancialProduct.Deposit.create(
            "customer123", new BigDecimal("10000"), new BigDecimal("0.05"), 12);
        FinancialProduct.Fund fund = FinancialProduct.Fund.create(
            "customer123", new BigDecimal("5000"), "EQUITY");
        when(repository.findByCustomerId("customer123"))
            .thenReturn(List.of(deposit, fund));

        BigDecimal total = service.calculateTotalValue("customer123");

        assertEquals(new BigDecimal("15000"), total);
    }

    @Test
    void getDeposits_shouldFilterDepositsOnly() {
        FinancialProduct.Deposit deposit = FinancialProduct.Deposit.create(
            "customer123", new BigDecimal("10000"), new BigDecimal("0.05"), 12);
        FinancialProduct.Fund fund = FinancialProduct.Fund.create(
            "customer123", new BigDecimal("5000"), "EQUITY");
        when(repository.findByCustomerId("customer123"))
            .thenReturn(List.of(deposit, fund));

        List<FinancialProduct.Deposit> deposits = service.getDeposits("customer123");

        assertEquals(1, deposits.size());
        assertTrue(deposits.get(0) instanceof FinancialProduct.Deposit);
    }

    @Test
    void getFunds_shouldFilterFundsOnly() {
        FinancialProduct.Deposit deposit = FinancialProduct.Deposit.create(
            "customer123", new BigDecimal("10000"), new BigDecimal("0.05"), 12);
        FinancialProduct.Fund fund = FinancialProduct.Fund.create(
            "customer123", new BigDecimal("5000"), "EQUITY");
        when(repository.findByCustomerId("customer123"))
            .thenReturn(List.of(deposit, fund));

        List<FinancialProduct.Fund> funds = service.getFunds("customer123");

        assertEquals(1, funds.size());
        assertTrue(funds.get(0) instanceof FinancialProduct.Fund);
    }
}
