package com.course.mongodb.m16.repository;

import com.course.mongodb.m16.domain.ChangeEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChangeEventRepository extends MongoRepository<ChangeEvent, String> {
    List<ChangeEvent> findByCollectionName(String collectionName);
    List<ChangeEvent> findByOperationType(ChangeEvent.OperationType operationType);
    List<ChangeEvent> findByProcessed(ChangeEvent.EventStatus processed);
    Optional<ChangeEvent> findByDocumentKey(String documentKey);
    List<ChangeEvent> findByDocumentKeyAndOperationType(String documentKey, ChangeEvent.OperationType operationType);
}
