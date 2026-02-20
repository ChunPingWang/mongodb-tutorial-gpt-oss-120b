package com.course.mongodb.m03.service;

import com.course.mongodb.m03.domain.Customer;
import com.course.mongodb.m03.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer createCustomer(String name, String email) {
        Customer customer = new Customer(name, email);
        return repository.save(customer);
    }

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public Customer findById(String id) {
        return repository.findById(id).orElse(null);
    }

    public Customer findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
