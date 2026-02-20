package com.course.mongodb.m13.service;

import com.course.mongodb.m13.domain.Customer360View;
import com.course.mongodb.m13.repository.Customer360ViewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Customer360ViewService {

    private final Customer360ViewRepository repository;

    public Customer360ViewService(Customer360ViewRepository repository) {
        this.repository = repository;
    }

    public Customer360View createCustomerView(String customerId, String name, String email) {
        Customer360View view = new Customer360View(customerId, name, email);
        return repository.save(view);
    }

    public Optional<Customer360View> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    public Optional<Customer360View> findById(String id) {
        return repository.findById(id);
    }

    public List<Customer360View> findAll() {
        return repository.findAll();
    }

    public Customer360View updateAccounts(String customerId, List<Customer360View.AccountSummary> accounts) {
        Customer360View view = repository.findByCustomerId(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        view.updateAccounts(accounts);
        return repository.save(view);
    }

    public Customer360View updateLoans(String customerId, List<Customer360View.LoanSummary> loans) {
        Customer360View view = repository.findByCustomerId(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        view.updateLoans(loans);
        return repository.save(view);
    }

    public void deleteByCustomerId(String customerId) {
        repository.findByCustomerId(customerId)
            .ifPresent(repository::delete);
    }
}
