package com.course.mongodb.m09.repository;

import com.course.mongodb.m09.domain.Transfer;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TransferRepository extends MongoRepository<Transfer, String> {
    List<Transfer> findByFromAccount(String fromAccount);
    List<Transfer> findByToAccount(String toAccount);
    List<Transfer> findByStatus(String status);
}
