package com.course.mongodb.m04.service;

import com.course.mongodb.m04.domain.Address;
import com.course.mongodb.m04.repository.AddressRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressService {

    private final AddressRepository repository;

    public AddressService(AddressRepository repository) {
        this.repository = repository;
    }

    public Address createAddress(String city, String district, String detail) {
        Address address = new Address(city, district, detail);
        return repository.save(address);
    }

    public List<Address> findAll() {
        return repository.findAll();
    }

    public Address findById(String id) {
        return repository.findById(id).orElse(null);
    }

    public List<Address> findByCity(String city) {
        return repository.findByCity(city);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
