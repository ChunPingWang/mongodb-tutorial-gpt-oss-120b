package com.course.mongodb.m04.repository;

import com.course.mongodb.m04.domain.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AddressRepository extends MongoRepository<Address, String> {
    List<Address> findByCity(String city);
    List<Address> findByDistrict(String district);
}
